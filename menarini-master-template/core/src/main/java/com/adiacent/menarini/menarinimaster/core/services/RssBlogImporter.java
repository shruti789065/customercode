package com.adiacent.menarini.menarinimaster.core.services;


import com.adiacent.menarini.menarinimaster.core.business.ContentFragmentApi;
import com.adiacent.menarini.menarinimaster.core.models.contentfragments.ContentFragmentBlogItemElements;
import com.adiacent.menarini.menarinimaster.core.models.contentfragments.ContentFragmentFactory;
import com.adiacent.menarini.menarinimaster.core.models.contentfragments.ContentFragmentM;


import com.adiacent.menarini.menarinimaster.core.models.rss.BlogItemModel;
import com.adiacent.menarini.menarinimaster.core.models.rss.RssModel;
import com.adiacent.menarini.menarinimaster.core.utils.ModelUtils;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.dam.print.ids.StringConstants;
import com.day.cq.commons.Externalizer;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.mailer.MailService;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.fasterxml.jackson.core.type.TypeReference;
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

    private static String TAG_RESOURCE_TYPE = "cq/tagging/components/tag";
    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL)
    protected volatile MailService mailService;
    private Config serviceConfig;
    private List<String> errors;

    private List<String> importedItems;

    private List<String> importedTags;

    private List<String> cancelledItems;

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
        RssModel data = getRssBlogData();
        if(errors != null && errors.size() > 0){
            sendResult();
            return;
        }


        if(data != null){

            //Elenco item del blog rss
            List<BlogItemModel> items = data.getChannel()!= null ? data.getChannel().getItems() : null;

            if(items == null || items.size() == 0){
                addErrors("No blog items to import. Procedure stopped ");
                if(errors != null && errors.size() > 0){
                    sendResult();
                    return;
                }
            }

            //- IMPORT TAG CATEGORIE
            if(!serviceConfig.isCategoryImportDisabled()){

                logger.info("**************** Category Tag import started **************************");

                if(StringUtils.isBlank(serviceConfig.getTagsRootPath())){
                    addErrors("Missing configuration path of root TAGS ");
                    if(errors != null && errors.size() > 0){
                        sendResult();
                        return;
                    }
                }

                if( resolver.getResource(serviceConfig.getTagsRootPath()) == null ){
                    addErrors("Missing resource node at  " + serviceConfig.getTagsRootPath());
                    if(errors != null && errors.size() > 0){
                        sendResult();
                        return;
                    }
                }

                // per ogni item del blog si recupera la categoria: se esiste il tag non si fa nulla, se non esiste lo si crea
                if(items!= null){
                    Node parentTagNode = null;
                    String parentTagName = null;
                    if(StringUtils.isNotBlank(serviceConfig.getCategoryParentTag())){
                        //si controlla se esiste la folder parent che contiene i tag della categoria
                        parentTagName = ModelUtils.getNodeName(serviceConfig.getCategoryParentTag());

                        //check tag parent with name "parentTagName"
                        Resource parentRc = resolver.getResource(StringUtils.appendIfMissing(serviceConfig.getTagsRootPath(),"/")+parentTagName );
                        parentTagNode = parentRc != null ? parentRc.adaptTo(Node.class):null;
                        //Eventuale creazione dei tag "parent" se non esiste su crx ( menarini-berlin-blog-tag)
                        if(parentTagNode == null){
                            HashMap properties = new HashMap();
                            properties.put(JcrConstants.JCR_TITLE, serviceConfig.getCategoryParentTag());
                            properties.put(StringConstants.SLING_RESOURCE_TYPE, TAG_RESOURCE_TYPE);

                            Tag t = null;
                            try {
                                t = ModelUtils.createTag(serviceConfig.getTagNamespace(), null, serviceConfig.getCategoryParentTag() ,properties, session, resolver );
                            } catch (Exception e) {
                                e.printStackTrace();
                                addErrors(e.getMessage());
                            }
                            if(t != null) {
                                parentTagNode = t.adaptTo(Node.class);
                            }

                        }
                    }

                    //Ci sono stati errori in fase di creazione del tag parent per le categorie
                    if(errors != null && errors.size() > 0){
                        sendResult();
                        return;
                    }

                    if(parentTagNode!= null){
                        String finalParentTagName = parentTagName;
                        items.stream().forEach(item -> {

                            if(item!= null){

                                item.getCategories().forEach(c->{
                                    if(StringUtils.isBlank(c))
                                        return;
                                    HashMap properties = new HashMap();
                                    properties.put(JcrConstants.JCR_TITLE, c);
                                    properties.put(StringConstants.SLING_RESOURCE_TYPE, TAG_RESOURCE_TYPE);

                                    try {
                                        Tag ctag = ModelUtils.createTag(serviceConfig.getTagNamespace(), finalParentTagName, c, properties, session, resolver);
                                        if(ctag != null)
                                            addImportedTag(ctag.getTagID());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        addErrors(e.getMessage());
                                    }
                                });
                            }
                        }
                        );
                    }



                }

                logger.info("**************** Category Tag import ended **************************");
            }
            //- FINE IMPORT TAG CATEGORIE


            //+IMPORT CONTENT FRAGMENT
            if(!serviceConfig.isBlogImportDisabled()){

                logger.info("**************** Blog Item import started **************************");

                if(StringUtils.isBlank(serviceConfig.getImportUsername()) ||
                        StringUtils.isBlank(serviceConfig.getImportPwd())){
                    addErrors("Missing credential for connection to Asset REST API");
                    if(errors != null && errors.size() > 0){
                        sendResult();
                        return;
                    }
                }

                if(StringUtils.isBlank(serviceConfig.getContentFragmentModel())){
                    addErrors("Missing path of content fragment model");
                    if(errors != null && errors.size() > 0){
                        sendResult();
                        return;
                    }
                }

                if(StringUtils.isBlank(serviceConfig.getDamRootPath())){
                    addErrors("Missing path of AEM DAM");
                    if(errors != null && errors.size() > 0){
                        sendResult();
                        return;
                    }
                }

                Externalizer externalizer = resolver.adaptTo(Externalizer.class);
                String hostname =  externalizer.authorLink(resolver, "/");

                ContentFragmentApi.Config apiConfig = new ContentFragmentApi.Config();
                apiConfig.setUsername(serviceConfig.getImportUsername());
                apiConfig.setPwd(serviceConfig.getImportPwd());
                cfApi = new ContentFragmentApi(apiConfig);

                //se impostato, si controlla l'esistenza della folder/nodo sotto la quale storicizzare i blog item (es: /content/dam/menarini-berlinchemie/area-content-fragments/blog-items)
                if(StringUtils.isNotBlank(serviceConfig.getBlogItemRootPath())){
                    Resource folder = resolver.getResource(serviceConfig.getBlogItemRootPath());
                    if (folder == null) {
                        try {
                            generateNode(serviceConfig.getBlogItemRootPath(), "sling:Folder", session);
                            session.refresh(true);
                            session.save();
                        } catch (RepositoryException e) {
                            e.printStackTrace();
                            addErrors("Error creating node at  " + serviceConfig.getBlogItemRootPath());
                            if(errors != null && errors.size() > 0){
                                sendResult();
                                return;
                            }
                        }

                    }
                }

                List<String> importedCFs = new ArrayList<String>(); //contiene i nomi dei nodi dei content fragment oggetto di importazione
                if(items!= null){
                    items.stream().forEach(item -> {
                        if(item != null) {
                            ContentFragmentM<ContentFragmentBlogItemElements> cf = ContentFragmentFactory.generate("blog", item, resolver, serviceConfig.getTagNamespace(), ModelUtils.getNodeName(serviceConfig.getCategoryParentTag()), serviceConfig.getContentFragmentModel());
                            if (cf != null) {
                                String cfName = calculateBlogItemContentFragmentName(cf);
                                //se esiste il content fragment con quel nome, lo aggiorno, altrimenti si crea
                                String cfResourcePath = serviceConfig.getBlogItemRootPath() + "/" + cfName;
                                //Get the resource of content fragment as below.
                                ContentFragment fragment = getBlogItemContentFragmentByPath(cfResourcePath, resolver);

                                String targetContentFragmentPath = StringUtils.replace(cfResourcePath, StringUtils.appendIfMissing(serviceConfig.getDamRootPath(),  "/"), "");
                                boolean operationSuccess = storeContentFragment(fragment == null, hostname, targetContentFragmentPath, cf);
                                if (operationSuccess) {
                                    try {

                                        importedCFs.add(cfName);
                                        session.save();
                                        addImportedItem(item.getTitle());
                                    } catch (RepositoryException e) {
                                        e.printStackTrace();
                                        addErrors(e.getMessage());
                                        return;
                                    }
                                    //associo  un tag per ogni categoria del blog item
                                    // 1.per ogni categoria presente nel blogitem recupero il tagID
                                    if (item.getCategories() != null) {
                                        List<Tag> tags = item.getCategories().stream()
                                                .map(c -> {
                                                    Tag t = ModelUtils.findTag(serviceConfig.getTagNamespace(),
                                                            ModelUtils.getNodeName(serviceConfig.getCategoryParentTag()),
                                                            ModelUtils.getNodeName(c), resolver);
                                                    return t != null ? t : null;
                                                })
                                                .filter(e -> e != null)
                                                .collect(Collectors.toList());

                                        if (tags != null && tags.size() > 0) {
                                            //2.si recuperano tutti gli elementi di tipo Tag sulla base del TagId
                                            Tag[] allTags = tags.toArray(new Tag[tags.size()]);
                                            //3.si recupera il content fragment appena creato/modificato
                                            Resource masterNodeResource = resolver.getResource(cfResourcePath + "/jcr:content/data/master");
                                            //4. si associano i tags al content fragment
                                            if (masterNodeResource != null)
                                                getTagManager().setTags(masterNodeResource, allTags);
                                            else
                                                addErrors("Impostare cq:tags per " + cfResourcePath);
                                        }
                                    }


                                }

                            }
                        }
                    });
                    logger.info("**************** Blog Item Tag import ended **************************");
                }
                //- FINE IMPORT CONTENT FRAGMENT

                //+CANCELLAZIONE CONTENT FRAGMENT NON OGGETTO DI IMPORTAZIONE CORRENTE
                if(!serviceConfig.isBlogItemDeletionDisabled()){

                    logger.info("**************** Blog Item Cancellation started **************************");

                    //recupero id dei content fragment nel crx
                    List<ContentFragment> cfList = listRssBlogItem(resolver);
                    //si cancellano i CF NON oggetto di importazione corrente
                    if(cfList!=null){
                        cfList.stream().forEach(cfToCheck->{
                            String cfname = cfToCheck.getName();
                            if( importedCFs == null || importedCFs.size() == 0  || !importedCFs.contains(cfname)) {
                                String cfResourcePath = StringUtils.appendIfMissing(serviceConfig.getBlogItemRootPath(),"/")+cfname;
                                String targetPath = StringUtils.replace(cfResourcePath, StringUtils.appendIfMissing(serviceConfig.getDamRootPath(), "/"), "");
                                deleteContentFragment(hostname, targetPath);
                                addCancelledItem(cfname);
                            }
                        });
                    }
                    logger.info("**************** Blog Item Cancellation ended **************************");
                }

                //-FINE CANCELLAZIONE   CONTENT FRAGMENT

            }

        }

        sendResult();

        logger.info("**************** End RSS Feed BLOG Importer by bean with id " + this.toString() +" **************************");
    }

    public void deleteContentFragment(String hostname, String targetPath) {
        if(StringUtils.isNotBlank(hostname)  && StringUtils.isNotBlank(targetPath))
            cfApi.delete(hostname, targetPath);
    }

    public void generateNode(String blogItemRootPath, String s, Session session) throws RepositoryException {
        ModelUtils.createNode(serviceConfig.getBlogItemRootPath(), "sling:Folder", session);
    }

    private ContentFragment getBlogItemContentFragmentByPath(String cfResourcePath, ResourceResolver resolver) {
        Resource resource = resolver.getResource(cfResourcePath);
        ContentFragment fragment = null;
        if (resource != null)
            fragment = resource.adaptTo(ContentFragment.class);
        return fragment;
    }

    private String calculateBlogItemContentFragmentName(ContentFragmentM<ContentFragmentBlogItemElements> cf) {
        if(cf == null)
            return null;
        return ModelUtils.getNodeName(cf.getProperties().getTitle());// usare l'id per nome nodo!!!!!se possibile'
    }

    private List<ContentFragment> listRssBlogItem(ResourceResolver resolver) {

        List<ContentFragment> res = new ArrayList<ContentFragment>();
        Map<String, String> map = new HashMap<>();
        map.put("type", "dam:Asset");
        map.put("path", serviceConfig.getBlogItemRootPath());
        map.put("First_property", "jcr:content/contentFragment");
        map.put("First_property.value", "true");
        map.put("Second_property", "jcr:content/data/cq:model");
        map.put("Second_property.value", serviceConfig.getContentFragmentModel());
        map.put("property.and", "true");
        map.put("p.limit", "-1");

        QueryBuilder queryBuilder = resolver.adaptTo(QueryBuilder.class);
        Query query = queryBuilder.createQuery(PredicateGroup.create(map),  resolver.adaptTo(Session.class));
        final SearchResult result = query.getResult();
        for (Hit hit : result.getHits()) {
            ContentFragment cf = null;
            try {
                cf = hit.getResource().adaptTo(ContentFragment.class);
                res.add(cf);
            } catch (RepositoryException e) {
                addErrors(e.getMessage());
                e.printStackTrace();
                return null;
            }
            /* String contentFragmentName = cf.getName();
            Iterator<ContentElement> contentElement = cf.getElements();
            while (contentElement.hasNext()) {
                ContentElement contentElementObject = contentElement.next();
                String tagElement = contentElementObject.getName().toString();
                String elementContent = contentElementObject.getContent();
            }
            */
        }
        return res;
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
/*
    private Tag findTag(String namespace, String nestedTagPath, String tagName) {
        Tag tag = null;
        if(StringUtils.isNotBlank(tagName))
            tag = getTagManager().resolve( (StringUtils.isNotBlank(namespace) ? namespace:"" ) +
                    (StringUtils.isNotBlank(nestedTagPath) ? nestedTagPath+"/":"") +
                    tagName);
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
*/
    //Deserializzazione news da feed DNN
    public RssModel getRssBlogData() {
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
                    new TypeReference<RssModel<BlogItemModel>>(){});

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

    public List<String> getImportedItems() {
        if(importedItems == null)
            return null;
        String[] array = importedItems.toArray(new String[importedItems.size()]);
        String[] clone = array.clone();
        return Arrays.asList(clone);
    }
    public void setImportedItems(List<String> items) {
        if(items == null)
            this.importedItems = null;
        else {
            String[] array = importedItems.toArray(new String[importedItems.size()]);
            String[] clone = array.clone();
            this.importedItems = Arrays.asList(clone);
        }
    }

    public void addImportedItem(String txt){
        if(StringUtils.isNotBlank(txt)) {
            if (this.getImportedItems() == null)
                this.importedItems = new ArrayList<String>();
            this.importedItems.add(txt);
        }
    }

    public List<String> getImportedTags() {
        if(importedTags == null)
            return null;
        String[] array = importedTags.toArray(new String[importedTags.size()]);
        String[] clone = array.clone();
        return Arrays.asList(clone);
    }
    public void setImportedTags(List<String> items) {
        if(items == null)
            this.importedTags = null;
        else {
            String[] array = importedItems.toArray(new String[importedTags.size()]);
            String[] clone = array.clone();
            this.importedTags = Arrays.asList(clone);
        }
    }

    public void addImportedTag(String txt){
        if(StringUtils.isNotBlank(txt)) {
            if (this.getImportedTags() == null)
                this.importedTags = new ArrayList<String>();
            this.importedTags.add(txt);
        }
    }

    public List<String> getCancelledItems() {
        if(cancelledItems == null)
            return null;
        String[] array = cancelledItems.toArray(new String[cancelledItems.size()]);
        String[] clone = array.clone();
        return Arrays.asList(clone);
    }
    public void setCancelledItems(List<String> items) {
        if(items== null)
            this.cancelledItems = null;
        else {
            String[] array = cancelledItems.toArray(new String[items.size()]);
            String[] clone = array.clone();
            this.cancelledItems = Arrays.asList(clone);
        }
    }

    public void addCancelledItem(String txt){
        if(StringUtils.isNotBlank(txt)) {
            if (this.cancelledItems == null)
                this.cancelledItems = new ArrayList<String>();
            this.cancelledItems.add(txt);
        }
    }

    protected void sendResult() {
        if(!serviceConfig.isDebugReportEnabled())
            return;

        String result = "Esito operazione  : Ok";
        if(errors != null  && errors.size() > 0){
            String resultKO = errors.stream().collect(Collectors.joining("\n"));
            result="Esito operazione : Ko\n\n"+resultKO;
        }

        if(importedItems != null  && importedItems.size() > 0){
            String resultOK = importedItems.stream().collect(Collectors.joining("\n"));
            result+="\n\nBlog items created:"+ importedItems.size()+" \n\n"+resultOK;
        }

        if(importedTags != null  && importedTags.size() > 0){
            String resultOK = importedTags.stream().collect(Collectors.joining("\n"));
            result+="\n\nCategory Tags created:"+ importedTags.size()+" \n\n"+resultOK;
        }

        if(cancelledItems != null  && cancelledItems.size() > 0){
            String resultDis = cancelledItems.stream().collect(Collectors.joining("\n"));
            result+="\n\nContent fragments cancelled :"+ cancelledItems.size()+"\n\n"+resultDis;
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
        String getBlogItemRootPath() default "";

        @AttributeDefinition(name = "Rss feed url", description = "RSS Feed url")
        String getRssBlogUrl() default "";

        @AttributeDefinition(name = "Disable category tag import", description = "Disable category tag import")
        boolean isCategoryImportDisabled() default true;

        @AttributeDefinition(name = "Delete old blog items before new import", description = "Delete old blog items before new import")
        boolean isBlogItemDeletionDisabled() default true;

        @AttributeDefinition(name = "Content fragment model", description = "Path to the content fragment model")
        String getContentFragmentModel() default "";

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
