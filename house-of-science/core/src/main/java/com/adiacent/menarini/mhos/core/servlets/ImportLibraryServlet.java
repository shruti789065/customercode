package com.adiacent.menarini.mhos.core.servlets;


import com.adiacent.menarini.mhos.core.business.ContentFragmentApi;
import com.adiacent.menarini.mhos.core.exception.ImportLibraryException;
import com.adiacent.menarini.mhos.core.models.*;
import com.adiacent.menarini.mhos.core.resources.ImportLibraryResource;
import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.dam.print.ids.StringConstants;
import com.day.cq.commons.RangeIterator;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.dam.api.Asset;
import com.day.cq.mailer.MailService;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagException;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.repoinit.parser.operations.DeleteServiceUser;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.*;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.ss.usermodel.Cell;

import javax.jcr.*;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.version.VersionException;
import javax.servlet.Servlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;



@Component(service = { Servlet.class }, name = "Menarini House of Science - Import Library Servlet", immediate = true)
@SlingServletResourceTypes(
        resourceTypes = NameConstants.NT_PAGE,
        methods= {HttpConstants.METHOD_GET},
        extensions= "json",
        selectors={ImportLibraryServlet.DEFAULT_SELECTOR})
@ServiceDescription("Menarini House of Science - Import Library Servlet")

public class ImportLibraryServlet extends AbstractJsonServlet {

    private transient final Logger LOG = LoggerFactory.getLogger(this.getClass());
    public static final String DEFAULT_SELECTOR = "importLibrary";

    //indice colonne del file excel dalle quali recuperare le informazioni
    private static Integer AUTHOREDATE_COL_INDEX = 0;
    private static Integer TYPOLOGY_TAGS_COL_INDEX = 1;
    private static Integer TOPIC_TAGS_COL_INDEX = 2;
    private static Integer SOURCE_TAGS_COL_INDEX = 2;
    private static Integer TITLE_COL_INDEX = 3;
    private static Integer DESCRIPTION_COL_INDEX = 4;
    private static Integer AUTHOR_COL_INDEX = 5;
    private static Integer GENERIC_TAGS_COL_INDEX = 6;

    private static Integer LINK_COL_INDEX = 7;

    private static SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T00:00:00.000Z'");
    private static String TAG_RESOURCE_TYPE = "cq/tagging/components/tag";

    private static String TAG_VALUES_SEPARATOR = ",";
    boolean running = false; //TRUE: è in corso l'import

    private ImportLibraryServlet.Response resp = null;


    private ResourceResolver resourceResolver = null;
    private ImportLibraryResource.Config servletConfig = null;
    private ContentFragmentApi cfApi = null;
    private Session session = null;
    private String serverName = null;
    private Integer serverPort = null;

