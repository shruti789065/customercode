package com.adiacent.menarini.mhos.core.servlets;

import com.adiacent.menarini.mhos.core.resources.ImportLibraryResource;
import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.dam.print.ids.StringConstants;
import com.day.cq.commons.jcr.JcrConstants;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.poi.ss.usermodel.CellType.STRING;

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
    private static Integer TIPOLOGY_TAGS_COL_INDEX = 1;
    private static Integer TOPIC_TAGS_COL_INDEX = 2;
    private static Integer SOURCE_TAGS_COL_INDEX = 2;
    private static Integer AUTHOR_COL_INDEX = 5;
    private static Integer GENERIC_TAGS_COL_INDEX = 6;

    private static HashMap<String, List<String>> tagsMap = null;

    static{
        tagsMap = new HashMap<String, List<String>>();
        tagsMap.put("author", new ArrayList());
        tagsMap.put("generic-tags", new ArrayList());
        tagsMap.put("topic", new ArrayList());
        tagsMap.put("source", new ArrayList());
        tagsMap.put("typology", new ArrayList());
    }
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
                importTags(inputStream, session);
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


    private void importTags(InputStream inputStream, Session session) {
        LOG.debug("Start import tags********************");
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

                    //Iterator<Cell> cellIterator = row.cellIterator();   //iterating over each column
                    Cell authorCell = row.getCell(AUTHOR_COL_INDEX);
                    if(authorCell != null){
                        String value = authorCell.getStringCellValue();
                        LOG.info("author values in cell {} ", value);
                        if(StringUtils.isNotBlank(value)){
                            //si usa la virgola come separatore
                            List<String> convertedAuthorsList = Stream.of(value.split(",", -1))
                                    .map(a->{
                                        String res= null;
                                       res = a.toLowerCase().replaceAll("mr|md|phd|ms(\\w+[;]{0,1})", "");
                                       res=StringUtils.trimToEmpty(res);
                                       return res;
                                    })
                                    .filter(e-> StringUtils.isNotBlank(e))
                                    .collect(Collectors.toList());
                            if(convertedAuthorsList.size() > 0){
                                tagsMap.get("author").addAll(convertedAuthorsList);
                            }
                        }

                    }
                    /*while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        switch (cell.getCellType()) {
                            case STRING:    //field that represents string cell type
                                System.out.print(cell.getStringCellValue() + "\t\t\t");
                                break;
                            case NUMERIC:    //field that represents number cell type
                                System.out.print(cell.getNumericCellValue() + "\t\t\t");
                                break;
                            default:
                        }
                    }*/
                }
               count++;
            }

            //vedere se c'è da creare il nodo per il tag e i nodi dei valori per ogni key dell'hahsmap
            storeTags2(session);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LOG.debug("End import tags******************************");
    }

    private Node findDescendantNodeByName(String searchPath, String name){
        if(StringUtils.isBlank(searchPath) || StringUtils.isBlank(name))
            return null;
        try {
            StringBuilder myXpathQuery;
            myXpathQuery = new StringBuilder();
            myXpathQuery.append("SELECT * FROM [cq:Tag]  as p ");
            myXpathQuery.append("WHERE ISDESCENDANTNODE('" + searchPath + "') ");
            myXpathQuery.append(" AND NAME() = '"+ name+"'");
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
    private Node insertOrUpdateChildNode(String searchPath, String childTitle, HashMap properties ){

        if(StringUtils.isBlank(searchPath) || StringUtils.isBlank(childTitle))
            return null;

        Resource root = resourceResolver.getResource(searchPath);
        if(root == null)
            return null;
        Node rootNode = root.adaptTo(Node.class);

        String childName = StringUtils.replace(childTitle," ", "-");
        Node childNode = findDescendantNodeByName(searchPath, childName);
        Node parentNode = (childNode == null) ? rootNode :  null;

        try {
            if(childNode == null) {
                TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
                Tag tagChild = tagManager.createTag(childName, childTitle, null,false);
                childNode = tagChild.adaptTo(Node.class);
                tagManager.moveTag(tagChild,parentNode.getPath()+"/"+childName );
                //childNode =parentNode.addNode(childName);
            }
            else{
                Calendar lmCalendar = Calendar.getInstance();
                lmCalendar.setTimeInMillis(System.currentTimeMillis());
                childNode.setProperty(JcrConstants.JCR_LASTMODIFIED, lmCalendar);
            }
            final Node child = childNode;
            if(properties != null){
                properties.keySet().forEach(k -> {
                    try {
                        child.setProperty((String)k, (String)properties.get(k));
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

        return childNode;
    }

    private void storeTags2(Session session) {
        LOG.debug("Start store tags -  2  - ");
        //Per ogni chiave dell'hashmap tagsMap si controlla che esista il nodo, altrimenti lo si crea

        tagsMap.keySet().forEach(k -> {
            LOG.debug( "Tag name " + k );

            HashMap properties = new HashMap();
            //properties.put(JcrConstants.JCR_PRIMARYTYPE, "cq:Tag");
            properties.put(JcrConstants.JCR_TITLE, k);
            properties.put(StringConstants.SLING_RESOURCE_TYPE, "cq/tagging/components/tag");
            Node currentTagNode = insertOrUpdateChildNode(servletConfig.getTagsRootPath(), k.toLowerCase() ,properties);

            if(currentTagNode != null){
                //si aggiungono i nodi valore del nodo tag SE non già presenti,altrimenti si aggiorna
                List<String> valueList = tagsMap.get(k);
                if( valueList != null &&  valueList.size() > 0){
                    valueList.forEach(v-> {
                        if (v != null) {

                            HashMap propertiesV = new HashMap();
                            //propertiesV.put(JcrConstants.JCR_PRIMARYTYPE, "cq:Tag");
                            propertiesV.put(JcrConstants.JCR_TITLE, v);
                            propertiesV.put(StringConstants.SLING_RESOURCE_TYPE, "cq/tagging/components/tag");
                            try {
                                Node currentTagValueNode = insertOrUpdateChildNode(currentTagNode.getPath(), v.toLowerCase() ,propertiesV);
                            } catch (RepositoryException e) {
                                throw new RuntimeException(e);
                            }


                        }
                    });
                }
            }

        } );

        /*try {
            session.save();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }*/
        LOG.debug("End store tags");
    }

    private void storeTags(Session session) {
        LOG.debug("Start store tags");
        //Per ogni chiave dell'hashmap tagsMap si controlla che esista il nodo, altrimenti lo si crea
        Resource tagRoot = resourceResolver.getResource(servletConfig.getTagsRootPath());
        if(tagRoot == null)
            return;
        Node tagRootNode = tagRoot.adaptTo(Node.class);

        tagsMap.keySet().forEach(k -> {
            LOG.debug( "Tag name " + k );
            Resource currentTag = resourceResolver.getResource(servletConfig.getTagsRootPath()+"/"+k);
            Node currentTagNode = null;
            if(currentTag == null){

                try {
                    currentTagNode  = tagRootNode.addNode(k);
                    Calendar lmCalendar = Calendar.getInstance();
                    lmCalendar.setTimeInMillis(System.currentTimeMillis());
                    currentTagNode.setProperty(JcrConstants.JCR_LASTMODIFIED, lmCalendar);
                    currentTagNode.setProperty(JcrConstants.JCR_PRIMARYTYPE, "cq:Tag");
                    currentTagNode.setProperty(JcrConstants.JCR_TITLE, k);
                    currentTagNode.setProperty(StringConstants.SLING_RESOURCE_TYPE, "cq/tagging/components/tag");
                } catch (RepositoryException e) {
                    throw new RuntimeException(e);
                }
            }
            else
                currentTagNode = currentTag.adaptTo(Node.class);

            //si aggiungono i nodi valore del nodo tag SE non già presenti,altrimenti si aggiorna
            List<String> valueList = tagsMap.get(k);
            try {

                String currentTagNodeRootPath = currentTagNode.getPath();
                final Node currentTagNodeRoot = currentTagNode;
                if( valueList != null &&  valueList.size() > 0){
                    valueList.forEach(v->{
                        if(v != null){
                            Resource currentTagValue = resourceResolver.getResource(currentTagNodeRootPath+"/"+v);
                            Node currentTagValueNode = null;
                            if(currentTagValue == null){

                                try {
                                    currentTagValueNode  =  currentTagNodeRoot.addNode(k);
                                    Calendar lmCalendar = Calendar.getInstance();
                                    lmCalendar.setTimeInMillis(System.currentTimeMillis());
                                    currentTagValueNode.setProperty(JcrConstants.JCR_CREATED, lmCalendar);
                                    currentTagValueNode.setProperty(JcrConstants.JCR_PRIMARYTYPE, "cq:Tag");
                                    currentTagValueNode.setProperty(JcrConstants.JCR_TITLE, v);
                                    currentTagValueNode.setProperty(StringConstants.SLING_RESOURCE_TYPE, "cq/tagging/components/tag");
                                } catch (RepositoryException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            else
                                currentTagValueNode = currentTagValue.adaptTo(Node.class);
                        }
                    });

                }
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }

        } );

        try {
            session.save();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        LOG.debug("End store tags");
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
