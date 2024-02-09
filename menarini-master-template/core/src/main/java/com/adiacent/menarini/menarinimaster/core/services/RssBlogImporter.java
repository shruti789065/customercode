package com.adiacent.menarini.menarinimaster.core.services;


import com.adiacent.menarini.menarinimaster.core.business.ContentFragmentApi;
import com.adiacent.menarini.menarinimaster.core.models.ContentFragmentBlogItemElements;
import com.adiacent.menarini.menarinimaster.core.models.ContentFragmentFactory;
import com.adiacent.menarini.menarinimaster.core.models.ContentFragmentM;
import com.adiacent.menarini.menarinimaster.core.models.rssblog.RssBlogModel;
import com.adiacent.menarini.menarinimaster.core.models.rssblog.RssBlogItemModel;
import com.adiacent.menarini.menarinimaster.core.utils.ModelUtils;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.dam.print.ids.StringConstants;
import com.day.cq.commons.Externalizer;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.mailer.MailService;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Designate(ocd = RssBlogImporter.Config.class)
@Component(
        service = RssBlogImporter.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "= MMT - Rss Blog importer",
                Constants.SERVICE_VENDOR + "=Adiacent"
        },
        immediate = true
)
public class RssBlogImporter implements Cloneable{
    private static final Logger logger = LoggerFactory.getLogger(RssBlogImporter.class);

    private static final String IMPORTER_USER = "Importer";

    private static final CharSequence BLOG_SEPARATOR = "|";