    TagManager tagManager = null;
    private List<String> errors;

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL)
    protected volatile MailService mailService;


    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response){
        if(running){
            resp.setResult("running");
            sendResult(resp, response);
        }

        else{

            try{
                LOG.info("******************Import library servlet ************************");

                Resource currentResource = request.getResource();
                resourceResolver = currentResource.getResourceResolver();
                session = getCustomSession();//resourceResolver.adaptTo(Session.class);
                tagManager = getTagManager();
                servletConfig = getCustomConfig();
                serverName = request.getServerName();
                serverPort = request.getServerPort();
                cfApi = new ContentFragmentApi();
                resp = new Response();
                errors = null;
                //+bonifica valori tag author: se ci sono dei tag sotto /content/cq:tag/author che non siano container tag ( ovvero nodi diversi da A,B,C,...)
                //vanno spostati sotto i relativi container tag
                cleanAuthorValueTag();
                if(errors != null && errors.size() > 0){
                    resp.setResult(KO_RESULT);
                    resp.setErrors(errors);
                    sendResult(resp,response);
                    return;
                }
                //-bonifica valori tag author

                //recupero file excel dal dam
                Resource rs = resourceResolver.getResource(servletConfig.getSourceFilePath());
                if(rs == null) {
                    resp.setResult(KO_RESULT);
                    resp.addErrors("File not found at " + servletConfig.getSourceFilePath());
                    sendResult(resp,response);
                    return;
                }

                InputStream inputStream = getFileInputStream(rs);
                //Asset asset = rs.adaptTo(Asset.class);
                //InputStream inputStream = asset.getOriginal().adaptTo(InputStream.class);
                if(inputStream == null) {
                    resp.setResult(KO_RESULT);
                    resp.addErrors("File Input stream: reading error");
                    sendResult(resp,response);
                    return;
                }

                //Se abilitata da configurazione di importano i valori dei tag
                if(servletConfig.isImportTagEnabled()){
                    importTagsData(inputStream);
                    if(errors != null && errors.size() > 0){
                        resp.setResult(KO_RESULT);
                        resp.setErrors(errors);
                        sendResult(resp,response);
                        return;
                    }
                }



                if(servletConfig.isImportArticleEnabled()){
                     importArticlesData(inputStream, request.getServerName(), request.getServerPort());
                    if(errors != null && errors.size() > 0){
                        resp.setResult(KO_RESULT);
                        resp.setErrors(errors);
                        sendResult(resp,response);
                        return;
                    }
                }


                inputStream.close();

            }catch (Exception e){
                addErrors(e.getMessage());
                LOG.error("Error in import library servlet: ", e);
            }

            running = false;

            resp.setResult(OK_RESULT);

            sendResult(resp, response);
            LOG.info("******************FINE Import library servlet ************************");
        }

    }



    public InputStream getFileInputStream(Resource rs) {
        if(rs != null) {
            Asset asset = rs.adaptTo(Asset.class);
            return asset.getOriginal().adaptTo(InputStream.class);
        }
        return null;
    }

    public TagManager getTagManager() {
        if(resourceResolver != null)
            return resourceResolver.adaptTo(TagManager.class);
        return null;
    }

    public ImportLibraryResource.Config getCustomConfig() {
        return ImportLibraryResource.get_instance().getConfig();
    }
    public Session getCustomSession() {
        if(resourceResolver != null)
        return resourceResolver.adaptTo(Session.class);
        return null;

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

    private Node insertOrUpdateTagNode(Node tag, String destinationPath,  String title, HashMap properties) throws ImportLibraryException {

        if( StringUtils.isBlank(title))
            return null;
        Node n = tag;
        try{
            if( n == null){
                String name = getNodeName(title);
                //TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
                Tag tagChild = getTagManager().createTag(name, title, null,false);
                tagChild = getTagManager().moveTag(tagChild,destinationPath+"/" + name );
                //session.move(destinationPath, destinationPath+"/" + name);

                n = tagChild.adaptTo(Node.class);
                session.save();
            }
            else{
                Calendar lmCalendar = Calendar.getInstance();
                lmCalendar.setTimeInMillis(System.currentTimeMillis());
                n.setProperty(JcrConstants.JCR_LASTMODIFIED, lmCalendar);
            }
            final Node node = n;
            if(properties != null){
                properties.keySet().forEach(k -> {
                    try {
                        node.setProperty((String)k, (String)properties.get(k));
                    } catch (RepositoryException e) {
                        throw new RuntimeException(e);
                    }
                });

            }
            //session.save();
        } catch (RepositoryException | InvalidTagFormatException |  TagException e) {
            throw new ImportLibraryException(e.getMessage());
        }
        return n;
    }


    private void cleanAuthorValueTag(){
        //Si controlla la presenza di nodi sotto /content/cq:tag/author di tipo cq:tag con name .length >1 ( in questo caso si escludono i tagcontainer di nome A,B,C,D...)
        String authorTagPath = servletConfig.getTagsRootPath()+"/author";
        Resource resource = resourceResolver.getResource(authorTagPath);
        Iterator<Resource> children = resource.listChildren();

        String prevTagId = null; //memorizza il tagID, namespace, del nodo oggetto di analisi prima della migrazione sotto il tagContainer di competenza A,B,C,D...
        String tagContainerName = "";
        while (children.hasNext() ) {
            Resource child = children.next();
            String childName = child.getName();
            if(childName.length() > 1){

                //Si recupera il namespace corrente del tag da spostare
                Tag tagChild = child.adaptTo(Tag.class);
                prevTagId = tagChild.getTagID();


                //Si sposta il nodo nel tagcontainer nuova destinazione
                tagContainerName = childName.substring(0,1).toLowerCase(); //si recupera la prima iniziale dal nome del nodo da spostare
                String destinationPath = authorTagPath+"/"+tagContainerName;
                //NB: Francesca-> provando il metodo seguente per il recupero dell'eventuale tagcontainer, se già presente su CRX, alle volte
                //non si ottiene un dato consistente, in quanto sembra che il crx non sia aggiornato con gli ultimi tagcontainer aggiunti durante
                //l'elaborazione della servlet. Per tale motivo tale f() è commentata a favore del punto 2.
                //Node containerTagNode  = getNode("[cq:Tag]",tagContainerName,  authorTagPath,false)
                //2.
                Resource rc = resourceResolver.getResource(destinationPath);
                Node containerTagNode = rc != null ? rc.adaptTo(Node.class):null;

                //Si deve creare il tag container A,B,C,....
                if(containerTagNode == null) {
                    HashMap properties = new HashMap();
                    properties.put(JcrConstants.JCR_TITLE, tagContainerName);
                    properties.put(StringConstants.SLING_RESOURCE_TYPE, TAG_RESOURCE_TYPE);
                    try{
                        insertOrUpdateTagNode(null, authorTagPath, tagContainerName, properties);
                    }catch(ImportLibraryException e){
                        addErrors(e.getMessage());
                        break;
                    }

                }

                try {
                    //NB: Francesca -> la moveTag alle  volte si incarta, non la posso usare.
                    //Per questo motivo si è preferito :
                    // 1. calcolare il nuovo tagID ( namespace ) dopo lo spostamento del tag
                    // 2. individuare tutti i ContentFragment che risultassero associati al tag autore corrente, oggetto di spostamento
                    // 3. sostituire il vecchio namespace del tag autore con il nuovo nel content fragment
                    // 4. procedere con lo spostamento del nodo tramite il metodo session.move(src ,dest)

                    //tagChild = tagManager.moveTag(tagChild,destinationPath+"/" + childName );NON FUNZIONA SEMPRE

                    //1.
                    String newTagNamespace = StringUtils.substringBeforeLast(prevTagId, "/") + "/" + tagContainerName + "/" + childName;

                    //2.
                    RangeIterator<Resource> res = tagManager.find(prevTagId);
                    if(res != null){
                        while (res.hasNext()) {
                            Resource r = res.next();
                            String jsonPath = r.getPath().replace(servletConfig.getDamRootPath(), "");
                            jsonPath = jsonPath.replace("/jcr:content/data/master", "");
                            jsonPath = jsonPath + ".json";
                            ContentFragmentModel cf = cfApi.getByPath(serverName, serverPort, jsonPath);
                            if (cf != null) {
                                List<Object> authorList = cf.getProperties().getElements().getAuthor().getValue();
                                final String otId = prevTagId;
                                List<Object> newAuthorList = authorList.stream().map(obj -> {
                                    String a = (String) obj;
                                    if (a.equals(otId)) {
                                        a = newTagNamespace;
                                    }
                                    return a;
                                }).collect(Collectors.toList());
                                cf.getProperties().getElements().getAuthor().setValue(newAuthorList);

                                String targetPath = r.getPath().replace(servletConfig.getDamRootPath(), "");
                                targetPath = targetPath.replace("/jcr:content/data/master", "");

                                cfApi.put(serverName, serverPort, targetPath, cf);
                            }

                        }
                    }

                    //4.
                    session.move(tagChild.getPath(), destinationPath + "/" + childName);
                    session.save();

                } catch (RepositoryException e) {
                   addErrors(e.getMessage());

                }


            }
        }

    }

    private void handleValueTag(String parentTagTitle, String values){

        List<String> convertedList = parseValues(parentTagTitle, values);

        if(convertedList != null && convertedList.size() > 0){
            String parentTagName = getNodeName(parentTagTitle);
            //check tag parent with name "parentTagName"
            //Node parentTagNode = getNode("[cq:Tag]", parentTagName, servletConfig.getTagsRootPath(), true);unused
            Resource parentRc = resourceResolver.getResource(servletConfig.getTagsRootPath()+"/"+parentTagName);
            Node parentTagNode = parentRc != null ? parentRc.adaptTo(Node.class):null;

            //Eventuale creazione dei tag "parent" se non esistono su crx ( author, topic ,source ,generic-tags, typology,..)
            if(parentTagNode == null){
                HashMap properties = new HashMap();
                properties.put(JcrConstants.JCR_TITLE, parentTagTitle);
                properties.put(StringConstants.SLING_RESOURCE_TYPE, TAG_RESOURCE_TYPE);
                try {
                    parentTagNode = insertOrUpdateTagNode(null, servletConfig.getTagsRootPath(), parentTagTitle ,properties );
                } catch (ImportLibraryException e) {
                   addErrors( e.getMessage());
                   return;
                }
            }

            if(parentTagNode != null){
                Node parent = parentTagNode;
                convertedList.stream().forEach(tagVal->{
                    String tagName = StringUtils.replace(tagVal.toLowerCase().trim(), " ", "-");
                    try {
                        //Node valueNode = getNode("[cq:Tag]",tagName,  parent.getPath(), true); unused
                        String rPath = parent.getPath()+("author".equals(parentTagTitle) ? "/"+tagName.substring(0,1) :"")+"/"+tagName;
                        Resource rc = resourceResolver.getResource(rPath);
                        Node valueNode = rc != null ? rc.adaptTo(Node.class):null;


                        if(valueNode == null){

                            String destinationPath = parent.getPath();
                            if("author".equals(parentTagTitle)){
                                //i valori del tag author si storicizzano sotto folder aventi l'iniziale dell'autore
                                String tagContainerName = tagVal.substring(0,1).toLowerCase();
                                destinationPath = parent.getPath()+"/"+tagContainerName;
                                //Node containerTagNode  = getNode("[cq:Tag]", tagContainerName,  parent.getPath(),false); unused
                                Resource rcTC = resourceResolver.getResource(destinationPath);
                                Node containerTagNode= rcTC != null ? rcTC.adaptTo(Node.class):null;

                                if(containerTagNode == null){
                                    HashMap properties = new HashMap();
                                    properties.put(JcrConstants.JCR_TITLE, tagContainerName);
                                    properties.put(StringConstants.SLING_RESOURCE_TYPE, TAG_RESOURCE_TYPE);
                                    insertOrUpdateTagNode(null, parent.getPath(),  tagContainerName , properties);
                                }

                            }

                            HashMap properties = new HashMap();
                            properties.put(JcrConstants.JCR_TITLE, tagVal);
                            properties.put(StringConstants.SLING_RESOURCE_TYPE, "cq/tagging/components/tag");
                            insertOrUpdateTagNode(valueNode, destinationPath,  tagVal, properties);
                        }
                    } catch (RepositoryException | ImportLibraryException e) {
                        addErrors(e.getMessage());
                        return;
                    }

                });
            }


        }
        /*
        if(StringUtils.isNotBlank(values)){
            //si usa la virgola come separatore
            List<String> convertedList = Stream.of(values.split(TAG_VALUES_SEPARATOR, -1))
                    .map(a->{
                        String res = a.toLowerCase();
                        if("author".equals(parentTagTitle)) {
                            res = res.replaceAll("\\*|nes|st|nink|mr|md|phd|ms(\\w+[;]{0,1})", "");
                            res = res.replaceAll("&amp;eacute;", "é");
                            res = res.replaceAll("&amp;egrave;", "è");
                            res = res.replaceAll("&amp;ouml;", "Ö");
                            res = res.replaceAll("&amp;uuml;", "ü");
                            res = res.replaceAll("&amp;aacute;", "á");
                            res = res.replaceAll("&amp;iacute;", "í");
                            res = res.replaceAll("&amp;uacute;", "ú");
                            res = res.replaceAll("&amp;ccedil;", "ç");
                            res = res.replaceAll("&amp;agrave;", "à");
                            res = res.replaceAll("&amp;rsquo;", "’");
                            res = res.replaceAll("<br />", "");
                            res = res.replaceAll("&amp;amp", "");

                        }
                        res=StringUtils.trimToEmpty(res);
                        return res;
                    })
                    .filter(e-> StringUtils.isNotBlank(e))
                    .collect(Collectors.toList());

            if(convertedList != null && convertedList.size() > 0){
                String parentTagName = getNodeName(parentTagTitle);
                //check tag parent with name "parentTagName"
                //Node parentTagNode = getNode("[cq:Tag]", parentTagName, servletConfig.getTagsRootPath(), true);to check
                Resource parentRc = resourceResolver.getResource(servletConfig.getTagsRootPath()+"/"+parentTagName);
                Node parentTagNode = parentRc != null ? parentRc.adaptTo(Node.class):null;

                //Eventuale creazione dei tag "parent" se non esistono su crx ( author, topic ,source ,generic-tags, typology,..)
                if(parentTagNode == null){
                    HashMap properties = new HashMap();
                    properties.put(JcrConstants.JCR_TITLE, parentTagTitle);
                    properties.put(StringConstants.SLING_RESOURCE_TYPE, TAG_RESOURCE_TYPE);
                    parentTagNode = insertOrUpdateTagNode(null, servletConfig.getTagsRootPath(), parentTagTitle ,properties );
                }

                if(parentTagNode != null){
                    Node parent = parentTagNode;
                    convertedList.stream().forEach(tagVal->{
                        String tagName = StringUtils.replace(tagVal.toLowerCase().trim(), " ", "-");
                        try {
                            //Node valueNode = getNode("[cq:Tag]",tagName,  parent.getPath(), true);
                            String rPath = parent.getPath()+("author".equals(parentTagTitle) ? "/"+tagName.substring(0,1) :"")+"/"+tagName;
                            Resource rc = resourceResolver.getResource(rPath);
                            Node valueNode = rc != null ? rc.adaptTo(Node.class):null;


                            if(valueNode == null){

                                String destinationPath = parent.getPath();
                                if("author".equals(parentTagTitle)){
                                    //i valori del tag author si storicizzano sotto folder aventi l'iniziale dell'autore
                                    /*String folderName = v.substring(0,1);
                                    destinationPath = parent.getPath()+"/"+folderName;
                                    //se la folder esiste già non si crea, altrimenti si
                                    Resource folder = resourceResolver.getResource(destinationPath );
                                    if(folder == null){
                                        Session session = resourceResolver.adaptTo(Session.class);
                                        JcrUtil.createPath(destinationPath, "sling:Folder", session);
                                        session.save();
                                    }* /
                                    String tagContainerName = tagVal.substring(0,1).toLowerCase();
                                    destinationPath = parent.getPath()+"/"+tagContainerName;
                                    //Node containerTagNode  = getNode("[cq:Tag]", tagContainerName,  parent.getPath(),false);
                                    Resource rcTC = resourceResolver.getResource(destinationPath);
                                    Node containerTagNode= rcTC != null ? rcTC.adaptTo(Node.class):null;

                                    if(containerTagNode == null){
                                        HashMap properties = new HashMap();
                                        properties.put(JcrConstants.JCR_TITLE, tagContainerName);
                                        properties.put(StringConstants.SLING_RESOURCE_TYPE, TAG_RESOURCE_TYPE);
                                        insertOrUpdateTagNode(null, parent.getPath(),  tagContainerName , properties);
                                    }

                                }

                                HashMap properties = new HashMap();
                                properties.put(JcrConstants.JCR_TITLE, tagVal);
                                properties.put(StringConstants.SLING_RESOURCE_TYPE, "cq/tagging/components/tag");
                                insertOrUpdateTagNode(valueNode, destinationPath,  tagVal, properties);
                            }
                        } catch (RepositoryException e) {
                            addErrors(e.getMessage());

                        }

                    });
                }


            }

        }*/

    }

    public XSSFWorkbook createWorkBook(InputStream inputStream) throws IOException {
        return new XSSFWorkbook(inputStream);
    }
    private void importTagsData(InputStream inputStream) {
        LOG.debug("Start import tags data********************");
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(inputStream); //new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);     //creating a Sheet object to retrieve object

            if(sheet == null) {
                addErrors("Missing sheet in file excel");
                return;
            }

            //iterazione righe dell'excel
            Iterator<Row> itr = sheet.iterator();
            int count = 0;
            while (itr.hasNext()) {
                Row row = itr.next();
                LOG.info("ROW " + row.getRowNum() + " **********");
                //ignoro l'header se il file excel la prevede
                if(servletConfig.isHeaderRowPresent() && count == 0);
                else{

                    Cell authorCell = row.getCell(AUTHOR_COL_INDEX);
                    if(authorCell != null) {
                        LOG.info("\t author **********");
                        String value = authorCell.getStringCellValue();
                        handleValueTag( "author", value );
                    }

                    Cell topicCell = row.getCell(TOPIC_TAGS_COL_INDEX);
                    if(topicCell != null) {
                        LOG.info("\t topic  **********");
                        String value = topicCell.getStringCellValue();
                        handleValueTag("topic", value );
                    }

                    Cell sourceCell = row.getCell(SOURCE_TAGS_COL_INDEX);
                    if(sourceCell != null) {
                        LOG.info("\t source **********");
                        String value = sourceCell.getStringCellValue();
                        handleValueTag("source", value );
                    }

                    Cell genericTagsCell = row.getCell(GENERIC_TAGS_COL_INDEX);
                    if(genericTagsCell != null) {
                        LOG.info("\t generic tags**********");
                        String value = genericTagsCell.getStringCellValue();
                        handleValueTag("generic tags", value );
                    }


                    Cell typologyCell = row.getCell(TYPOLOGY_TAGS_COL_INDEX);
                    if(typologyCell != null) {
                        LOG.info("\t typology **********");
                        String value = typologyCell.getStringCellValue();
                        handleValueTag("typology", value );
                    }
                }
                count++;
            }

            inputStream.reset();
        } catch (IOException e) {
           addErrors(e.getMessage());

        }


        LOG.debug("End import tags data******************************");


    }


    private void importArticlesData(InputStream inputStream, String serverName, int serverPort) {
        LOG.debug("Start import Articles data********************");

        // Si crea la folder della categoria, se non esiste,  per la storicizzazione dei content fragment relativi agli articoli
        try {
            Resource categoryFolder =  resourceResolver.getResource(servletConfig.getCategoryPath());
            if(categoryFolder == null) {
                Node n = JcrUtil.createPath(servletConfig.getCategoryPath(), "sling:Folder", session);
                Node jcr = JcrUtil.createPath(servletConfig.getCategoryPath()+"/jcr:content", JcrConstants.NT_UNSTRUCTURED, session);
                jcr.setProperty("title","infectivology");
                jcr.setProperty("source","false");
                session.save();
            }
        } catch (RepositoryException e) {
          addErrors("Error in creating category folder at "+ servletConfig.getCategoryPath());
          return;
        }

        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);     //creating a Sheet object to retrieve object

            Iterator<Row> itr = sheet.iterator();    //iterating over excel file
            int count = 0;
            while (itr.hasNext()) {
                Row row = itr.next();
                //ignoro l'header se il file excel la prevede
                if (servletConfig.isHeaderRowPresent() && count == 0) ;
                else {
                    ContentFragmentModel cf = generateContentFragmentFromRow(row);

                    if (cf != null) {
                        //si controlla l'esistenza del content fragment su crx sotto la folder della categoria
                        String cfName = getNodeName(cf.getProperties().getTitle());
                        //Node n = getNode("[dam:Asset]", cfName, servletConfig.getCategoryPath(), true);unused

                        //i cf si storicizzano sotto folder aventi come nome, l'anno di pubblicazione
                        String authorDateStr = (String) cf.getProperties().getElements().getArticleDate().getValue();
                        LocalDate date = LocalDate.parse(authorDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T00:00:00.000Z'"));


                        String year ="" + date.getYear();
                        String folderPath = servletConfig.getCategoryPath() + "/" + year;

                        String  cfResourcePath = servletConfig.getCategoryPath()+"/"+ year+"/"+cfName;
                        Resource rCF = resourceResolver.getResource(cfResourcePath);
                        Node n = rCF != null ? rCF.adaptTo(Node.class) : null;

                        if (n != null) {
                            String targetPath = StringUtils.replace(cfResourcePath, servletConfig.getDamRootPath() + "/", "");
                            cfApi.put(serverName, serverPort, targetPath, cf);
                        } else {

                            //se la folder esiste già non si crea, altrimenti sì
                            Resource folder = resourceResolver.getResource(folderPath);
                            if (folder == null) {
                                JcrUtil.createPath(folderPath, "sling:Folder", session);
                                session.save();
                            }
                            String targetPath = StringUtils.replace(cfResourcePath, servletConfig.getDamRootPath() + "/", "") ;
                            cfApi.create(serverName, serverPort, targetPath, cf);
                        }

                    }
                }
                count++;
            }

        } catch (IOException | RepositoryException | ImportLibraryException e) {
            addErrors(e.getMessage());
            return;
        }
        LOG.debug("End import Articles data******************************");
    }



    private ContentFragmentModel generateContentFragmentFromRow(Row row) throws ImportLibraryException {
        ContentFragmentModel res = new ContentFragmentModel();
        res.setProperties(new ContentFragmentPropertiesModel());
        res.getProperties().setCqModel("/conf/mhos/settings/dam/cfm/models/result-fragment");
        res.getProperties().setElements(new ContentFragmentElements());

        try{
            Cell cell = row.getCell(TITLE_COL_INDEX);
            if (cell != null) {
                String value = cell.getStringCellValue().replace("/", " ").trim();
                res.getProperties().setTitle(value);
            }

            cell = row.getCell(AUTHOREDATE_COL_INDEX);
            if (cell != null) {
                Date d = cell.getDateCellValue();
                //isoFormat.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
                String dateStr = isoFormat.format(d);
                ContentFragmentElementSingleValue aDate = new ContentFragmentElementSingleValue();
                aDate.setType("Date");
                aDate.setValue(dateStr);
                res.getProperties().getElements().setArticleDate(aDate);
            }


            cell = row.getCell(DESCRIPTION_COL_INDEX);
            if (cell != null) {
                String value = cell.getStringCellValue().trim();
                ContentFragmentElementSingleValue desc = new ContentFragmentElementSingleValue();
                desc.setType("text/html");
                desc.setValue(value);
                res.getProperties().getElements().setDescription(desc);
            }


            cell = row.getCell(LINK_COL_INDEX);
            if (cell != null) {
                String value = cell.getStringCellValue().trim();
                if (StringUtils.isNotBlank(value)) {
                    ContentFragmentElementSingleValue link = new ContentFragmentElementSingleValue();
                    link.setType("string");
                    link.setValue(value);
                    res.getProperties().getElements().setLink(link);

                    ContentFragmentElementSingleValue linkTarget = new ContentFragmentElementSingleValue();
                    linkTarget.setType("string");
                    linkTarget.setValue("_blank");
                    res.getProperties().getElements().setLinkTarget(linkTarget);

                    ContentFragmentElementSingleValue linkLabel = new ContentFragmentElementSingleValue();
                    linkLabel.setType("string");
                    linkLabel.setValue("read more");
                    res.getProperties().getElements().setLinkLabel(linkLabel);
                }

            }

            cell = row.getCell(AUTHOR_COL_INDEX);
            //TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
            if (cell != null) {
                String values = cell.getStringCellValue();
                //pulisco i valori della cella
                List<String> authorList = parseValues("author", values);
                //Node parentTagNode = getNode("[cq:Tag]", "author", servletConfig.getTagsRootPath(), false); to check
                Resource rc = resourceResolver.getResource(servletConfig.getTagsRootPath()+"/author");
                Node parentTagNode = rc != null ? rc.adaptTo(Node.class):null;

                if (authorList != null && authorList.size() > 0) {
                        List tgs = authorList.stream().map(a -> {
                        //controllo se il valore i-esimo dell'authore esiste come valore possibile del tag AUTHOR

                        String tagPath = null;
                        try {
                            tagPath = parentTagNode.getPath() + "/" + a.substring(0, 1).toLowerCase() + "/" + getNodeName(a);
                        } catch (RepositoryException e) {
                           addErrors("Error in creating content fragment with title " + res.getProperties().getTitle() +" : access to crx wrong");
                           return null;
                        }
                        Resource tagResource = resourceResolver.getResource(tagPath);
                        Tag t = tagResource != null ? tagResource.adaptTo(Tag.class) : null;

                        if (t !=null)
                            return t.getTagID();
                        else {
                            addErrors("Error in creating content fragment with title " + res.getProperties().getTitle() +" : missing author tag " + getNodeName(a));
                            return null;
                        }


                    }).filter(e -> e != null).collect(Collectors.toList());
                        if(tgs == null || tgs.size() < authorList.size()) {
                            throw new ImportLibraryException("Error in importing content fragment with title " + res.getProperties().getTitle());
                        }

                    if (tgs.size() > 0) {
                        ContentFragmentElementValue author = new ContentFragmentElementValue();
                        author.setType("string");
                        author.setValue(tgs);
                        res.getProperties().getElements().setAuthor(author);
                    }
                }
            }


            cell = row.getCell(SOURCE_TAGS_COL_INDEX);
            if (cell != null) {
                String values = cell.getStringCellValue();
                //pulisco i valori della cella
                List<String> sourceList = parseValues("source", values);
                //Node parentTagNode = getNode("[cq:Tag]", "source", servletConfig.getTagsRootPath(), false); to check
                Resource rc = resourceResolver.getResource(servletConfig.getTagsRootPath()+"/source");
                Node parentTagNode = rc != null ? rc.adaptTo(Node.class):null;

                if (sourceList != null && sourceList.size() > 0) {
                    List tgs = sourceList.stream().map(a -> {
                        //controllo se il valore i-esimo della source esiste come valore possibile del tag SOURCE
                        try {
                            String tagPath = parentTagNode.getPath() + "/" + getNodeName(a);
                            Resource tagResource = resourceResolver.getResource(tagPath);
                            Tag t = tagResource != null ? tagResource.adaptTo(Tag.class) : null;

                            if (t !=null)
                                return t.getTagID();
                            else {
                                addErrors("Error in creating content fragment with title " + res.getProperties().getTitle() +" : missing source tag " + getNodeName(a));
                                return null;
                            }


                        } catch (RepositoryException e) {
                            throw new RuntimeException(e);
                        }
                    }).filter(e -> e != null).collect(Collectors.toList());
                    if (tgs != null && tgs.size() > 0) {
                        ContentFragmentElementValue src = new ContentFragmentElementValue();
                        src.setType("string");
                        src.setValue(tgs);
                        res.getProperties().getElements().setSource(src);
                    }
                }
            }


            cell = row.getCell(TOPIC_TAGS_COL_INDEX);
            if (cell != null) {
                String values = cell.getStringCellValue();
                //pulisco i valori della cella
                List<String> sourceList = parseValues("topic", values);
                //Node parentTagNode = getNode("[cq:Tag]", "topic", servletConfig.getTagsRootPath(), false);to check
                Resource rc = resourceResolver.getResource(servletConfig.getTagsRootPath()+"/topic");
                Node parentTagNode = rc != null ? rc.adaptTo(Node.class):null;

                if (sourceList != null && sourceList.size() > 0) {
                    List tgs = sourceList.stream().map(a -> {
                        //controllo se il valore i-esimo del topic esiste come valore possibile del tag TOPIC
                        try {
                            String tagPath = parentTagNode.getPath() + "/" + getNodeName(a);
                            Resource tagResource = resourceResolver.getResource(tagPath);
                            Tag t = tagResource != null ? tagResource.adaptTo(Tag.class) : null;

                            if (t !=null)
                                return t.getTagID();
                            else {
                                addErrors("Error in creating content fragment with title " + res.getProperties().getTitle() +" : missing topic tag " + getNodeName(a));
                                return null;
                            }


                        } catch (RepositoryException e) {
                            throw new RuntimeException(e);
                        }
                    }).filter(e -> e != null).collect(Collectors.toList());
                    if (tgs != null && tgs.size() > 0) {
                        ContentFragmentElementValue tpc = new ContentFragmentElementValue();
                        tpc.setType("string");
                        tpc.setValue(tgs);
                        res.getProperties().getElements().setTopic(tpc);
                    }
                }
            }


            cell = row.getCell(GENERIC_TAGS_COL_INDEX);
            if (cell != null) {
                String values = cell.getStringCellValue();
                //pulisco i valori della cella
                List<String> sourceList = parseValues("generic-tags", values);
                //Node parentTagNode = getNode("[cq:Tag]", "generic-tags", servletConfig.getTagsRootPath(), false); tocheck
                Resource rc = resourceResolver.getResource(servletConfig.getTagsRootPath()+"/generic-tags");
                Node parentTagNode = rc != null ? rc.adaptTo(Node.class):null;

                if (sourceList != null && sourceList.size() > 0) {
                    List tgs = sourceList.stream().map(a -> {
                        //controllo se il valore i-esimo del generic-tags esiste come valore possibile del tag GENERIC-TAGS
                        try {
                            String tagPath = parentTagNode.getPath() + "/" + getNodeName(a);
                            Resource tagResource = resourceResolver.getResource(tagPath);
                            Tag t = tagResource != null ? tagResource.adaptTo(Tag.class) : null;

                            if (t !=null)
                                return t.getTagID();
                            else {
                                addErrors("Error in creating content fragment with title " + res.getProperties().getTitle() +" : missing generic tag " + getNodeName(a));
                                return null;
                            }


                        } catch (RepositoryException e) {
                            throw new RuntimeException(e);
                        }
                    }).filter(e -> e != null).collect(Collectors.toList());
                    if (tgs != null && tgs.size() > 0) {
                        ContentFragmentElementValue gtg = new ContentFragmentElementValue();
                        gtg.setType("string");
                        gtg.setValue(tgs);
                        res.getProperties().getElements().setTags(gtg);
                    }
                }
            }


            cell = row.getCell(TYPOLOGY_TAGS_COL_INDEX);
            if (cell != null) {
                String values = cell.getStringCellValue();
                //pulisco i valori della cella
                List<String> sourceList = parseValues("typology", values);
                // Node parentTagNode = getNode("[cq:Tag]", "typology", servletConfig.getTagsRootPath(), false); to check
                Resource rc = resourceResolver.getResource(servletConfig.getTagsRootPath()+"/typology");
                Node parentTagNode = rc != null ? rc.adaptTo(Node.class):null;

                if (sourceList != null && sourceList.size() > 0) {
                    List tgs = sourceList.stream().map(a -> {
                        //controllo se il valore i-esimo del typology esiste come valore possibile del tag TYPOLOGY
                        try {
                            String tagPath = parentTagNode.getPath() + "/" + getNodeName(a);
                            Resource tagResource = resourceResolver.getResource(tagPath);
                            Tag t = tagResource != null ? tagResource.adaptTo(Tag.class) : null;

                            if (t !=null)
                                return t.getTagID();
                            else {
                                addErrors("Error in creating content fragment with title " + res.getProperties().getTitle() +" : missing  typology tag " + getNodeName(a));
                                return null;
                            }


                        } catch (RepositoryException e) {
                            throw new RuntimeException(e);
                        }
                    }).filter(e -> e != null).collect(Collectors.toList());
                    if (tgs != null && tgs.size() > 0) {
                        ContentFragmentElementValue ttg = new ContentFragmentElementValue();
                        ttg.setType("string");
                        ttg.setValue(tgs);
                        res.getProperties().getElements().setTypology(ttg);
                    }
                }
            }
        } catch (ImportLibraryException e) {
            throw new ImportLibraryException(e.getMessage());
        }

        return res;
    }



    private List<String> parseValues(String rootTag, String values){

        if("author".equals(rootTag)) {
            if (StringUtils.isNotBlank(values)) {
                //si usa la virgola come separatore
                List<String> convertedList = Stream.of(values.split(TAG_VALUES_SEPARATOR, -1))
                        .map(a -> {

                            String res = StringUtils.trimToEmpty(a);
                            res = res.replaceAll("\\*", "");
                            res = res.toLowerCase();
                            res = res.replaceAll(";","");
                            res = JcrUtil.escapeIllegalJcrChars(res);
                            res = res.replaceAll("†|msc|nink|mr|md|phd|ms(\\w+[;]{0,1})", "");
                            res = res.replaceAll("\\d+\\s*", "");
                            /*res = res.replaceAll("&amp;eacute;", "é");
                            res = res.replaceAll("&amp;egrave;", "è");
                            res = res.replaceAll("&amp;ouml;", "Ö");
                            res = res.replaceAll("&amp;uuml;", "ü");
                            res = res.replaceAll("&amp;aacute;", "á");
                            res = res.replaceAll("&amp;iacute;", "í");
                            res = res.replaceAll("&amp;uacute;", "ú");
                            res = res.replaceAll("&amp;ccedil;", "ç");
                            res = res.replaceAll("&amp;agrave;", "à");
                            res = res.replaceAll("&amp;rsquo;", "’");
                            res = res.replaceAll("<br />", "");
                            res = res.replaceAll("&amp;amp", "");
                            res = res.replaceAll("/", "-");
                            res = res.replaceAll("/?", "");
                            res = res.replaceAll("\\(", "-");
                            res = res.replaceAll("\\)", "-");
                            res = res.replaceAll("\\”", "-");
                            res = res.replaceAll("\\“", "-");
                            res = res.replaceAll("’", "-");
                            res = res.replaceAll("\\.", "-");*/
                            res = StringUtils.trimToEmpty(res);
                            res = JcrUtil.escapeIllegalJcrChars(res);
                            return res;
                        })
                        .filter(a -> StringUtils.isNotBlank(a))
                        .collect(Collectors.toList());
                return convertedList;
            }
        }

        else { // same as topics, source, generic-tags, typology
            if (StringUtils.isNotBlank(values)) {
                //si usa la virgola come separatore
                List<String> convertedList = Stream.of(values.split(TAG_VALUES_SEPARATOR, -1))
                        .map(a -> {
                            String res = StringUtils.trimToEmpty(a).toLowerCase();
                            res = res.replaceAll(";", "");
                            /* res = res.replaceAll("/", "-");
                            res = res.replaceAll("/?", "");
                            res = res.replaceAll("\\(", "-");
                            res = res.replaceAll("\\)", "-");
                            res = res.replaceAll("\\”", "-");
                            res = res.replaceAll("\\“", "-");
                            res = res.replaceAll("’", "-");
                            res = res.replaceAll("\\.", "-");*/
                            res = JcrUtil.escapeIllegalJcrChars(res);
                            return res;
                        })
                        .filter(e -> StringUtils.isNotBlank(e))
                        .collect(Collectors.toList());
                return convertedList;
            }
        }

        return null;
    }

    private String getNodeName(String label){
        if(StringUtils.isBlank(label))
            return null;
        label = label.trim().toLowerCase().replaceAll("[\\(\\)\\[\\]\\']","");
        label = label.replaceAll("[\\s:]","-");
        return JcrUtil.escapeIllegalJcrChars(label);
    }

    @Override
    protected MailService getMailService() {
        return mailService;
    }


    public class Response{
        private String result;
        private List<String> errors;

        private List<String> tagsInErrors;
        private List<String> tagsImported;
        private List<String> articlesInErrors;
        private List<String> articlesImported;
        public Response() {
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
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

        public List<String> getTagsInErrors() {
            if(tagsInErrors == null)
                return null;
            String[] array = tagsInErrors.toArray(new String[tagsInErrors.size()]);
            String[] clone = array.clone();
            return Arrays.asList(clone);
        }

        public void setTagsInErrors(List<String> tagsInErrors) {
            if(tagsInErrors == null)
                this.tagsInErrors = null;
            else {
                String[] array = tagsInErrors.toArray(new String[tagsInErrors.size()]);
                String[] clone = array.clone();
                this.tagsInErrors = Arrays.asList(clone);
            }
        }

        public List<String> getTagsImported() {

            if(tagsImported == null)
                return null;
            String[] array = tagsImported.toArray(new String[tagsImported.size()]);
            String[] clone = array.clone();
            return Arrays.asList(clone);
        }

        public void setTagsImported(List<String> tagsImported) {
            if(tagsImported == null)
                this.tagsImported = null;
            else {
                String[] array = tagsImported.toArray(new String[tagsImported.size()]);
                String[] clone = array.clone();
                this.tagsImported = Arrays.asList(clone);
            }
        }


        public List<String> getArticlesInErrors() {
            if(articlesInErrors == null)
                return null;
            String[] array = articlesInErrors.toArray(new String[articlesInErrors.size()]);
            String[] clone = array.clone();
            return Arrays.asList(clone);
        }

        public void setArticlesInErrors(List<String> articlesInErrors) {
            if(articlesInErrors == null)
                this.articlesInErrors = null;
            else {
                String[] array = articlesInErrors.toArray(new String[articlesInErrors.size()]);
                String[] clone = array.clone();
                this.articlesInErrors = Arrays.asList(clone);
            }
        }

        public List<String> getArticlesImported() {
            if(articlesImported == null)
                return null;
            String[] array = articlesImported.toArray(new String[articlesImported.size()]);
            String[] clone = array.clone();
            return Arrays.asList(clone);
        }

        public void setArticlesImported(List<String> articlesImported) {
            if(articlesImported == null)
                this.articlesImported = null;
            else {
                String[] array = articlesImported.toArray(new String[articlesImported.size()]);
                String[] clone = array.clone();
                this.articlesImported = Arrays.asList(clone);
            }
        }
    }
}
