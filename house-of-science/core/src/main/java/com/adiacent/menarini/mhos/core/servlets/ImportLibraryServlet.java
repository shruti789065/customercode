package com.adiacent.menarini.mhos.core.servlets;


import com.adiacent.menarini.mhos.core.business.ContentFragmentApi;
import com.adiacent.menarini.mhos.core.models.*;
import com.adiacent.menarini.mhos.core.resources.ImportLibraryResource;
import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.dam.print.ids.StringConstants;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.dam.api.Asset;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagException;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.commons.lang3.StringUtils;
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
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.ss.usermodel.Cell;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.Servlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;



//@Designate(ocd = ImportLibraryServlet.Config.class)
@Component(service = { Servlet.class }, name = "Menarini House of Science - Import Library Servlet", immediate = true)
@SlingServletResourceTypes(
        resourceTypes = NameConstants.NT_PAGE,
        methods= {HttpConstants.METHOD_GET},
        extensions= "json",
        selectors={ImportLibraryServlet.DEFAULT_SELECTOR})
@ServiceDescription("Menarini House of Science - Import Library Servlet")

public class ImportLibraryServlet extends SlingSafeMethodsServlet {

    private transient final Logger LOG = LoggerFactory.getLogger(this.getClass());
    public static final String DEFAULT_SELECTOR = "importLibrary";

    //private Config servletConfig;

    private transient Page currentPage = null;

    ResourceResolver resourceResolver = null;
    ImportLibraryResource.Config servletConfig = null;
    /* @Activate
    @Modified
    protected void activate(final Config config) {
        LOG.info("Activating Import Library Servlet");

        servletConfig = config;

        LOG.debug("Config dam folder {} : ", servletConfig.getFileDAMFolder());
        LOG.debug("Config debug tag enabled {} :", servletConfig.isImportArticleEnabled());
        LOG.debug("Config debug article enabled {} :", servletConfig.isImportTagEnabled());

    }*/

    //indice colonne del file excel dalle quali recuperare le informazioni
    private static Integer TYPOLOGY_TAGS_COL_INDEX = 1;
    private static Integer TOPIC_TAGS_COL_INDEX = 2;
    private static Integer SOURCE_TAGS_COL_INDEX = 2;
    private static Integer AUTHOR_COL_INDEX = 5;
    private static Integer GENERIC_TAGS_COL_INDEX = 6;


    private static Integer TITLE_COL_INDEX = 3;
    private static Integer AUTHOREDATE_COL_INDEX = 0;

    private static Integer DESCRIPTION_COL_INDEX = 4;