    private static String TAG_RESOURCE_TYPE = "cq/tagging/components/tag";
    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL)
    protected volatile MailService mailService;
    private Config serviceConfig;
    private List<String> errors;

    private List<String> blogItemCreated;

    private List<String> blogItemDiscarded;

    private transient ContentFragmentApi cfApi = null;

    @Activate
    @Modified
    protected void activate(final Config config) {
        logger.info("Activating MTT RSS Blog Importer");
        serviceConfig = config;

        logger.debug("Config rss blog item root path:{}", serviceConfig.getBlogItemRootPath());
        logger.debug("Config debug enabled:{}", serviceConfig.isDebugReportEnabled());
        logger.debug("Config debug report recipient{}", serviceConfig.getDebugReportRecipient());

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void start() {
        logger.info("**************** Start RSS Feed BLOG Importer by bean with id " + this.toString() +" **************************");


        if(StringUtils.isBlank(serviceConfig.getRssBlogUrl())){
            addErrors("Missing URL for RSS Feed BLOG ");
            if(errors != null && errors.size() > 0){
                sendResult();
                return;
            }
        }


        ResourceResolver resolver = getResourceResolver();
        Session session = resolver.adaptTo(Session.class);

        //Ottenimento dati dal feed e deserializzazione
        RssBlogModel data = getRssBlogData();
       
        if(data != null){

            //Elenco item del blog rss
            List<RssBlogItemModel> items = data.getChannel()!= null ? data.getChannel().getItems() : null;

            //- IMPORT TAG CATEGORIE
            if(!serviceConfig.isCategoryImportDisabled()){
                //TagManager tagManager = resolver.adaptTo(TagManager.class);
                // per ogni item del blog si recupera la categoria: se esiste il tag non si fa nulla, se non esiste lo si crea

                if(items!= null){
                    //si controlla se esiste la folder parent che contiene i tag della categoria
                    String parentTagName = ModelUtils.getNodeName(serviceConfig.getCategoryParentTag());
                    //check tag parent with name "parentTagName"
                    Resource parentRc = resolver.getResource(serviceConfig.getTagsRootPath()+"/"+parentTagName);
                    Node parentTagNode = parentRc != null ? parentRc.adaptTo(Node.class):null;

                    //Eventuale creazione deil tag "parent" se non esiste su crx ( menarini-berlin-blog-tag)
                    if(parentTagNode == null){
                        HashMap properties = new HashMap();
                        properties.put(JcrConstants.JCR_TITLE, serviceConfig.getCategoryParentTag());
                        properties.put(StringConstants.SLING_RESOURCE_TYPE, TAG_RESOURCE_TYPE);

                        Tag t = createTag(serviceConfig.getTagNamespace(), null, serviceConfig.getCategoryParentTag() ,properties, session );
                        if(t != null) {
                            parentTagNode = t.adaptTo(Node.class);



                        }

                    }
                    if(parentTagNode!= null){
                        Node finalParentTagNode = parentTagNode;
                        items.stream().forEach(item -> {

                            if(item!= null){
                                item.getCategories().forEach(c->{
                                    if(StringUtils.isBlank(c))
                                        return;
                                    HashMap properties = new HashMap();
                                    properties.put(JcrConstants.JCR_TITLE, c);
                                    properties.put(StringConstants.SLING_RESOURCE_TYPE, TAG_RESOURCE_TYPE);

                                    createTag(serviceConfig.getTagNamespace(), parentTagName , c, properties, session);
                                });
                            }
                        }
                        );
                    }



                }


            }
            //- FINE IMPORT TAG CATEGORIE


            //+IMPORT CONTENT FRAGMENT
            if(!serviceConfig.isBlogImportDisabled()){

                Externalizer externalizer = resolver.adaptTo(Externalizer.class);
                String hostname =  externalizer.authorLink(resolver, "/");

                if(StringUtils.isBlank(serviceConfig.getBlogItemRootPath())){
                    addErrors("Missing Blog item folder ");
                    if(errors != null && errors.size() > 0){
                        sendResult();
                        return;
                    }
                }

                ContentFragmentApi.Config apiConfig = new ContentFragmentApi.Config();
                apiConfig.setUsername(serviceConfig.getImportUsername());
                apiConfig.setPwd(serviceConfig.getImportPwd());
                cfApi = new ContentFragmentApi(apiConfig);

                //+CANCELLAZIONE MASSIVA DI TUTTI I CONTENT FRAGMENT
                //chidere luca se ok o se invece ci posson oesser edegli item inseriti a manella che poi la delete massiva toglierebbe definitivamente di mezzo

                //-FINE CANCELLAZIONE MASSIVA DI TUTTI I CONTENT FRAGMENT

                //si controlla se esite il path sotto al quale storicizzare i blog item ///content/dam/menarini-berlinchemie/area-content-fragments/blog-items

                Resource folder = resolver.getResource(serviceConfig.getBlogItemRootPath());
                if (folder == null) {

                    try {
                        ModelUtils.createNode(serviceConfig.getBlogItemRootPath(), "sling:Folder", session);
                        session.refresh(true);
                        session.save();
                    } catch (RepositoryException e) {
                        e.printStackTrace();
                        addErrors("Error creating blog item folder at  " + serviceConfig.getBlogItemRootPath());
                        if(errors != null && errors.size() > 0){
                            sendResult();
                            return;
                        }
                    }

                }




                if(items!= null){
                    items.stream().limit(6).forEach(item -> {
                        ContentFragmentM<ContentFragmentBlogItemElements> cf = ContentFragmentFactory.generate("blog", item, resolver, serviceConfig.getTagNamespace(), ModelUtils.getNodeName(serviceConfig.getCategoryParentTag()));
                        if(cf != null){
                            String cfName = ModelUtils.getNodeName(cf.getProperties().getTitle());// usare l'id per nome nodo!!!!!se possibile'
                            //se esiste aggiorno,                         //se non esiste creo
                            String  cfResourcePath = serviceConfig.getBlogItemRootPath()+"/"+cfName;
                            //Get the resource of content fragment as below.
                            Resource fragmentResource = resolver.getResource(cfResourcePath);
                            ContentFragment fragment = null;
                            if(fragmentResource != null)
                                fragment = fragmentResource.adaptTo(ContentFragment.class);

                            //Node n = rCF != null ? rCF.adaptTo(Node.class) : null;
                            String targetPath = StringUtils.replace(cfResourcePath, serviceConfig.getDamRootPath() + "/", "");
                            boolean operationSuccess = storeContentFragment(fragment==null,  hostname, targetPath, cf);
                            if(operationSuccess) {
                                try {
                                    session.refresh(true);
                                } catch (RepositoryException e) {
                                    e.printStackTrace();
                                    addErrors(e.getMessage());
                                    return;
                                }
                                //associo  un tag per ogni categoria del blog item
                                // 1.per ogni categoria presente nel blogitem recupero il tagID
                                if(item.getCategories() != null){
                                    List<Tag> tags = item.getCategories().stream()
                                            .map(c->{ Tag t = findTag(serviceConfig.getTagNamespace(),
                                                    ModelUtils.getNodeName(serviceConfig.getCategoryParentTag()),
                                                    ModelUtils.getNodeName(c));
                                                return t != null ? t: null;})
                                            .filter(e->e!=null)
                                            .collect(Collectors.toList());

                                    if(tags != null && tags.size() >0) {
                                        //2.si recuperano tutti gli elementi di tipo Tag sulla base del TagId
                                        Tag[] allTags = tags.toArray(new Tag[tags.size()]);
                                        //3.si recupera il content fragment appena creato/modificato
                                        Resource masterNodeResource = resolver.getResource(cfResourcePath+"/jcr:content/data/master");
                                        //4. si associano i tags al content fragment
                                        if(masterNodeResource != null)
                                            getTagManager().setTags(masterNodeResource, allTags);
                                        else
                                            addErrors("Impostare cq:tags per "+cfResourcePath);
                                    }
                                }


                            }

                        }

                    });

                }




            }


            //- FINE IMPORT CONTENT FRAGMENT

            /*
            //recupero immagini delle news
            List<RssItemModel> items = data.getChannel().getItems();

            if(items!= null){





                String newsIds = items.stream().map(i-> {
                    if(i != null){
                        //Si controlla l'eventuale pregressa importazione della news
                        if(StringUtils.contains(previousImportedNewsIds.get(), i.getIdentifier())){
                            addNewsDiscarded(i.getIdentifier() + " - " + i.getTitle());
                            return null;
                        }

                        try {
                            String imageDAMPath = null;
                            //Si controlla se la news nel feed ha un'immagine
                            if(i.getEnclosure() != null && StringUtils.isNotBlank(i.getEnclosure().getUrl())) {
                                //salvataggio immagine del feed nel DAM
                                imageDAMPath = addImage(resolver, session, i);
                                if(StringUtils.isBlank(imageDAMPath))
                                    return null;//c'è stato un errore nel recupero dell'immagine o nel suo salvataggio su dam
                            }

                            //creazione pagina news
                            Page p = createNewsPage(resolver, session, i, imageDAMPath, referencePageTitle.get());
                            if(p != null) {


                                addNewsCreated(p.getPath());
                                return  i.getIdentifier();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            addErrors("Exception managing news " + i.getTitle() + " : " + e.getMessage());
                            return null;
                        }
                    }

                   return null;
                }).filter(entry->entry!=null).collect(Collectors.joining(NEWS_SEPARATOR));

                //scrittura proprietà di log
                try {
                    if(StringUtils.isNotBlank(newsIds)) {
                        newsRootJcrNode.setProperty("rssNewsImported", previousImportedNewsIds.get() + NEWS_SEPARATOR + newsIds);
                        session.save();
                    }
                } catch (RepositoryException e) {
                    e.printStackTrace();
                    addErrors("Error setting dnn news id in page property rssNewsImported");
                }



            }
            */
        }

        sendResult();

        logger.info("**************** End RSS Feed BLOG Importer by bean with id " + this.toString() +" **************************");
    }



    public boolean storeContentFragment(boolean isNew, String hostname, String targetPath,  ContentFragmentM cf) {
        boolean res = false;
        if(StringUtils.isNotBlank(hostname)  &&  cf != null) {
            if (isNew)
                res = cfApi.create(hostname, targetPath, cf, ContentFragmentBlogItemElements.class);
            else
                res = cfApi.put(hostname, targetPath, cf, ContentFragmentBlogItemElements.class);
        }
        return res;
    }

    private Tag findTag(String namespace, String nestedTagPath, String tagName) {
        Tag tag = null;
        if(StringUtils.isNotBlank(tagName))
            tag = getTagManager().resolve(namespace+(StringUtils.isNotBlank(nestedTagPath) ? nestedTagPath+"/":"")+tagName);
        return tag;
    }

    private Tag createTag(String namespace, String nestedTagPath, String title, HashMap properties, Session session) {

        logger.info("Handle " + title + " tag");
        if(StringUtils.isNotBlank(title)){
            String tagName = ModelUtils.getNodeName(title);
            Tag tag = findTag(namespace, nestedTagPath, tagName);
            if(tag == null){
                try{
                    tag = getTagManager().createTag(namespace+(StringUtils.isNotBlank(nestedTagPath) ? nestedTagPath+"/":"")+tagName, title, null,true);
                    if(tag == null){
                        addErrors("Error in creating tag " + title);
                    }
                    else
                        session.save();
                } catch (InvalidTagFormatException | RepositoryException e) {
                    e.printStackTrace();
                    addErrors("Error in creating tag " + tag);
                }
            }
            return tag;
        }
        return null;
    }

    //Deserializzazione news da feed DNN
    public RssBlogModel getRssBlogData() {
        URL url = null;
        try {
            url = new URL(serviceConfig.getRssBlogUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            InputStream inputStream = url.openConnection().getInputStream();

            StringBuilder textBuilder = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader
                    (inputStream, StandardCharsets.UTF_8))) {
                int c = 0;
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }
            }
            //logger.info(textBuilder.toString());
            String txt = textBuilder.toString();
            txt = txt.substring(txt.indexOf("<rss"));
            txt = txt.replace("<dc:creator>","<dc_creator>");
            txt = txt.replace("</dc:creator>","</dc_creator>");
            XmlMapper xmlMapper = new XmlMapper();
            return  xmlMapper.readValue(
                    txt,
                    RssBlogModel.class);

        } catch (MalformedURLException e) {
            addErrors(e.getMessage());
            return null;
        } catch (ProtocolException e) {
            addErrors(e.getMessage());
            return null;
        } catch (IOException e) {
            addErrors(e.getMessage());
            return null;
        }
    }

    protected MailService getMailService() {
        return mailService;
    }
    public List<String> getErrors() {
        if(errors == null)
            return null;
        String[] array = errors.toArray(new String[errors.size()]);
        String[] clone = array.clone();
        return Arrays.asList(clone);
    }
    public void setErrors(List<String> errors) {
        if(errors== null)
            this.errors = null;
        else {
            String[] array = errors.toArray(new String[errors.size()]);
            String[] clone = array.clone();
            this.errors = Arrays.asList(clone);
        }
    }

    public void addErrors(String txt){
        if(StringUtils.isNotBlank(txt)) {
            if (this.getErrors() == null)
                this.errors = new ArrayList<String>();
            this.errors.add(txt);
        }
    }

    public List<String> getBlogItemCreated() {
        if(blogItemCreated == null)
            return null;
        String[] array = blogItemCreated.toArray(new String[blogItemCreated.size()]);
        String[] clone = array.clone();
        return Arrays.asList(clone);
    }
    public void setBlogItemCreated(List<String> blogItemCreated) {
        if(blogItemCreated== null)
            this.blogItemCreated = null;
        else {
            String[] array = blogItemCreated.toArray(new String[blogItemCreated.size()]);
            String[] clone = array.clone();
            this.blogItemCreated = Arrays.asList(clone);
        }
    }

    public void addBlogItemCreated(String txt){
        if(StringUtils.isNotBlank(txt)) {
            if (this.getBlogItemCreated() == null)
                this.blogItemCreated = new ArrayList<String>();
            this.blogItemCreated.add(txt);
        }
    }

    public List<String> getBlogItemDiscarded() {
        if(blogItemDiscarded == null)
            return null;
        String[] array = blogItemDiscarded.toArray(new String[blogItemDiscarded.size()]);
        String[] clone = array.clone();
        return Arrays.asList(clone);
    }
    public void setBlogItemDiscarded(List<String> blogItemDiscarded) {
        if(blogItemDiscarded== null)
            this.blogItemDiscarded = null;
        else {
            String[] array = blogItemDiscarded.toArray(new String[blogItemDiscarded.size()]);
            String[] clone = array.clone();
            this.blogItemDiscarded = Arrays.asList(clone);
        }
    }

    public void addNewsDiscarded(String txt){
        if(StringUtils.isNotBlank(txt)) {
            if (this.getBlogItemDiscarded() == null)
                this.blogItemDiscarded = new ArrayList<String>();
            this.blogItemDiscarded.add(txt);
        }
    }

    protected void sendResult() {
        String result = "Esito operazione  : Ok";
        if(errors != null  && errors.size() > 0){
            String resultKO = errors.stream().collect(Collectors.joining("\n"));
            result="Esito operazione : Ko\n\n"+resultKO;
        }

        if(blogItemCreated != null  && blogItemCreated.size() > 0){
            String resultOK = blogItemCreated.stream().collect(Collectors.joining("\n"));
            result+="\n\nitem create:"+ blogItemCreated.size()+" \n\n"+resultOK;
        }

        if(blogItemDiscarded != null  && blogItemDiscarded.size() > 0){
            String resultDis = blogItemDiscarded.stream().collect(Collectors.joining("\n"));
            result+="\n\nitem scartate :"+ blogItemDiscarded.size()+"\n\n"+resultDis;
        }


        Email mail = new HtmlEmail();
        try {
            mail.setMsg(result);
            mail.setSubject("Fine importazione  RSS Feed blog ");
            mail.addTo(serviceConfig.getDebugReportRecipient());
            for (String copyto : serviceConfig.getDebugReportRecipientCopyTo()) {
                if (StringUtils.isNotEmpty(copyto)) {
                    logger.info("sendEmail Send Copy To " + copyto);
                    mail.addCc(copyto);
                }
            }
            mail.setCharset("UTF-8");
            getMailService().send(mail);
        } catch (Exception e) {
            logger.error("Error sending report email", e);

        }



    }

    public ResourceResolver getResourceResolver() {
        ResourceResolver resolver = null;
        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, com.adiacent.menarini.menarinimaster.core.utils.Constants.SERVICE_NAME);
        try {
            resolver = resolverFactory.getServiceResourceResolver(param);
        } catch (Exception e) {
            logger.error("Error retrieving resolver with system user", e);
        }
        return resolver;
    }

    public TagManager getTagManager() {
        if(getResourceResolver() != null)
            return getResourceResolver().adaptTo(TagManager.class);
        return null;
    }


    @ObjectClassDefinition(name = "RSS Blog Importer", description = "RSS Blog  Importer")
    public static @interface Config {

        @AttributeDefinition(name = "Disable Blog Import", description = "Disable Blog Import")
        boolean isBlogImportDisabled() default true;

        @AttributeDefinition(name = "Blog item Root Path", description = "Imported blog item root path")
        String getBlogItemRootPath() default "/content/dam/menarini-berlinchemie/area-content-fragments/blog-items";

        @AttributeDefinition(name = "Rss feed url", description = "RSS Feed url")
        String getRssBlogUrl() default "https://menariniblog.com/feed";

        @AttributeDefinition(name = "Disable category tag import", description = "Disable category tag import")
        boolean isCategoryImportDisabled() default true;

        @AttributeDefinition(name = "Category parent tag", description = "Category parent tag")
        String getCategoryParentTag() default "Menarini-berlin Blog Tag";

        @AttributeDefinition(name = "Menarini berlin chemie root tag", description = "Menarini berlin chemie root tag")
        String getTagsRootPath() default "/content/cq:tags/menarini-berlinchemie/";

        @AttributeDefinition(name = "Menarini berlin chemie tag namespace", description = "Menarini berlin chemie tag namespace")
        String getTagNamespace() default "menarini-berlinchemie:";

        @AttributeDefinition(name = "DAM Root Path", description = "getDamRootPath")
        String getDamRootPath() default "/content/dam";

        @AttributeDefinition(name = "Aem User for importer", description = "Aem User for importer")
        String getImportUsername() default "";
        @AttributeDefinition(name = "Aem User PWD for importer", description = "Aem User PWD for importer")
        String getImportPwd() default "";

        @AttributeDefinition(name = "Debug Report Enabled", description = "Enable debug report email")
        boolean isDebugReportEnabled() default false;

        @AttributeDefinition(name = "Debug Report Subject", description = "Debug email report subject")
        String getDebugReportSubject() default "[MTT] RSS Blog Import Procedure";

        @AttributeDefinition(name = "Debug Report Recipient", description = "Debug email report recipient")
        String getDebugReportRecipient() default "";

        @AttributeDefinition(name = "Debug Report Recipient Copyto", description = "Debug email report recipient Copyto")
        String[] getDebugReportRecipientCopyTo() default {};



    }
}
