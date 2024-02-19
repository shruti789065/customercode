package com.adiacent.menarini.menarinimaster.core.services;


import com.adiacent.menarini.menarinimaster.core.models.rssnews.RssItemModel;
import com.adiacent.menarini.menarinimaster.core.models.rssnews.RssNewModel;

import com.adiacent.menarini.menarinimaster.core.utils.ImageUtils;
import com.adiacent.menarini.menarinimaster.core.utils.ModelUtils;


import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.mailer.MailService;

import com.day.cq.wcm.api.Page;


import com.day.cq.wcm.api.WCMException;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.apache.sling.api.resource.LoginException;
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

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.adiacent.menarini.menarinimaster.core.models.NewsListModel.NEWSDATA_RESOURCE_TYPE;

@Designate(ocd = RssNewsImporter.Config.class)
@Component(
        service = RssNewsImporter.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "= MMT - Rss News importer",
                Constants.SERVICE_VENDOR + "=Adiacent"
        },
        immediate = true
)
public class RssNewsImporter implements Cloneable{
    private static final Logger logger = LoggerFactory.getLogger(RssNewsImporter.class);

    private static final String IMPORTER_USER = "Importer";

    private static final String DEFAULT_IMAGE_TYPE = "main";
    private static final String PAGE_RESOURCE_TYPE = "menarinimaster/components/page";
    private static final CharSequence NEWS_SEPARATOR = "|";