    private static Integer LINK_COL_INDEX = 7;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response){
        try{
            Resource currentResource = request.getResource();
            resourceResolver = currentResource.getResourceResolver();
            Session session = resourceResolver.adaptTo(Session.class);

            servletConfig = ImportLibraryResource.get_instance().getConfig();

            //recupero file excel dal dam
            Resource rs = resourceResolver.getResource(servletConfig.getFileDAMFolder()+"/"+servletConfig.getFileName());
            if(rs == null)
                return;

            Asset asset = rs.adaptTo(Asset.class);
            InputStream inputStream = asset.getOriginal().adaptTo(InputStream.class);

            if(servletConfig.isImportTagEnabled()){
                importTagsData(inputStream, session);
            }


            if(servletConfig.isImportArticleEnabled()){
                importArticlesData(inputStream, request.getServerName(), request.getServerPort(), session);
            }



           /* if (pageManager != null) {
                currentPage = pageManager.getContainingPage(currentResource.getPath());
            }
            if(currentPage != null){
                JSONArray jsonArray = getResult(request, resourceResolver);
                response.setContentType("application/json");
                response.getWriter().print(jsonArray);
            }

            */
        }catch (Exception e){
            LOG.error("Error in search results Get call: ", e);
        }
    }


    private Node getNode(String nodeType, String nodeName, String searchPath, boolean searchForDescendant){
        if(StringUtils.isBlank(nodeName))
            return null;
        if(StringUtils.isBlank(searchPath))
            return null;

        try {
            StringBuilder myXpathQuery;
            myXpathQuery = new StringBuilder();
            myXpathQuery.append("SELECT * FROM " + nodeType +"  as p ");
            if(searchForDescendant)
            myXpathQuery.append("WHERE ISDESCENDANTNODE('" + searchPath + "') ");
            else
                myXpathQuery.append(" WHERE ISCHILDNODE(p, '" + searchPath + "')");
            myXpathQuery.append(" AND NAME() = '"+ nodeName+"'");

            Session session = resourceResolver.adaptTo(Session.class);
            QueryManager queryManager  = session.getWorkspace().getQueryManager();

            Query query = queryManager.createQuery(myXpathQuery.toString(), Query.JCR_SQL2);
            QueryResult queryResult = query.execute();
            NodeIterator item = queryResult.getNodes();
            if (item.hasNext()){
                Node node = item.nextNode();
                return node;

            }
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private Node insertOrUpdateTagNode(Node tag, String destinationPath, String name, String title, HashMap properties ){

        if( StringUtils.isBlank(title))
            return null;
        Node n = tag;
        try{
            if( n == null){
                TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
                Tag tagChild = tagManager.createTag(name, title, null,false);
                tagManager.moveTag(tagChild,destinationPath+"/"+name );
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
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        } catch (InvalidTagFormatException e) {
            throw new RuntimeException(e);
        } catch (TagException e) {
            throw new RuntimeException(e);
        }
        return n;
    }
    private void handleTag(String tagName, String tagTitle, List<String> values){
        //check tag parent "tagName"
        Node parentTagNode = getNode("[cq:Tag]",tagName, servletConfig.getTagsRootPath(), false);
        if(parentTagNode == null){
            HashMap properties = new HashMap();
            properties.put(JcrConstants.JCR_TITLE, tagTitle);
            properties.put(StringConstants.SLING_RESOURCE_TYPE, "cq/tagging/components/tag");
            parentTagNode = insertOrUpdateTagNode(null, servletConfig.getTagsRootPath(), tagName,tagTitle ,properties );
        }

        if(values != null && values.size() > 0 && parentTagNode != null){
            Node parent = parentTagNode;
            values.stream().forEach(v->{
                String tgName = StringUtils.replace(v.toLowerCase().trim(), " ", "-");
                Node childTagNode = null;
                try {
                    childTagNode = getNode("[cq:Tag]",tgName,  parent.getPath(), true);

                    if(childTagNode == null){
                        HashMap properties = new HashMap();
                        properties.put(JcrConstants.JCR_TITLE, v);
                        properties.put(StringConstants.SLING_RESOURCE_TYPE, "cq/tagging/components/tag");
                        childTagNode  = insertOrUpdateTagNode(childTagNode, parent.getPath(), tgName, v,properties );
                    }
                } catch (RepositoryException e) {
                    throw new RuntimeException(e);
                }

            });
        }
    }

    private void handleAuthorTag(String value){

        if(StringUtils.isNotBlank(value)){
            //si usa la virgola come separatore
            List<String> convertedList = Stream.of(value.split(",", -1))
                    .map(a->{
                        String res= null;
                        res = a.toLowerCase().replaceAll("mr|md|phd|ms(\\w+[;]{0,1})", "");
                        res=StringUtils.trimToEmpty(res);
                        return res;
                    })
                    .filter(e-> StringUtils.isNotBlank(e))
                    .collect(Collectors.toList());
            if(convertedList != null && convertedList.size() > 0)
                handleTag("author", "author", convertedList);
        }

    }


    private void handleTopicTag(String value){
        if(StringUtils.isNotBlank(value)){
            //si usa la virgola come separatore
            List<String> convertedList = Stream.of(value.split(",", -1))
                    .map(a->{
                        String res= null;
                        res = a.toLowerCase().trim();
                        return res;
                    })
                    .filter(e-> StringUtils.isNotBlank(e))
                    .collect(Collectors.toList());
            if(convertedList != null && convertedList.size() > 0)
                handleTag("topic", "topic", convertedList);
        }
    }

    private void handleSourceTag(String value){
        if(StringUtils.isNotBlank(value)){
            //si usa la virgola come separatore
            List<String> convertedList = Stream.of(value.split(",", -1))
                    .map(a->{
                        String res= null;
                        res = a.toLowerCase().trim();
                        return res;
                    })
                    .filter(e-> StringUtils.isNotBlank(e))
                    .collect(Collectors.toList());
            if(convertedList != null && convertedList.size() > 0)
                handleTag("source", "source", convertedList);
        }
    }

    private void handleGenericTags(String value){
        if(StringUtils.isNotBlank(value)){
            //si usa la virgola come separatore
            List<String> convertedList = Stream.of(value.split(",", -1))
                    .map(a->{
                        String res= null;
                        res = a.toLowerCase().trim();
                        return res;
                    })
                    .filter(e-> StringUtils.isNotBlank(e))
                    .collect(Collectors.toList());
            if(convertedList != null && convertedList.size() > 0)
                handleTag("generic-tags", "generic-tags", convertedList);
        }
    }

    private void handleTypologyTag(String value){
        if(StringUtils.isNotBlank(value)){
            //si usa la virgola come separatore
            List<String> convertedList = Stream.of(value.split(",", -1))
                    .map(a->{
                        String res= null;
                        res = a.toLowerCase().trim();
                        return res;
                    })
                    .filter(e-> StringUtils.isNotBlank(e))
                    .collect(Collectors.toList());
            if(convertedList != null && convertedList.size() > 0)
                handleTag("typology", "typology", convertedList);
        }
    }

    private void importTagsData(InputStream inputStream, Session session) {
        LOG.debug("Start import tags data********************");
        if(inputStream == null)
            return;

        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);     //creating a Sheet object to retrieve object

            Iterator<Row> itr = sheet.iterator();    //iterating over excel file
            int count = 0;
            while (itr.hasNext()) {
                Row row = itr.next();
                //ignoro l'header se il file excel la prevede
                if(servletConfig.isHeaderRowPresent() && count == 0);
                else{

                    Cell authorCell = row.getCell(AUTHOR_COL_INDEX);
                    if(authorCell != null) {
                        String value = authorCell.getStringCellValue();
                        handleAuthorTag( value );
                    }

                    Cell topicCell = row.getCell(TOPIC_TAGS_COL_INDEX);
                    if(topicCell != null) {
                        String value = topicCell.getStringCellValue();
                        handleTopicTag( value );
                    }

                    Cell sourceCell = row.getCell(SOURCE_TAGS_COL_INDEX);
                    if(sourceCell != null) {
                        String value = sourceCell.getStringCellValue();
                        handleSourceTag( value );
                    }

                    Cell genericTagsCell = row.getCell(GENERIC_TAGS_COL_INDEX);
                    if(genericTagsCell != null) {
                        String value = genericTagsCell.getStringCellValue();
                        handleGenericTags( value );
                    }


                    Cell typologyCell = row.getCell(TYPOLOGY_TAGS_COL_INDEX);
                    if(typologyCell != null) {
                        String value = typologyCell.getStringCellValue();
                        handleTypologyTag( value );
                    }
                }
                count++;
            }

            inputStream.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LOG.debug("End import tags data******************************");
    }


    private void importArticlesData(InputStream inputStream, String serverName, int serverPort, Session session) {
        LOG.debug("Start import Articles data********************");
        if(inputStream == null)
            return;

        // Si crea la folder infettivology se non esiste


//Create the Node

        try {
            Resource categoryFolder =  resourceResolver.getResource("/content/dam/mhos/content-fragments/en/plutology");
            if(categoryFolder == null) {
                JcrUtil.createPath("/content/dam/mhos/content-fragments/en/plutology", JcrConstants.NT_FOLDER, session);
                session.save();
            }
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }





        ContentFragmentApi cfApi = new ContentFragmentApi();
        ContentFragmentModel cf = cfApi.getByPath(serverName, serverPort, "mhos/content-fragments/en/internal-medicine/obesity-and-weight-management-for-the-prevention-and-treatment-of-type-2-diabetes--standards-of-care-in-diabetes-2023.json");
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);     //creating a Sheet object to retrieve object

            Iterator<Row> itr = sheet.iterator();    //iterating over excel file
            int count = 0;
            while (itr.hasNext()) {
                Row row = itr.next();
                //ignoro l'header se il file excel la prevede
                if(servletConfig.isHeaderRowPresent() && count == 0);
                else{
                    cf  = generateContentFragmentFromRow(row);

                    if(cf != null) {
                        String folderPath= "mhos/content-fragments/en/plutology";
                        //si controlla l'esistenza del content fragment su crx


                        Node n = getNode("[dam:Asset]",getNodeName(cf.getProperties().getTitle()), servletConfig.getDamRootPath()+"/"+folderPath ,true);
                        if(n != null){
                            cfApi.put(serverName, serverPort, folderPath, cf);
                        }
                        else
                            cfApi.create(serverName, serverPort, folderPath, cf);

                    }
                }
                count++;
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LOG.debug("End import Articles data******************************");
    }

    private void storeContentFragment(String serverName, int serverPort, String folderPath, ContentFragmentModel cf) {


    }

    private ContentFragmentModel generateContentFragmentFromRow(Row row) {
        ContentFragmentModel res = new ContentFragmentModel();
        res.setProperties(new ContentFragmentPropertiesModel());

        res.getProperties().setCqModel("/conf/mhos/settings/dam/cfm/models/result-fragment");
        res.getProperties().setElements(new ContentFragmentElements());



        Cell cell = row.getCell(TITLE_COL_INDEX);
        if(cell != null) {
            String value = cell.getStringCellValue().replace("/"," ").trim();
            res.getProperties().setTitle(value);
        }



        cell = row.getCell(AUTHOREDATE_COL_INDEX);
        if(cell != null) {
            Date d = cell.getDateCellValue();
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T00:00:00.000Z'");
            //isoFormat.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
            String dateStr = isoFormat.format(d);
            ContentFragmentElementSingleValue aDate = new ContentFragmentElementSingleValue();
            aDate.setType("Date");
            aDate.setValue(dateStr);
            res.getProperties().getElements().setArticleDate( aDate );
        }


        cell = row.getCell(DESCRIPTION_COL_INDEX);
        if(cell != null) {
            String value = cell.getStringCellValue().trim();
            ContentFragmentElementSingleValue desc = new ContentFragmentElementSingleValue();
            desc.setType("text/html");
            desc.setValue(value);
            res.getProperties().getElements().setDescription(desc);
        }


        cell = row.getCell(LINK_COL_INDEX);
        if(cell != null) {
            String value = cell.getStringCellValue().trim();
            if(StringUtils.isNotBlank(value)){
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
        TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        if(cell != null) {
            String values = cell.getStringCellValue();
            //pulisco i valori della cella
            List<String> authorList = parseValues("author", values);
            Node parentTagNode = getNode("[cq:Tag]","author", servletConfig.getTagsRootPath(), false);
            if(authorList != null && authorList.size() >0){
                List tgs = authorList.stream().map(a->{
                    //controllo se il valore i-esimo dell'authore esiste come valore possibile del tag AUTHOR
                    try {
                        String tagPath  = parentTagNode.getPath()+"/"+getNodeName(a);
                        Resource tagResource = resourceResolver.getResource(tagPath);
                        Tag t = tagResource != null ? tagResource.adaptTo(Tag.class): null;

                        if(t == null) return null;// il tag con quel valore di author non esiste cosa si fa? si ferma tutto???
                       else return t.getTagID();

                    } catch (RepositoryException e) {
                        throw new RuntimeException(e);
                    }
                }).filter(e->e!=null).collect(Collectors.toList());
                if(tgs!= null && tgs.size()>0){
                    ContentFragmentElementValue author = new ContentFragmentElementValue();
                    author.setType("string");
                    author.setValue(tgs);
                    res.getProperties().getElements().setAuthor(author);
                }
            }
        }


        cell = row.getCell(SOURCE_TAGS_COL_INDEX);
        if(cell != null) {
            String values = cell.getStringCellValue();
            //pulisco i valori della cella
            List<String> sourceList = parseValues("source", values);
            Node parentTagNode = getNode("[cq:Tag]","source", servletConfig.getTagsRootPath(), false);
            if(sourceList != null && sourceList.size() >0){
                List tgs = sourceList.stream().map(a->{
                    //controllo se il valore i-esimo della source esiste come valore possibile del tag SOURCE
                    try {
                        String tagPath  = parentTagNode.getPath()+"/"+getNodeName(a);
                        Resource tagResource = resourceResolver.getResource(tagPath);
                        Tag t = tagResource != null ? tagResource.adaptTo(Tag.class): null;

                        if(t == null) return null;// il tag con quel valore di author non esiste cosa si fa? si ferma tutto???
                        else return t.getTagID();

                    } catch (RepositoryException e) {
                        throw new RuntimeException(e);
                    }
                }).filter(e->e!=null).collect(Collectors.toList());
                if(tgs!= null && tgs.size()>0){
                    ContentFragmentElementValue src = new ContentFragmentElementValue();
                    src.setType("string");
                    src.setValue(tgs);
                    res.getProperties().getElements().setSource(src);
                }
            }
        }


        cell = row.getCell(TOPIC_TAGS_COL_INDEX);
        if(cell != null) {
            String values = cell.getStringCellValue();
            //pulisco i valori della cella
            List<String> sourceList = parseValues("topic", values);
            Node parentTagNode = getNode("[cq:Tag]","topic", servletConfig.getTagsRootPath(), false);
            if(sourceList != null && sourceList.size() >0){
                List tgs = sourceList.stream().map(a->{
                    //controllo se il valore i-esimo del topic esiste come valore possibile del tag TOPIC
                    try {
                        String tagPath  = parentTagNode.getPath()+"/"+getNodeName(a);
                        Resource tagResource = resourceResolver.getResource(tagPath);
                        Tag t = tagResource != null ? tagResource.adaptTo(Tag.class): null;

                        if(t == null) return null;// il tag con quel valore di author non esiste cosa si fa? si ferma tutto???
                        else return t.getTagID();

                    } catch (RepositoryException e) {
                        throw new RuntimeException(e);
                    }
                }).filter(e->e!=null).collect(Collectors.toList());
                if(tgs!= null && tgs.size()>0){
                    ContentFragmentElementValue tpc = new ContentFragmentElementValue();
                    tpc.setType("string");
                    tpc.setValue(tgs);
                    res.getProperties().getElements().setTopic(tpc);
                }
            }
        }


        cell = row.getCell(GENERIC_TAGS_COL_INDEX);
        if(cell != null) {
            String values = cell.getStringCellValue();
            //pulisco i valori della cella
            List<String> sourceList = parseValues("generic-tags", values);
            Node parentTagNode = getNode("[cq:Tag]","generic-tags", servletConfig.getTagsRootPath(), false);
            if(sourceList != null && sourceList.size() >0){
                List tgs = sourceList.stream().map(a->{
                    //controllo se il valore i-esimo del generic-tags esiste come valore possibile del tag GENERIC-TAGS
                    try {
                        String tagPath  = parentTagNode.getPath()+"/"+getNodeName(a);
                        Resource tagResource = resourceResolver.getResource(tagPath);
                        Tag t = tagResource != null ? tagResource.adaptTo(Tag.class): null;

                        if(t == null) return null;// il tag con quel valore di author non esiste cosa si fa? si ferma tutto???
                        else return t.getTagID();

                    } catch (RepositoryException e) {
                        throw new RuntimeException(e);
                    }
                }).filter(e->e!=null).collect(Collectors.toList());
                if(tgs!= null && tgs.size()>0){
                    ContentFragmentElementValue gtg = new ContentFragmentElementValue();
                    gtg.setType("string");
                    gtg.setValue(tgs);
                    res.getProperties().getElements().setTags(gtg);
                }
            }
        }


        cell = row.getCell(TYPOLOGY_TAGS_COL_INDEX);
        if(cell != null) {
            String values = cell.getStringCellValue();
            //pulisco i valori della cella
            List<String> sourceList = parseValues("typology", values);
            Node parentTagNode = getNode("[cq:Tag]","typology", servletConfig.getTagsRootPath(), false);
            if(sourceList != null && sourceList.size() >0){
                List tgs = sourceList.stream().map(a->{
                    //controllo se il valore i-esimo del typology esiste come valore possibile del tag TYPOLOGY
                    try {
                        String tagPath  = parentTagNode.getPath()+"/"+getNodeName(a);
                        Resource tagResource = resourceResolver.getResource(tagPath);
                        Tag t = tagResource != null ? tagResource.adaptTo(Tag.class): null;

                        if(t == null) return null;// il tag con quel valore di author non esiste cosa si fa? si ferma tutto???
                        else return t.getTagID();

                    } catch (RepositoryException e) {
                        throw new RuntimeException(e);
                    }
                }).filter(e->e!=null).collect(Collectors.toList());
                if(tgs!= null && tgs.size()>0){
                    ContentFragmentElementValue ttg = new ContentFragmentElementValue();
                    ttg.setType("string");
                    ttg.setValue(tgs);
                    res.getProperties().getElements().setTypology(ttg);
                }
            }
        }

        return res;
    }



    private List<String> parseValues(String rootTag, String values){

        if("author".equals(rootTag)) {
            if (StringUtils.isNotBlank(values)) {
                //si usa la virgola come separatore
                List<String> convertedList = Stream.of(values.split(",", -1))
                        .map(a -> {
                            String res = null;
                            res = a.toLowerCase().replaceAll("mr|md|phd|ms(\\w+[;]{0,1})", "");
                            res = StringUtils.trimToEmpty(res);
                            return res;
                        })
                        .filter(e -> StringUtils.isNotBlank(e))
                        .collect(Collectors.toList());
                return convertedList;
            }
        }

        else { // same as topics, source, generic-tags, typology
            if (StringUtils.isNotBlank(values)) {
                //si usa la virgola come separatore
                List<String> convertedList = Stream.of(values.split(",", -1))
                        .map(a -> {
                            String res = StringUtils.trimToEmpty(a).toLowerCase();
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
        return label.toLowerCase().replaceAll(" ","-").trim();
}
    /*protected JSONArray getResult(SlingHttpServletRequest request, ResourceResolver resourceResolver) throws RepositoryException, JSONException {
        JSONArray results = new JSONArray();

            keyword = request.getParameter("fulltext");
            cfCategory = request.getParameter("category");
            String siteName = request.getResource().getPath().split("/")[2];
            String language = request.getResource().getPath().split("/")[4];
            Resource resourceDam = null;
            StringBuilder myXpathQuery;

            if(cfCategory != null && keyword == null){
                resourceDam = resourceResolver.getResource("/content/dam/" + siteName + "/content-fragments/" + language+  "/" + cfCategory);
                if(resourceDam != null){
                    Iterator<Resource> resouceCategory =  resourceDam.listChildren();
                    while (resouceCategory.hasNext()){
                        JSONObject cfResults = new JSONObject();
                        Resource resourceCf = resouceCategory.next();

                        JSONObject res = contentFragmentData(resourceCf, resourceResolver);
                        if(res.length() >0){
                            results.put(res);
                        }
                    }
                }
            }
            if((keyword!=null && cfCategory!=null) || (cfCategory == null && keyword != null)){
                if(keyword!=null && cfCategory!=null){
                    resourceDam = resourceResolver.getResource("/content/dam/" + siteName + "/content-fragments/" + language+  "/" + cfCategory);
                }else {
                    resourceDam = resourceResolver.getResource("/content/dam/" + siteName + "/content-fragments/" + language);
                }

                myXpathQuery = new StringBuilder();
                myXpathQuery.append("SELECT * FROM [dam:Asset] as p ");
                myXpathQuery.append("WHERE ISDESCENDANTNODE('" + resourceDam.getPath() + "') ");
                myXpathQuery.append(" AND contains(p.*, '*" + keyword + "*' ) ");
                Session session = resourceResolver.adaptTo(Session.class);
                QueryManager queryManager = session.getWorkspace().getQueryManager();
                Query query = queryManager.createQuery(myXpathQuery.toString(), Query.JCR_SQL2);
                QueryResult queryResult = query.execute();
                NodeIterator item = queryResult.getNodes();
                while (item.hasNext()){
                    Node node = item.nextNode();
                    Resource itemRes = resourceResolver.getResource(node.getPath());
                    JSONObject res = contentFragmentData(itemRes, resourceResolver);
                    if(res.length() >0){
                        results.put(res);
                    }
                }
            }
        return results;
    }


    protected JSONObject contentFragmentData(Resource resourceCf, ResourceResolver resourceResolver) throws JSONException {
        JSONObject cfResults = new JSONObject();
        if(resourceCf.getResourceType().equals("dam:Asset")) {
            ContentFragment cf = resourceCf.adaptTo(ContentFragment.class);
            // format date
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            // format year
            SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");

            // get convert article date format
            ContentElement articleDateField = cf.getElement("articleDate");
            if(!articleDateField.getContent().equals("")){
                FragmentData articleDate = articleDateField.getValue();
                Calendar c = articleDate.getValue(Calendar.class);
                String formatedArticleDate = simpleDateFormat.format(c.getTime());
                cfResults.put("date", formatedArticleDate);

                // get year
                String year = formatYear.format(c.getTime());
                cfResults.put("year", year);
            }else {
                cfResults.put("date", "");
                cfResults.put("year", "");
            }
            cfResults.put("title", cf.getTitle());
            cfResults.put("description", cf.getElement("description").getContent());
            cfResults.put("urlLink", cf.getElement("link").getContent());
            cfResults.put("targetLink", cf.getElement("linkTarget").getContent());
            cfResults.put("labelLink", cf.getElement("linkLabel").getContent());
            TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
            String[] authors =  cf.getElement("author").getValue().getValue(String[].class);
            String[] topics = cf.getElement("topic").getValue().getValue(String[].class);
            String[] sources = cf.getElement("source").getValue().getValue(String[].class);
            String[] tags = cf.getElement("tags").getValue().getValue(String[].class);
            Tag typology = tagManager.resolve(cf.getElement("typology").getValue().getValue(String.class));
            cfResults.put("author", getTags(authors,tagManager));
            cfResults.put("topic",getTags(topics,tagManager));
            cfResults.put("source", getTags(sources,tagManager));
            cfResults.put("typology", typology != null ? typology.getTitle() :"");
            cfResults.put("tag",getTags(tags,tagManager));

        }
        return cfResults;
    }

    protected JSONArray getTags(String[] arrayTags , TagManager tagManager){
        JSONArray authorTag =  new JSONArray();
        if(arrayTags != null){
            for (String val : arrayTags) {
                Tag tag = tagManager.resolve(val.toString());
                if (tag != null) {
                    authorTag.put(tag.getTitle());
                }
            }
        }
        return authorTag ;
    }
*/

    /**
     * Configuration class
     */
   /* @ObjectClassDefinition(name = "Menarini Import Library Servlet Config", description = "Menarini Import Library Servlet Config")
    public static @interface Config {

        @AttributeDefinition(name = "Excel file name", description = "Excel file name")
        String getFileName() default "";


        @AttributeDefinition(name = "File Path", description = "File Path")
        String getFileDAMFolder() default "";


        @AttributeDefinition(name = "Tag Import Enabled", description = "Tag Import Enabled")
        boolean isImportTagEnabled() default false;


        @AttributeDefinition(name = "Article Import Enabled", description = "Tag Import Enabled")
        boolean isImportArticleEnabled() default false;

    }*/
}
