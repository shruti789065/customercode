package com.jakala.menarini.core.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.ContentFragmentException;
import com.adobe.cq.dam.cfm.ContentVariation;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.adobe.cq.dam.cfm.VariationDef;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Service component for migrating data from SQL Server to Content Fragments in AEM.
 */
@Component(service = DataMigrationService.class)
public class DataMigrationService {

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private SlingRepository slingRepository;

    private static String SERVICE = "data-migration-service";
    private static String CSV_PATH = "/csv/";
    private static String ROOT_DATA = "/content/dam/fondazione";
    private static String BASE_MODEL = "/conf/fondazione/settings/dam/cfm/models";

    private static String eventModelPath = BASE_MODEL + "/event";
    private static String eventParentPath = ROOT_DATA + "/event";
    private static String[] eventSingleFields = {"id", "data_inizio", "data_fine", "ref_citta", "ref_nazione", "sede", "ref_iscrizione", "immagine_evidenza_url", "program_cover", "program_pdf"};
    private static final Map<String, Object> eventFieldIndexMap = new HashMap<>();
    static {
        eventFieldIndexMap.put("id", 0);
        eventFieldIndexMap.put("data_inizio", 3);
        eventFieldIndexMap.put("data_fine", 4);
        eventFieldIndexMap.put("citta", 7);
        eventFieldIndexMap.put("ref_citta", "/content/dam/fondazione/city/");
        eventFieldIndexMap.put("nazione", 8);
        eventFieldIndexMap.put("ref_nazione", "/content/dam/fondazione/nation/");
        eventFieldIndexMap.put("iscrizione", 9);
        eventFieldIndexMap.put("ref_iscrizione", "/content/dam/fondazione/subscription/");
        eventFieldIndexMap.put("sede", 10);
        eventFieldIndexMap.put("immagine_evidenza_url", 11);
        eventFieldIndexMap.put("program_cover", 14);
        eventFieldIndexMap.put("program_pdf", 15);
    }
    private static String[] eventTopicsSingleFields = {"id", "ref_topics"};
    private static final Map<String, Object> eventTopicsFieldIndexMap = new HashMap<>();
    static {
        eventTopicsFieldIndexMap.put("id", 2);
        eventTopicsFieldIndexMap.put("topics", 1);
        eventTopicsFieldIndexMap.put("ref_topics", "/content/dam/fondazione/topic/");
    }


    private static String topicModelPath = BASE_MODEL + "/topic";
    private static String topicParentPath = ROOT_DATA + "/topic";
    private static String[] topicSingleFields = {"id"};
    private static final Map<String, Object> topicFieldIndexMap = new HashMap<>();
    static {
        topicFieldIndexMap.put("id", 0);
    }

    private static String speakerModelPath = BASE_MODEL + "/speaker";
    private static String speakerParentPath = ROOT_DATA + "/speaker";
    private static String[] speakerSingleFields = {"id", "nome", "cognome", "societa", "ruolo", "foto"};
    private static final Map<String, Object> speakerFieldIndexMap = new HashMap<>();
    static {
        speakerFieldIndexMap.put("id", 0);
        speakerFieldIndexMap.put("nome", 1);
        speakerFieldIndexMap.put("cognome", 2);
        speakerFieldIndexMap.put("societa", 3);
        speakerFieldIndexMap.put("ruolo", 4);
        speakerFieldIndexMap.put("foto", 5);
    }

    private static String nationModelPath = BASE_MODEL + "/nation";
    private static String nationParentPath = ROOT_DATA + "/nation";
    private static String[] nationSingleFields = {"id"};
    private static final Map<String, Object> nationFieldIndexMap = new HashMap<>();
    static {
        nationFieldIndexMap.put("id", 0);
    }

    private static String cityModelPath = BASE_MODEL + "/city";
    private static String cityParentPath = ROOT_DATA + "/city";
    private static String[] citySingleFields = {"id", "ref_nation"};
    private static final Map<String, Object> cityFieldIndexMap = new HashMap<>();
    static {
        cityFieldIndexMap.put("id", 0);
        cityFieldIndexMap.put("nation", 1);
        cityFieldIndexMap.put("ref_nation", "/content/dam/fondazione/nation/");
    }

    private static String subscriptionModelPath = BASE_MODEL + "/subscription";
    private static String subscriptionParentPath = ROOT_DATA + "/subscription";
    private static String[] subscriptionSingleFields = {"id"};
    private static final Map<String, Object> subscriptionFieldIndexMap = new HashMap<>();
    static {
        subscriptionFieldIndexMap.put("id", 0);
    }

