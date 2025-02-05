package com.adiacent.menarini.mhos.core.services;

import com.adiacent.menarini.mhos.core.business.ContentFragmentApi;
import com.adiacent.menarini.mhos.core.exception.ImportLibraryException;
import com.adiacent.menarini.mhos.core.models.*;
import com.adiacent.menarini.mhos.core.resources.ImportLibraryResource;
import com.adobe.dam.print.ids.StringConstants;
import com.day.cq.commons.Externalizer;
import com.day.cq.commons.RangeIterator;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.dam.api.Asset;
import com.day.cq.mailer.MailService;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagException;
import com.day.cq.tagging.TagManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component(
        service = LibraryImporter.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Menarini Dnn Library importer Indexer",
                Constants.SERVICE_VENDOR + "=Adiacent"
        },
        immediate = true
)
public class LibraryImporter implements Cloneable{

    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

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

    private SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T00:00:00.000Z'");
    private static String TAG_RESOURCE_TYPE = "cq/tagging/components/tag";

    private static String CQMOVETO_PROPERTY = "cq:movedTo";

    private static String TAG_VALUES_SEPARATOR = ",";

    private transient ImportLibraryResource.Config importerConfig = null;
    private transient ContentFragmentApi cfApi = null;


    private List<String> errors;

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL)
    protected  transient volatile MailService mailService;


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void start() {
        try{
            logger.info("******************Importer Library  ************************");


            importerConfig = getCustomConfig();

            cfApi = new ContentFragmentApi();
            errors = null;

            if(!importerConfig.isProcedureEnabled()){
                addErrors("Procedure not enabled ");
                if(errors != null && errors.size() > 0){
                    sendResult();
                    return;
                }
            }
            //+bonifica valori tag author: se ci sono dei tag sotto /content/cq:tag/author che non siano container tag ( ovvero nodi diversi da A,B,C,...)
            //vanno spostati sotto i relativi container tag
            cleanAuthorValueTag();
            if(errors != null && errors.size() > 0){
                sendResult();
                return;
            }
            //-bonifica valori tag author

            //recupero file excel dal dam
            ResourceResolver resolver = getResourceResolver();
            Resource rs = resolver.getResource(importerConfig.getSourceFilePath());

            if(rs == null)
                addErrors("File not found at " + importerConfig.getSourceFilePath());
            if(errors != null && errors.size() > 0){
                sendResult();
                return;
            }
            InputStream inputStream = getFileInputStream(rs);
            if(errors != null && errors.size() > 0){
                sendResult();
                return;
            }

            if(resolver != null)
                resolver.close();

            //Se abilitata da configurazione di importano i valori dei tag
            if(importerConfig.isImportTagEnabled()){
                importTagsData(inputStream);
                if(errors != null && errors.size() > 0){
                    sendResult();
                    return;
                }
            }




            if( importerConfig.isImportArticleEnabled()){
                importArticlesData(inputStream);
                if(errors != null && errors.size() > 0){
                    sendResult();
                    return;
                }
            }


            inputStream.close();

        }catch (Exception e){
            addErrors(e.getMessage());
            logger.error("Error in import library servlet: ", e);
        }





        sendResult();

        logger.info("******************FINE Dnn Import library ************************");


    }

    protected void sendResult() {


        String result = "Result : Ok";
        if(errors != null  && errors.size() > 0){
            result = errors.stream().collect(Collectors.joining("\n"));
        }

        Email mail = new HtmlEmail();
        try {
            mail.setMsg(result);
            mail.setSubject("Fine importazione dnn library ");
            mail.addTo(importerConfig.getEmailTo());
        /*for (String copyto : config.getDebugReportRecipientCopyTo()) {
            if (org.apache.commons.lang.StringUtils.isNotEmpty(copyto)) {
                logger.info("sendEmail Send Copy To " + copyto);
                mail.addCc(copyto);
            }
        }*/
            mail.setCharset("UTF-8");
            getMailService().send(mail);
        } catch (Exception e) {
            logger.error("Error sending report email", e);

        }



    }





    //Metodi di utility per poi fare gli unit test
    public InputStream getFileInputStream(Resource rs) {
        if(rs != null) {
            Asset asset = rs.adaptTo(Asset.class);
            InputStream is = asset.getOriginal().adaptTo(InputStream.class);
            if( is == null)
                addErrors("File Input stream: reading error");
            else
                return is;
        }
        return null;
    }


    public ImportLibraryResource.Config getCustomConfig() {
        return ImportLibraryResource.get_instance().getConfig();
    }

    public XSSFWorkbook createWorkBook(InputStream inputStream) throws IOException {
        return new XSSFWorkbook(inputStream);
    }
    public void resetInputStream(InputStream inputStream) {
        if(inputStream != null){
            if(inputStream.markSupported()) {
                //inputStream.mark(Integer.MAX_VALUE);
                try {
                    inputStream.reset();
                } catch (IOException e) {
                    addErrors(e.getMessage());
                }
            }
        }
    }
    public void storeContentFragment(boolean isNew, String hostname, String targetPath, ContentFragmentModel cf) {
        if(StringUtils.isNotBlank(hostname)  &&
                cf != null)

            if(isNew)
                cfApi.create(hostname, targetPath, cf);
            else
                cfApi.put( hostname, targetPath, cf);
    }

    public Node createNode(String path, String type, Session session) {
        try{
            if(StringUtils.isNotBlank(path) && StringUtils.isNotBlank(type))
                return JcrUtil.createPath(path, type, session);
        }catch(RepositoryException e){
            addErrors(e.getMessage());
        }
        return null;
    }
    //Fine Metodi di utility per poi fare gli unit test


    protected MailService getMailService() {
        return mailService;
    }

    private Node insertOrUpdateTagNode(Node tag, String namespacePrefix, String destinationPath, String title, HashMap properties, ResourceResolver resourceResolver, Session session, TagManager tagmanager) throws ImportLibraryException {

        if( StringUtils.isBlank(title))
            return null;
        Node n = tag;
        try{
            if( n == null){
                String name = getNodeName(title);
                Tag tagChild = tagmanager.createTag(namespacePrefix + name, title, null,true);
                n = tagChild.adaptTo(Node.class);


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
            session.save();
        } catch (RepositoryException | InvalidTagFormatException e) {
            throw new ImportLibraryException(e.getMessage());
        }
        return n;
    }

    private void handleValueTag(String parentTagTitle, String values,ResourceResolver resourceResolver, Session session, TagManager tagmanager){

        List<String> convertedList = parseValues(parentTagTitle, values);

        if(convertedList != null && convertedList.size() > 0){
            String parentTagName = getNodeName(parentTagTitle);
            //check tag parent with name "parentTagName"
            //Node parentTagNode = getNode("[cq:Tag]", parentTagName, servletConfig.getTagsRootPath(), true);unused
            Resource parentRc = resourceResolver.getResource(importerConfig.getTagsRootPath()+"/"+parentTagName);
            Node parentTagNode = parentRc != null ? parentRc.adaptTo(Node.class):null;

            //Eventuale creazione dei tag "parent" se non esistono su crx ( author, topic ,source ,generic-tags, typology,..)
            if(parentTagNode == null){
                HashMap properties = new HashMap();
                properties.put(JcrConstants.JCR_TITLE, parentTagTitle);
                properties.put(StringConstants.SLING_RESOURCE_TYPE, TAG_RESOURCE_TYPE);
                try {
                    parentTagNode = insertOrUpdateTagNode(null, "house-of-science:", importerConfig.getTagsRootPath(), parentTagTitle ,properties, resourceResolver, session, tagmanager );
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
                            String tagContainerName = null;
                            String destinationPath = parent.getPath();
                            if("author".equals(parentTagTitle)){
                                //i valori del tag author si storicizzano sotto folder aventi l'iniziale dell'autore
                                tagContainerName = tagVal.substring(0,1).toLowerCase();
                                destinationPath = parent.getPath()+"/"+tagContainerName;
                                //Node containerTagNode  = getNode("[cq:Tag]", tagContainerName,  parent.getPath(),false); unused
                                Resource rcTC = resourceResolver.getResource(destinationPath);
                                Node containerTagNode= rcTC != null ? rcTC.adaptTo(Node.class):null;

                                if(containerTagNode == null){
                                    HashMap properties = new HashMap();
                                    properties.put(JcrConstants.JCR_TITLE, tagContainerName);
                                    properties.put(StringConstants.SLING_RESOURCE_TYPE, TAG_RESOURCE_TYPE);
                                    insertOrUpdateTagNode(null, "house-of-science:author/", parent.getPath(),  tagContainerName , properties, resourceResolver, session, tagmanager);
                                }

                            }

                            HashMap properties = new HashMap();
                            properties.put(JcrConstants.JCR_TITLE, tagVal);
                            properties.put(StringConstants.SLING_RESOURCE_TYPE, "cq/tagging/components/tag");
                            insertOrUpdateTagNode(valueNode,"house-of-science:"+parentTagName+"/"+(StringUtils.isNotBlank(tagContainerName) ? tagContainerName+"/" :""),destinationPath,  tagVal, properties, resourceResolver, session, tagmanager);
                        }
                    } catch (RepositoryException | ImportLibraryException e) {
                        addErrors(e.getMessage());
                        return;
                    }

                });
            }


        }
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
        label = label.replaceAll("[\\?]","-");
        label = label.replaceAll("[\\%]","-");
        return JcrUtil.escapeIllegalJcrChars(label);
    }

    private ContentFragmentModel generateContentFragmentFromRow(Row row, ResourceResolver resourceResolver) throws ImportLibraryException {
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
                Resource rc = resourceResolver.getResource(importerConfig.getTagsRootPath()+"/author");
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
                Resource rc = resourceResolver.getResource(importerConfig.getTagsRootPath()+"/source");
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
                Resource rc = resourceResolver.getResource(importerConfig.getTagsRootPath()+"/topic");
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
                Resource rc = resourceResolver.getResource(importerConfig.getTagsRootPath()+"/generic-tags");
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
                Resource rc = resourceResolver.getResource(importerConfig.getTagsRootPath()+"/typology");
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

    @Reference
    ResourceResolverFactory resolverFactory;
    public ResourceResolver getResourceResolver() {
        ResourceResolver resolver = null;
        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, "mhos");
        try {
            resolver = resolverFactory.getServiceResourceResolver(param);
        } catch (Exception e) {
            logger.error("Error retrieving resolver with system user", e);

        }
        return resolver;
    }

    private void cleanAuthorValueTag(){
        logger.info("********** Start cleanAuthorValueTag********************");

        ResourceResolver resolver = getResourceResolver();
        Session session = resolver.adaptTo(Session.class);
        TagManager tagmanager = resolver.adaptTo(TagManager.class);
        Externalizer externalizer = resolver.adaptTo(Externalizer.class);
        String hostname =  externalizer.authorLink(resolver, "/");

        try {
            //Si controlla la presenza di nodi sotto /content/cq:tag/author di tipo cq:tag con name .length >1 ( in questo caso si escludono i tagcontainer di nome A,B,C,D...)
            String authorTagPath = importerConfig.getTagsRootPath() + "/author";
            Resource resource = resolver.getResource(authorTagPath);
            Iterator<Resource> children = resource.listChildren();
            String tagNamespacePrefix = "house-of-science:author/";

            String prevTagId = null; //memorizza il tagID, namespace, del nodo oggetto di analisi prima della migrazione sotto il tagContainer di competenza A,B,C,D...
            String tagContainerName = "";
            while (children.hasNext()) {
                Resource child = children.next();
                String childName = child.getName();
                //Si controlla se il nodo tag ha la proprietà "cq:movedTo" settata-> ciò vuol dire che è già stato gestito e referenzia già il nuovo tag
                Node childNode = child.adaptTo(Node.class);

                if (childName.length() > 1 && !childNode.hasProperty(CQMOVETO_PROPERTY)) {

                    //Si recupera il namespace/tagId corrente del tag da spostare
                    Tag tagChild = child.adaptTo(Tag.class);
                    prevTagId = tagChild.getTagID();


                    //Si sposta il nodo nel tagcontainer nuova destinazione
                    tagContainerName = childName.substring(0, 1).toLowerCase(); //si recupera la prima iniziale dal nome del nodo da spostare
                    String destinationPath = authorTagPath + "/" + tagContainerName;
                    //NB: Francesca-> provando il metodo seguente per il recupero dell'eventuale tagcontainer, se già presente su CRX, alle volte
                    //non si ottiene un dato consistente, in quanto sembra che il crx non sia aggiornato con gli ultimi tagcontainer aggiunti durante
                    //l'elaborazione della servlet. Per tale motivo tale f() è commentata a favore del punto 2.
                    //Node containerTagNode  = getNode("[cq:Tag]",tagContainerName,  authorTagPath,false)
                    //2.
                    Resource rc = resolver.getResource(destinationPath);
                    Node containerTagNode = rc != null ? rc.adaptTo(Node.class) : null;

                    //Si deve creare il tag container A,B,C,....
                    if (containerTagNode == null) {
                        HashMap properties = new HashMap();
                        properties.put(JcrConstants.JCR_TITLE, tagContainerName);
                        properties.put(StringConstants.SLING_RESOURCE_TYPE, TAG_RESOURCE_TYPE);
                        try {
                            insertOrUpdateTagNode(null, tagNamespacePrefix, authorTagPath, tagContainerName, properties, resolver, session, tagmanager);
                        } catch (ImportLibraryException e) {
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

                        Tag newTag = tagmanager.moveTag(tagChild,destinationPath+"/" + childName );

                        //1.
                        String newTagNamespace = StringUtils.substringBeforeLast(prevTagId, "/") + "/" + tagContainerName + "/" + childName;

                        //2.
                        RangeIterator<Resource> res = tagmanager.find(prevTagId);
                        if (res != null) {
                            while (res.hasNext()) {
                                Resource masterNodeResource = res.next(); //è il nodo master sotto <Content_Fragment>/jcr:content/data
                                String jsonPath = masterNodeResource.getPath().replace(importerConfig.getDamRootPath(), "");
                                jsonPath = jsonPath.replace("/jcr:content/data/master", "");
                                jsonPath = jsonPath + ".json";

                                ContentFragmentModel cf = cfApi.getByPath(hostname, jsonPath);
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

                                    String targetPath = masterNodeResource.getPath().replace(importerConfig.getDamRootPath(), "");
                                    targetPath = targetPath.replace("/jcr:content/data/master", "");

                                    cfApi.put(hostname, targetPath, cf);

                                    //Tag[] currentTags = tagmanager.getTags(masterNodeResource);
                                    //tagmanager.setTags(masterNodeResource,currentTags);


                                }

                            }
                        }

                        //4.
                        /*session.move(tagChild.getPath(), destinationPath + "/" + childName);
                        session.refresh(true);
                        session.save();
                        */
                    } /*catch (RepositoryException e) {
                        addErrors(e.getMessage());

                    } */catch (TagException e) {
                        addErrors(e.getMessage());
                    } catch (InvalidTagFormatException e) {
                        addErrors(e.getMessage());
                    }


                }
            }

        } catch (RepositoryException e) {
            addErrors(e.getMessage());
        } finally {
            if (resolver != null) {
                resolver.close();
            }
        }
        logger.info("********** END cleanAuthorValueTag********************");
    }




    private void importTagsData(InputStream inputStream) {
        logger.info("Start import tags data********************");
        XSSFWorkbook workbook = null;

        ResourceResolver resolver = getResourceResolver();
        Session session = resolver.adaptTo(Session.class);
        TagManager tagmanager = resolver.adaptTo(TagManager.class);
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
                logger.info("ROW " + row.getRowNum() + " **********");
                //ignoro l'header se il file excel la prevede
                if(importerConfig.isHeaderRowPresent() && count == 0);
                else{

                    Cell authorCell = row.getCell(AUTHOR_COL_INDEX);
                    if(authorCell != null) {
                        logger.info("\t author **********");
                        String value = authorCell.getStringCellValue();
                        handleValueTag( "author", value, resolver, session, tagmanager );
                    }

                    Cell topicCell = row.getCell(TOPIC_TAGS_COL_INDEX);
                    if(topicCell != null) {
                        logger.info("\t topic  **********");
                        String value = topicCell.getStringCellValue();
                        handleValueTag("topic", value, resolver, session, tagmanager );
                    }

                    Cell sourceCell = row.getCell(SOURCE_TAGS_COL_INDEX);
                    if(sourceCell != null) {
                        logger.info("\t source **********");
                        String value = sourceCell.getStringCellValue();
                        handleValueTag("source", value, resolver, session, tagmanager );
                    }

                    Cell genericTagsCell = row.getCell(GENERIC_TAGS_COL_INDEX);
                    if(genericTagsCell != null) {
                        logger.info("\t generic tags**********");
                        String value = genericTagsCell.getStringCellValue();
                        handleValueTag("generic tags", value, resolver, session, tagmanager );
                    }


                    Cell typologyCell = row.getCell(TYPOLOGY_TAGS_COL_INDEX);
                    if(typologyCell != null) {
                        logger.info("\t typology **********");
                        String value = typologyCell.getStringCellValue();
                        handleValueTag("typology", value, resolver, session, tagmanager );
                    }
                }
                count++;
            }


            resetInputStream(inputStream);

        } catch (IOException e) {
            addErrors(e.getMessage());

        }finally{
            if (resolver != null) {
                resolver.close();
            }
        }


        logger.info("End import tags data******************************");


    }




    private void importArticlesData(InputStream inputStream) {
        logger.info("Start import Articles data********************");

        ResourceResolver resolver = getResourceResolver();
        Session session = resolver.adaptTo(Session.class);
        Externalizer externalizer = resolver.adaptTo(Externalizer.class);
        String hostname =  externalizer.authorLink(resolver, "/");
        TagManager tagmanager = resolver.adaptTo(TagManager.class);

        HashMap<String, ContentFragmentModel> cfMap = new HashMap<String, ContentFragmentModel>();

        // Si crea la folder della categoria, se non esiste,  per la storicizzazione dei content fragment relativi agli articoli
        try {
            Resource categoryFolder =  resolver.getResource(importerConfig.getCategoryPath());
            if(categoryFolder == null) {

                Node n = createNode(importerConfig.getCategoryPath(), "sling:Folder", session);
                Node jcr = createNode(importerConfig.getCategoryPath()+"/jcr:content", JcrConstants.NT_UNSTRUCTURED, session);
                if(jcr != null) {
                    jcr.setProperty("title", "Microbiology & Infectious Disease");
                    jcr.setProperty("source", "false");
                    session.refresh(true);
                    session.save();
                }
            }
        } catch (RepositoryException e) {
            addErrors("Error in creating category folder at "+ importerConfig.getCategoryPath());
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
                if (importerConfig.isHeaderRowPresent() && count == 0) ;
                else {
                    ContentFragmentModel cf = generateContentFragmentFromRow(row, resolver);

                    if (cf != null) {
                        //si controlla l'esistenza del content fragment su crx sotto la folder della categoria
                        String cfName = getNodeName(cf.getProperties().getTitle());
                        //Node n = getNode("[dam:Asset]", cfName, servletConfig.getCategoryPath(), true);unused

                        //i cf si storicizzano sotto folder aventi come nome, l'anno di pubblicazione
                        String authorDateStr = (String) cf.getProperties().getElements().getArticleDate().getValue();
                        LocalDate date = LocalDate.parse(authorDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T00:00:00.000Z'"));


                        String year ="" + date.getYear();
                        String folderPath = importerConfig.getCategoryPath() + "/" + year;

                        String  cfResourcePath = importerConfig.getCategoryPath()+"/"+ year+"/"+cfName;
                        Resource rCF = resolver.getResource(cfResourcePath);
                        Node n = rCF != null ? rCF.adaptTo(Node.class) : null;

                        if (n != null) {
                            String targetPath = StringUtils.replace(cfResourcePath, importerConfig.getDamRootPath() + "/", "");
                            storeContentFragment(false, hostname, targetPath, cf);

                        } else {

                            //se la folder esiste già non si crea, altrimenti sì
                            Resource folder = resolver.getResource(folderPath);
                            if (folder == null) {
                                //JcrUtil.createPath(folderPath, "sling:Folder", getCustomSession(resourceResolver));
                                createNode(folderPath, "sling:Folder", session);
                                session.refresh(true);
                                session.save();
                            }
                            String targetPath = StringUtils.replace(cfResourcePath, importerConfig.getDamRootPath() + "/", "") ;

                            storeContentFragment(true,  hostname, targetPath, cf);

                        }

                        cfMap.put(cfResourcePath, cf);
                    }
                }
                count++;
            }

            if(cfMap.size() >0){
                session.refresh(true);
                cfMap.forEach((cfResourcePath, cf) -> {
                    //settaggio dei tags
                    //1. si recuperano i tagID === namespace dei tag dell'elemento content fragment
                    List<Object> allTagIds =  new ArrayList<Object>();
                    if(cf.getProperties().getElements().getTags()!= null && cf.getProperties().getElements().getTags().getValue() != null)
                        allTagIds.addAll(cf.getProperties().getElements().getTags().getValue());
                    if(cf.getProperties().getElements().getAuthor()!= null &&cf.getProperties().getElements().getAuthor().getValue() != null)
                        allTagIds.addAll(cf.getProperties().getElements().getAuthor().getValue());
                    if(cf.getProperties().getElements().getSource()!= null && cf.getProperties().getElements().getSource().getValue() != null)
                        allTagIds.addAll( cf.getProperties().getElements().getSource().getValue());
                    if(cf.getProperties().getElements().getTopic()!= null &&cf.getProperties().getElements().getTopic().getValue() != null)
                        allTagIds.addAll(cf.getProperties().getElements().getTopic().getValue());
                    if(cf.getProperties().getElements().getTypology()!= null &&cf.getProperties().getElements().getTypology().getValue() != null)
                        allTagIds.addAll(cf.getProperties().getElements().getTypology().getValue());

                    if(allTagIds != null && allTagIds.size() >0) {
                        //2.si recuperano tutti gli elementi di tipo Tag sulla base del TagId
                        Tag[] allTags = allTagIds.stream().map(tagId->{
                            return tagmanager.resolve((String)tagId);
                        }).toArray(Tag[]::new);
                        //3.si recupera il content fragment appena creato/modificato
                        Resource masterNodeResource = resolver.getResource(cfResourcePath+"/jcr:content/data/master");
                        //4. si associano i tags al content fragment
                        if(masterNodeResource != null)
                            tagmanager.setTags(masterNodeResource, allTags);
                        else
                            addErrors("Impostare cq:tags per "+cfResourcePath);
                    }
                });


            }
        } catch (IOException | RepositoryException | ImportLibraryException e) {
            addErrors(e.getMessage());
            return;
        }
        finally{
            if (resolver != null) {
                resolver.close();
            }
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            addErrors(e.getMessage());
        }
        logger.info("End import Articles data******************************");
    }


    public class Response{
        private String result;
        private List<String> errors;

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

    }

    //metodi per gestione errori
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
}

    

   