    public static final String NEWSDATA_RESOURCE_TYPE = "menarinimaster/components/news_data";
    public static final String INTERNAL_HEADER_RESOURCE_TYPE = "menarinimaster/components/internalheader";
    private static final String NEWSDATA_DATE_PROPERTY_NAME = "newsDate";
    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL)
    protected volatile MailService mailService;
    private Config serviceConfig;
    private List<String> errors;
    private List<String> newsCreated;

    private List<String> newsDiscarded;

    @Reference
    private WorkflowService workflowService;

    @Activate
    @Modified
    protected void activate(final Config config) {
        logger.info("Activating MTT RSS News Importer");
        serviceConfig = config;

        logger.debug("Config rss news root path:{}", serviceConfig.getNewsRootPath());
        logger.debug("Config debug enabled:{}", serviceConfig.isDebugReportEnabled());
        logger.debug("Config debug report recipient{}", serviceConfig.getDebugReportRecipient());

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void start() {
        logger.info("**************** Start RSS Feed NEWS Importer by bean with id " + this.toString() +" **************************");
        //Si controlla che l'importer sia abilitato all'esecuzione

        if(serviceConfig.isNewsImportDisabled()){
            addErrors("Procedure not enabled");
            if(errors != null && errors.size() > 0){
                sendResult();
                return;
            }
        }

        if(StringUtils.isBlank(serviceConfig.getRssFeedUrl())){
            addErrors("Missing URL for RSS Feed NEWS ");
            if(errors != null && errors.size() > 0){
                sendResult();
                return;
            }
        }


        ResourceResolver resolver = getResourceResolver();
        Session session = resolver.adaptTo(Session.class);

        WorkflowModel wfModel = null;
        WorkflowSession wfSession =  getWorkflowSession(session);
        if(!serviceConfig.isApprovalWorkflowDisabled() ){
            if(StringUtils.isBlank(serviceConfig.getApprovalWorkflowModelPath())){
                addErrors("Missing approval workflow model path  for RSS Feed NEWS ");
                if(errors != null && errors.size() > 0){
                    sendResult();
                    return;
                }
            }

            try {
                wfModel = wfSession.getModel(serviceConfig.getApprovalWorkflowModelPath());
            } catch (WorkflowException e) {
                addErrors("Approval workflow model doesn't exist for RSS Feed NEWS " + serviceConfig.getApprovalWorkflowModelPath() );
                if(errors != null && errors.size() > 0){
                    sendResult();
                    return;
                }
            }
        }


        //Ottenimento dati dal feed e deserializzazione
        RssNewModel data = getRssNewsData();
       
        if(data != null){
            //recupero immagini delle news
            List<RssItemModel> items = data.getChannel().getItems();

            if(items!= null){

                //recupero proprietà di log relativa alle news importate in precedenza
                AtomicReference<String> previousImportedNewsIds = new AtomicReference<>("");
                Node newsRootJcrNode = null;
                String newsRootTitle = null;
                Page newsRootPage = (resolver.getResource(serviceConfig.getNewsRootPath())).adaptTo(Page.class);
                if(newsRootPage!= null){
                    newsRootJcrNode = newsRootPage.getContentResource().adaptTo(Node.class);
                    try {
                        if(newsRootJcrNode.hasProperty("rssNewsImported"))
                            previousImportedNewsIds.set(newsRootJcrNode.getProperty("rssNewsImported").getString());
                        if(newsRootJcrNode.hasProperty("jcr:title"))
                            newsRootTitle = newsRootJcrNode.getProperty("jcr:title").getString();
                    } catch (RepositoryException e) {
                        e.printStackTrace();
                        addErrors("Error getting rssNewsImported property from " + serviceConfig.getNewsRootPath() );
                        return;
                    }
                }



                AtomicReference<WorkflowModel> referenceWfmodel = new AtomicReference<>();
                referenceWfmodel.set(wfModel);

                AtomicReference<String> referencePageTitle = new AtomicReference<String>();
                referencePageTitle.set(newsRootTitle);

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

                                //trigger processo approvativo
                                if( referenceWfmodel.get() != null) {
                                    WorkflowData payload = wfSession.newWorkflowData("JCR_PATH", p.getPath());
                                    try {
                                        wfSession.startWorkflow(referenceWfmodel.get(), payload);
                                    }catch(WorkflowException wf){
                                        wf.printStackTrace();
                                        addErrors("Exception starting approval workflow for page  " + p.getTitle() + " : " + wf.getMessage());
                                        return null;
                                    }
                                }

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
        }

        sendResult();

        logger.info("**************** End RSS Feed NEWS Importer by bean with id " + this.toString() +" **************************");
    }



    private Page createNewsPage(ResourceResolver resolver, Session session, RssItemModel item,String imgPath, String defaultPageTitle)  {

        Page yearPage = null;
        Page newsPage = null;
        Calendar pubblicationDate = null;
        //get pubdate
        int year = 0;
        if(item.getPubDate() != null) {
            pubblicationDate = Calendar.getInstance();
            pubblicationDate.setTime(item.getPubDate());
            year = pubblicationDate.get(Calendar.YEAR);
        }

        //verifico se esiste la pagina corrispondente all'anno della pubblicazione
        if(year != 0){
            try {
                String yPagePath = serviceConfig.getNewsRootPath() + "/" + year;
                if (session.nodeExists(yPagePath)) {
                    Node yPageNode = session.getNode(yPagePath);
                    /*if (yPageNode.hasProperty("jcr:primaryType") &&
                            com.adiacent.menarini.menarinimaster.core.utils.Constants.PAGE_PROPERTY_NAME.equals(yPageNode.getProperty("jcr:primaryType"))) */
                        yearPage = resolver.getResource(yPagePath).adaptTo(Page.class);


                }

                //se la pagina dell'anno non esiste, si crea
                if (yearPage == null)
                    yearPage = ModelUtils.createPage(resolver, session, serviceConfig.getNewsRootPath(), "" + year, "" + year, serviceConfig.getYearPageTemplate(), PAGE_RESOURCE_TYPE);

                if (yearPage != null) {
                    //si crea la pagina della news
                    String pageName = ModelUtils.getNodeName(item.getTitle());

                    String pageTitle = item.getTitle();

                    String template = serviceConfig.getNewsPageTemplate();

                    newsPage = ModelUtils.createPage(resolver, session, yearPage.getPath(), pageName, pageTitle, template, PAGE_RESOURCE_TYPE);

                    if (newsPage != null) {

                        //inserimento proprietà id news da dnn
                        Node jcrNode = newsPage.getContentResource().adaptTo(Node.class);
                        jcrNode.setProperty("dnnNewsIdentifier", item.getIdentifier());


                        //settagio riferimento imgpath in componente image
                        if (StringUtils.isNotBlank(imgPath)) {
                            String imgNodePath = yearPage.getPath() + "/" + pageName + "/jcr:content/root/container/container/container/image";
                            if (session.nodeExists(imgNodePath)) {
                                Node node = session.getNode(imgNodePath);
                                node.setProperty("jcr:lastModified", Instant.now().toEpochMilli());
                                node.setProperty("jcr:lastModifiedBy", IMPORTER_USER);
                                node.setProperty("fileReference", imgPath);
                                node.setProperty("imageFromPageImage", false);
                            }
                            String thumbnailNodePath = yearPage.getPath() + "/" + pageName + "/jcr:content/cq:featuredimage";
                            Node thumbnail = ModelUtils.createNode(thumbnailNodePath,"nt:unstructured",session);
                            thumbnail.setProperty("sling:resourceType", "core/wcm/components/image/v3/image");
                            thumbnail.setProperty("fileReference", imgPath);
                            thumbnail.setProperty("altValueFromDAM", false);


                        }

                        //settagio testo news
                        if (StringUtils.isNotBlank(item.getDescription())) {
                            String textNodePath = yearPage.getPath() + "/" + pageName + "/jcr:content/root/container/container/container/text";
                            if (session.nodeExists(textNodePath)) {
                                Node node = session.getNode(textNodePath);
                                node.setProperty("jcr:lastModified", Instant.now().toEpochMilli());
                                node.setProperty("jcr:lastModifiedBy", IMPORTER_USER);
                                node.setProperty("text", item.getDescription());

                            }

                        }

                        //settaggio data pubblicazione
                        Resource newsDataCmp = ModelUtils.findChildComponentByResourceType(newsPage.getContentResource(), NEWSDATA_RESOURCE_TYPE);
                        if(newsDataCmp != null){
                            Node ndNode = newsDataCmp.adaptTo(Node.class);
                            ndNode.setProperty(NEWSDATA_DATE_PROPERTY_NAME,pubblicationDate );
                        }
                        //Settaggio titolo di pagina in componente internalheader
                        Resource internalHeaderCmp = ModelUtils.findChildComponentByResourceType(newsPage.getContentResource(), INTERNAL_HEADER_RESOURCE_TYPE);
                        if(internalHeaderCmp != null && StringUtils.isNotBlank(defaultPageTitle)){
                            Node ihNode = internalHeaderCmp.adaptTo(Node.class);
                            ihNode.setProperty("jcr:title",defaultPageTitle );
                            ihNode.setProperty("titleFromPage",false );
                            ihNode.setProperty("altValueFromDAM", false);
                            ihNode.setProperty("imageFromPageImage", false);

                        }
                        session.save();

                    }
                }
            }catch(RepositoryException | WCMException e){
                e.printStackTrace();
                addErrors("Error creating page for news " + item.getTitle() + " " + e.getMessage());
                return null;
            }

        }
        return newsPage;
    }

    @SuppressWarnings("findsecbugs:PATH_TRAVERSAL_IN")
    public String addImage(ResourceResolver resolver, Session session, RssItemModel item) throws Exception {

        if(item == null)
            return null;

        String addedImagePath = null;

        String imageType = DEFAULT_IMAGE_TYPE;
        if (item.getEnclosure()!= null && StringUtils.isNotBlank(item.getEnclosure().getType()))
            imageType = item.getEnclosure().getType();

        logger.debug("processing image at url: {}", item.getEnclosure().getUrl());
        String mimeType = "image/jpeg";

        // detect image mime type
        byte[] imgData = ImageUtils.recoverImageFromUrl(item.getEnclosure().getUrl());
        if (imgData == null) {
            addErrors("Error getting img for  " + item.getTitle() + " at " + item.getEnclosure().getUrl());
            return null;
        }

        InputStream iis = new ByteArrayInputStream(imgData);
        try {
            Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(ImageIO.createImageInputStream(new ByteArrayInputStream(imgData)));
            while (imageReaders.hasNext()) {
                ImageReader reader = imageReaders.next();
                mimeType = "image/" + reader.getFormatName().toLowerCase();
                break;
            }
            logger.debug("detected image mimeType:{}", mimeType);

            AssetManager assetManager = resolver.adaptTo(AssetManager.class);
            if (assetManager != null) {
                String imgName = DEFAULT_IMAGE_TYPE.equals(imageType) ? null : FilenameUtils.getName( item.getEnclosure().getUrl());
                String imgPath = serviceConfig.getNewsImagesDAMFolder();
                String imgPathName = (imgName != null) ? ( imgPath + "/" + imgName ) : imgPath;

                logger.debug("creating product image into DAM:{}", imgPathName);
                if (assetManager.createAsset(imgPathName, iis, mimeType, true) != null) {
                    addedImagePath = imgPathName;

                } else {
                    addErrors("Cannot add image into DAM: " + imgPathName);
                }
            } else {

                addErrors("Cannot add image into DAM: AssetManager null");
            }
        } finally {
            iis.close();
        }


        return addedImagePath;
    }

    //Deserializzazione news da feed DNN
    public RssNewModel getRssNewsData() {
        URL url = null;
        try {
            url = new URL(serviceConfig.getRssFeedUrl());
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
                    RssNewModel.class);

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

    public List<String> getNewsCreated() {
        if(newsCreated == null)
            return null;
        String[] array = newsCreated.toArray(new String[newsCreated.size()]);
        String[] clone = array.clone();
        return Arrays.asList(clone);
    }
    public void setNewsCreated(List<String> newsCreated) {
        if(newsCreated== null)
            this.newsCreated = null;
        else {
            String[] array = newsCreated.toArray(new String[newsCreated.size()]);
            String[] clone = array.clone();
            this.newsCreated = Arrays.asList(clone);
        }
    }

    public void addNewsCreated(String txt){
        if(StringUtils.isNotBlank(txt)) {
            if (this.getNewsCreated() == null)
                this.newsCreated = new ArrayList<String>();
            this.newsCreated.add(txt);
        }
    }

    public List<String> getNewsDiscarded() {
        if(newsDiscarded == null)
            return null;
        String[] array = newsDiscarded.toArray(new String[newsDiscarded.size()]);
        String[] clone = array.clone();
        return Arrays.asList(clone);
    }
    public void setNewsDiscarded(List<String> newsDiscarded) {
        if(newsDiscarded== null)
            this.newsDiscarded = null;
        else {
            String[] array = newsDiscarded.toArray(new String[newsDiscarded.size()]);
            String[] clone = array.clone();
            this.newsDiscarded = Arrays.asList(clone);
        }
    }

    public void addNewsDiscarded(String txt){
        if(StringUtils.isNotBlank(txt)) {
            if (this.getNewsDiscarded() == null)
                this.newsDiscarded = new ArrayList<String>();
            this.newsDiscarded.add(txt);
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

        if(newsCreated != null  && newsCreated.size() > 0){
            String resultOK = newsCreated.stream().collect(Collectors.joining("\n"));
            result+="\n\nNews create:"+ newsCreated.size()+" \n\n"+resultOK;
        }

        if(newsDiscarded != null  && newsDiscarded.size() > 0){
            String resultDis = newsDiscarded.stream().collect(Collectors.joining("\n"));
            result+="\n\nNews scartate :"+ newsDiscarded.size()+"\n\n"+resultDis;
        }


        Email mail = new HtmlEmail();
        try {
            mail.setMsg(result);
            mail.setSubject("Fine importazione dnn RSS Feed news ");
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

    public WorkflowSession getWorkflowSession(Session session) {
        if(session != null) {
            return workflowService.getWorkflowSession(session);
            /*ResourceResolver resolver1 = getResourceResolver();
            if(resolver1 != null) {
                Session session1 = resolver1.adaptTo(Session.class);
                if(session1 != null)
                    return workflowService.getWorkflowSession(session1);
            }*/
        }
        return null;

    }

    @ObjectClassDefinition(name = "RSS News Importer", description = "RSS News  Importer")
    public static @interface Config {

        @AttributeDefinition(name = "Disable News Import", description = "Disable News Import")
        boolean isNewsImportDisabled() default false;

        @AttributeDefinition(name = "News Root Path", description = "Imported news root path")
        String getNewsRootPath() default "/content/menarini-berlinchemie/de/news";

        @AttributeDefinition(name = "Rss feed url", description = "RSS Feed url")
        String getRssFeedUrl() default "";

        @AttributeDefinition(name = "DAM News Images Root Folder", description = "DAM root folder where imported news images will be placed")
        String getNewsImagesDAMFolder() default "/content/dam/menarini-berlinchemie/assets/news";

        @AttributeDefinition(name = "AEM page template for page news", description = "AEM page template for page news")
        String getNewsPageTemplate() default "/conf/menarini-berlinchemie/settings/wcm/templates/menarini---details-news";

        @AttributeDefinition(name = "AEM page template for year page", description = "AEM page template for year page")
        String getYearPageTemplate() default "menarini---news-year";


        @AttributeDefinition(name = "Debug Report Enabled", description = "Enable debug report email")
        boolean isDebugReportEnabled() default false;

        @AttributeDefinition(name = "Debug Report Subject", description = "Debug email report subject")
        String getDebugReportSubject() default "[MTT] RSS News Import Procedure";

        @AttributeDefinition(name = "Debug Report Recipient", description = "Debug email report recipient")
        String getDebugReportRecipient() default "";

        @AttributeDefinition(name = "Debug Report Recipient Copyto", description = "Debug email report recipient Copyto")
        String[] getDebugReportRecipientCopyTo() default {};

        @AttributeDefinition(name = "News Approval Workflow Disabled", description = "News Approval Workflow Disabled")
        boolean isApprovalWorkflowDisabled() default false;
        @AttributeDefinition(name = "Approval Workfloew Identifier", description = "Approval Workfloew Identifier")
        String getApprovalWorkflowModelPath() default "/var/workflow/models/publish-approval-for-menarini-berlinchemie";


    }
}