    private static String[] currentSingleFields;
    private static Map<String, String> currentVariationData;
    private static Map<String, Object> currentFieldIndexMap;
    private static boolean appendRef;

    /**
     * Main method to initiate the data migration process.
     * Calls individual migration methods for different content types.
     */
    public void migrateData() throws Exception {
          
        try (ResourceResolver resolver = getResourceResolver()) {

            // migrateTopics(resolver);
            // migrateSpeakers(resolver);
            // migrateNations(resolver);
            // migrateCities(resolver);
            // migrateSubscriptions(resolver);
            migrateEvents(resolver);
            connectEventsTopics(resolver);

            //TODO connection with image and PDF assets
        } 
    }

    /**
     * Migrates topic data from CSV to Content Fragments.
     */
    private void migrateTopics(ResourceResolver resolver) throws Exception {

        Resource modelResource = resolver.getResource(topicModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + topicModelPath);
        }

        Resource parentResource = ensureFolderExists(resolver, topicParentPath);

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CSV_PATH + "topics.csv");
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] fields = parseCsvLine(line);

                appendRef = false;
                currentSingleFields = topicSingleFields;
                currentFieldIndexMap = topicFieldIndexMap;
                currentVariationData = new HashMap<>();
                currentVariationData.put("nome_disciplina_it", fields[1]);
                currentVariationData.put("nome_disciplina_en", fields[2]);

                processCsvRow(fields, 0, template, parentResource, resolver);
                
            }
        } finally {
            if (resolver != null) {
                resolver.commit();
            }
        }
    }

    /**
     * Migrates speaker data from CSV to Content Fragments.
     */
    private void migrateSpeakers(ResourceResolver resolver) throws Exception {
        Resource modelResource = resolver.getResource(speakerModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + speakerModelPath);
        }

        Resource parentResource = ensureFolderExists(resolver, speakerParentPath);

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CSV_PATH + "speakers.csv");
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] fields = parseCsvLine(line);

                appendRef = false;
                currentSingleFields = speakerSingleFields;
                currentFieldIndexMap = speakerFieldIndexMap;
                currentVariationData = new HashMap<>();
                currentVariationData.put("curriculum_it", fields[6]);
                currentVariationData.put("curriculum_en", fields[6]);

                processCsvRow(fields, 0, template, parentResource, resolver);
                
            }
        } finally {
            if (resolver != null) {
                resolver.commit();
            }
        }
    }

    /**
     * Migrates nation data from CSV to Content Fragments.
     */
    private void migrateNations(ResourceResolver resolver) throws Exception {
        Resource modelResource = resolver.getResource(nationModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + nationModelPath);
        }

        Resource parentResource = ensureFolderExists(resolver, nationParentPath);

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CSV_PATH + "nazioni.csv");
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] fields = parseCsvLine(line);

                appendRef = false;
                currentSingleFields = nationSingleFields;
                currentFieldIndexMap = nationFieldIndexMap;
                currentVariationData = new HashMap<>();
                currentVariationData.put("name_it", fields[1]);
                currentVariationData.put("name_en", fields[2]);

                processCsvRow(fields, 0, template, parentResource, resolver);
                
            }
        } finally {
            if (resolver != null) {
                resolver.commit();
            }
        }
    }

    /**
     * Migrates city data from CSV to Content Fragments.
     */
    private void migrateCities(ResourceResolver resolver) throws Exception {
        Resource modelResource = resolver.getResource(cityModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + cityModelPath);
        }

        Resource parentResource = ensureFolderExists(resolver, cityParentPath);

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CSV_PATH + "citta.csv");
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] fields = parseCsvLine(line);

                appendRef = false;
                currentSingleFields = citySingleFields;
                currentFieldIndexMap = cityFieldIndexMap;
                currentVariationData = new HashMap<>();
                currentVariationData.put("name_it", fields[2]);
                currentVariationData.put("name_en", fields[3]);

                processCsvRow(fields, 0, template,parentResource, resolver);
                
            }
        } finally {
            if (resolver != null) {
                resolver.commit();
            }
        }
    }

    /**
     * Migrates event data from CSV to Content Fragments.
     */
    private void migrateEvents(ResourceResolver resolver) throws Exception {

        Resource modelResource = resolver.getResource(eventModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + eventModelPath);
        }

        Resource parentResource = ensureFolderExists(resolver, eventParentPath);

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CSV_PATH + "events.csv");
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] fields = parseCsvLine(line);

                appendRef = false;
                currentSingleFields = eventSingleFields;
                currentFieldIndexMap = eventFieldIndexMap;
                currentVariationData = new HashMap<>();
                currentVariationData.put("titolo_it", fields[1]);
                currentVariationData.put("titolo_en", fields[2]);
                currentVariationData.put("descrizione_it", fields[5]);
                currentVariationData.put("descrizione_en", fields[6]);
                currentVariationData.put("presentation_description_it", fields[12]);
                currentVariationData.put("presentation_description_en", fields[13]);

                processCsvRow(fields, 0, template, parentResource, resolver);
                
            }
        } finally {
            if (resolver != null) {
                resolver.commit();
            }
        }
    }
    
    /**
     * Migrates subscription data from CSV to Content Fragments.
     */
    private void migrateSubscriptions(ResourceResolver resolver) throws Exception {
        Resource modelResource = resolver.getResource(subscriptionModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + subscriptionModelPath);
        }

        Resource parentResource = ensureFolderExists(resolver, subscriptionParentPath);

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CSV_PATH + "subscriptions.csv");
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] fields = parseCsvLine(line);

                appendRef = false;
                currentSingleFields = subscriptionSingleFields;
                currentFieldIndexMap = subscriptionFieldIndexMap;
                currentVariationData = new HashMap<>();
                currentVariationData.put("type_it", fields[1]);
                currentVariationData.put("type_en", fields[2]);

                processCsvRow(fields, 0, template, parentResource, resolver);
                
            }
        } finally {
            if (resolver != null) {
                resolver.commit();
            }
        }
    }

    /**
     * Connects events with their corresponding topics.
     */
    private void connectEventsTopics(ResourceResolver resolver) throws Exception {

        Resource modelResource = resolver.getResource(eventModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + eventModelPath);
        }

        Resource parentResource = ensureFolderExists(resolver, eventParentPath);

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CSV_PATH + "events_topics.csv");
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] fields = parseCsvLine(line);

                appendRef = true;
                currentSingleFields = eventTopicsSingleFields;
                currentFieldIndexMap = eventTopicsFieldIndexMap;
                currentVariationData = new HashMap<>();

                processCsvRow(fields, 2, template, parentResource, resolver);
                
            }
        } finally {
            if (resolver != null) {
                resolver.commit();
            }
        }
    }

    /**
     * Processes a single row from the CSV file and creates or updates a Content Fragment.
     */
    private void processCsvRow(String[] fields, int idPos, FragmentTemplate template, Resource parentResource, ResourceResolver resourceResolver) throws Exception {
        String id = fields[idPos];
        String fragmentPath = null;
        
        fragmentPath = parentResource.getPath() + "/" + id;

        Resource fragmentResource = resourceResolver.getResource(fragmentPath);
        ContentFragment cfm;
        
        if (fragmentResource == null) {
            cfm = template.createFragment(parentResource, id, fields[1]);
        } else {
            cfm = fragmentResource.adaptTo(ContentFragment.class);
        }

        if (cfm == null) {
            throw new IllegalStateException("Failed to create or retrieve content fragment for ID: " + id);
        }

        updateFields(cfm, fields);
        
        updateContentFragmentVariations(cfm);

        resourceResolver.commit();
    }

    /**
     * Updates Content Fragment variations for different languages.
     */
    private void updateContentFragmentVariations(ContentFragment cfm) throws Exception {
        String[] languages = {"it", "en"};
        
        for (String language : languages) {
            String variationName = cfm.getName() + "_" + language;
            Iterator<VariationDef> variations = cfm.listAllVariations();
            boolean variationExists = false;
            
            while (variations.hasNext()) {
                VariationDef variation = variations.next();
                if (variation.getName().equals(variationName)) {
                    variationExists = true;
                    break;
                }
            }

            if (!variationExists) {
                cfm.createVariation(variationName, variationName, "");
            }

            Iterator<ContentElement> elements = cfm.getElements();
            while (elements.hasNext()) {
                ContentElement cfElement = elements.next();
                ContentVariation cv = cfElement.getVariation(variationName);
                FragmentData fragmentData = cv.getValue();
                
                String key = cfElement.getName() + "_" + language;
                if (currentVariationData.containsKey(key)) {
                    fragmentData.setValue(currentVariationData.get(key));
                    cv.setValue(fragmentData);
                }
            }
        }
    }

    /**
     * Updates fields of a Content Fragment with data from CSV.
     * This also manages the single and multiple fragment reference fields.
     */
    private void updateFields(ContentFragment cfm, String[] fields) throws ContentFragmentException {
        for (String field : currentSingleFields) {
            ContentElement element = cfm.getElement(field);
            if (element != null) {
                String newVal = fields[(int)currentFieldIndexMap.get(field)];
                boolean isDate = checkAndFormatDateString(newVal, element);
                if (!isDate) {
                    element.setContent(newVal, element.getContentType());
                }
            } else if (field.startsWith("ref_")) {
                String ref = field.substring(4);
                ContentElement elementRef = cfm.getElement(ref);
                if (elementRef != null) {
                    String newVal = fields[(int)currentFieldIndexMap.get(ref)];
                    if (newVal.isBlank()) {
                        continue;
                    }
                    String refPath = (String)currentFieldIndexMap.get(field);
                    String newValPath = refPath + newVal;

                    FragmentData fragmentData = elementRef.getValue();
                    Object elementContent = fragmentData.getValue();
                    boolean isArray = elementContent == null ? false : elementContent.getClass().isArray();
                    List<String> oldVals = null;
                    if (elementContent != null && elementContent instanceof String[] && ((String[])elementContent).length > 0) {
                        oldVals = new ArrayList<>(Arrays.asList((String[])elementContent));
                    } else {
                        oldVals = new ArrayList<>();
                    }

                    if (appendRef && isArray ) {
                        if(!oldVals.contains(newValPath)) {
                            oldVals.add(newValPath);
                        }
                        fragmentData.setValue(oldVals.toArray());
                        elementRef.setValue(fragmentData);
                    } else {
                        elementRef.setContent(newValPath, elementRef.getContentType());
                    }
                } 
            }
        }
    }

    /**
     * Save the Date Time fields into the fragment data as GregorianCalendar objects
     */
    private boolean checkAndFormatDateString(String val, ContentElement element) {
        // Verifica se la stringa corrisponde al formato di una data"
        if (val.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.0")) {
            FragmentData fragmentData = element.getValue();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateVal;
            try {
                dateVal = dateFormat.parse(val);
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(dateVal);

                fragmentData.setValue(calendar);
                element.setValue(fragmentData);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (ContentFragmentException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    /**
     * Parses a CSV line, handling quoted fields and escaping internal quotes.
     */
    private String[] parseCsvLine(String line) {
        // Regex per trovare e sostituire i doppi apici interni con \"
        String[] fields = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
    
        for (int i = 0; i < fields.length; i++) {
            // Rimuove i doppi apici esterni se presenti
            if (fields[i].startsWith("\"") && fields[i].endsWith("\"")) {
                fields[i] = fields[i].substring(1, fields[i].length() - 1);
            }
            // Sostituisce i doppi apici interni con \"
            fields[i] = fields[i].replace("\"\"", "\\\"");
        }
    
        return fields;
    }
    
    /**
     * Ensures that the specified folder path exists in the JCR repository.
     * Creates the folder structure if it doesn't exist.
     */
    private Resource ensureFolderExists(ResourceResolver resolver, String folderPath) throws PersistenceException {
        Resource folderResource = resolver.getResource(folderPath);
        Resource prevPath = null;
        if (folderResource == null) {
            folderPath = StringUtils.remove(folderPath, ROOT_DATA);
            String[] parts = folderPath.split("/");
            StringBuilder pathBuilder = new StringBuilder();
            pathBuilder.append(ROOT_DATA);
            for (String part : parts) {
                pathBuilder.append(part).append("/");
                String currentPath = pathBuilder.toString();
                Resource currentResource = resolver.getResource(currentPath);
                if ( currentResource == null) {
                    Map<String, Object> properties = new HashMap<>();
                    properties.put("jcr:primaryType", "nt:folder");
                    folderResource = resolver.create(prevPath, part, properties);
                } else {
                    prevPath = currentResource;
                }

            }
            resolver.commit();
        }
        return folderResource;
    }

    /**
     * Retrieves a ResourceResolver for the data migration service.
     */
    private ResourceResolver getResourceResolver() throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, SERVICE);
        return resolverFactory.getServiceResourceResolver(param);
    }

}
