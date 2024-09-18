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
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
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

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

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
    private static String SPEAKER_IMAGES_PATH = "/speakerImages/";
    private static String EVENT_IMAGES_PATH = "/eventImages/";
    private static String ROOT_DATA = "/content/dam/fondazione";
    private static String BASE_MODEL = "/conf/fondazione/settings/dam/cfm/models";
    private static String[] OBJECTS = {"events", "cities", "nations", "subscriptiontypes", "speakers", "media", "topics", "speakerimages", "eventimages"};

    private static String eventModelPath = BASE_MODEL + "/event";
    private static String eventParentPath = ROOT_DATA + "/events";
    private static String[] eventSingleFields = {"id", "data_inizio", "data_fine", "ref_citta", "ref_nazione", "sede", "ref_iscrizione", "immagine_evidenza_url", "program_cover", "program_pdf"};
    private static final Map<String, Object> eventFieldIndexMap = new HashMap<>();
    static {
        eventFieldIndexMap.put("id", 0);
        eventFieldIndexMap.put("data_inizio", 3);
        eventFieldIndexMap.put("data_fine", 4);
        eventFieldIndexMap.put("citta", 7);
        eventFieldIndexMap.put("ref_citta", "/content/dam/fondazione/cities/");
        eventFieldIndexMap.put("nazione", 8);
        eventFieldIndexMap.put("ref_nazione", "/content/dam/fondazione/nations/");
        eventFieldIndexMap.put("iscrizione", 9);
        eventFieldIndexMap.put("ref_iscrizione", "/content/dam/fondazione/subscriptionTypes/");
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
        eventTopicsFieldIndexMap.put("ref_topics", "/content/dam/fondazione/topics/");
    }
    private static String eventImagesParentPath = ROOT_DATA + "/images/events";

    private static String topicModelPath = BASE_MODEL + "/topic";
    private static String topicParentPath = ROOT_DATA + "/topics";
    private static String[] topicSingleFields = {"id"};
    private static final Map<String, Object> topicFieldIndexMap = new HashMap<>();
    static {
        topicFieldIndexMap.put("id", 0);
    }

    private static String speakerModelPath = BASE_MODEL + "/speaker";
    private static String speakerParentPath = ROOT_DATA + "/speakers";
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
    private static String speakerImagesParentPath = ROOT_DATA + "/images/speakers";

    private static String nationModelPath = BASE_MODEL + "/nation";
    private static String nationParentPath = ROOT_DATA + "/nations";
    private static String[] nationSingleFields = {"id"};
    private static final Map<String, Object> nationFieldIndexMap = new HashMap<>();
    static {
        nationFieldIndexMap.put("id", 0);
    }

    private static String cityModelPath = BASE_MODEL + "/city";
    private static String cityParentPath = ROOT_DATA + "/cities";
    private static String[] citySingleFields = {"id", "ref_nation"};
    private static final Map<String, Object> cityFieldIndexMap = new HashMap<>();
    static {
        cityFieldIndexMap.put("id", 0);
        cityFieldIndexMap.put("nation", 1);
        cityFieldIndexMap.put("ref_nation", "/content/dam/fondazione/nations/");
    }

    private static String subscriptionModelPath = BASE_MODEL + "/subscription";
    private static String subscriptionParentPath = ROOT_DATA + "/subscriptionTypes";
    private static String[] subscriptionSingleFields = {"id"};
    private static final Map<String, Object> subscriptionFieldIndexMap = new HashMap<>();
    static {
        subscriptionFieldIndexMap.put("id", 0);
    }

    private static String mediaModelPath = BASE_MODEL + "/media";
    private static String mediaParentPath = ROOT_DATA + "/media";
    private static String[] mediaSingleFields = {"id", "ref_evento", "ref_relatore", "ordine", "data_relazione", "code_video", "path_video", "path_mini", "tipo", "ref_disciplina"};
    private static final Map<String, Object> mediaFieldIndexMap = new HashMap<>();
    static {
        mediaFieldIndexMap.put("id", 0);
        mediaFieldIndexMap.put("evento", 1);
        mediaFieldIndexMap.put("ref_evento", "/content/dam/fondazione/events/");
        mediaFieldIndexMap.put("relatore", 2);
        mediaFieldIndexMap.put("ref_relatore", "/content/dam/fondazione/speakers/");
        mediaFieldIndexMap.put("ordine", 3);
        mediaFieldIndexMap.put("data_relazione", 4);
        mediaFieldIndexMap.put("code_video", 9);
        mediaFieldIndexMap.put("path_video", 10);
        mediaFieldIndexMap.put("path_mini", 11);
        mediaFieldIndexMap.put("tipo", 12);
        mediaFieldIndexMap.put("disciplina", 13);
        mediaFieldIndexMap.put("ref_disciplina", "/content/dam/fondazione/topics/");
    }

    private static ResourceResolver currentResolver;
    private static String[] currentSingleFields;
    private static Map<String, String> currentVariationData;
    private static Map<String, Object> currentFieldIndexMap;
    private static boolean appendRef;
    private static QueryManager queryManager;
    private static String currentParentPath;

    /**
     * Main method to initiate the data migration process.
     * Calls individual migration methods for different content types.
     */
    public void migrateData(String object) throws Exception {
          
        try (ResourceResolver resolver = getResourceResolver()) {
            currentResolver = resolver;
            queryManager = resolver.adaptTo(Session.class).getWorkspace().getQueryManager();

            if (object.isBlank()) {
                migrateTopics();
                migrateSpeakers();
                migrateNations();
                migrateSubscriptionTypes();
                migrateCities(); // nations
                migrateEvents(); // subscriptionTypes cities nations
                try {
                    connectEventsTopics();
                } catch (Exception e) {
                    Thread.sleep(2000); // Wait for 2 seconds
                    connectEventsTopics();
                }
                migrateMedia(); // speakers topics events
            } else if (Arrays.asList(OBJECTS).contains(object.toLowerCase())) {
                switch (object.toLowerCase()) {
                    case "events":
                        migrateEvents(); // subscriptionTypes cities nations
                        connectEventsTopics();
                        break;
                    case "cities":
                        migrateCities(); // nations
                        break;
                    case "nations":
                        migrateNations();
                        break;
                    case "subscriptiontypes":
                        migrateSubscriptionTypes();
                        break;
                    case "speakers":
                        migrateSpeakers();
                        break;
                    case "media":
                        migrateMedia(); // speakers topics events
                        break;
                    case "topics":
                        migrateTopics();
                        break;
                    case "speakerimages":
                        loadLinkSpeakerImages();
                        break;
                    case "eventimages":
                        loadLinkEventImages();
                        break;
                    default:
                        break;
                } 
            } else {
                throw new IllegalArgumentException();   
            }

            //TODO connection with image and PDF assets
        } 
    }

    /**
     * Migrates topic data from CSV to Content Fragments.
     */
    private void migrateTopics() throws Exception {

        Resource modelResource = currentResolver.getResource(topicModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + topicModelPath);
        }

        appendRef = false;
        currentParentPath = topicParentPath;
        currentSingleFields = topicSingleFields;
        currentFieldIndexMap = topicFieldIndexMap;
        Resource parentResource = ensureFolderExists();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CSV_PATH + "topics.csv");
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] fields = parseCsvLine(line);

                currentVariationData = new HashMap<>();
                currentVariationData.put("nome_disciplina_it", fields[1]);
                currentVariationData.put("nome_disciplina_en", fields[2]);

                processCsvRow(fields, fields[0], fields[1], template, parentResource);

                count++;
                if (count % 100 == 0) {
                    currentResolver.commit();
                }
            }
        } finally {
            if (currentResolver != null) {
                currentResolver.commit();
            }
        }
    }

    /**
     * Migrates speaker data from CSV to Content Fragments.
     */
    private void migrateSpeakers() throws Exception {
        Resource modelResource = currentResolver.getResource(speakerModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + speakerModelPath);
        }

        appendRef = false;
        currentParentPath = speakerParentPath;
        currentSingleFields = speakerSingleFields;
        currentFieldIndexMap = speakerFieldIndexMap;
        Resource parentResource = ensureFolderExists();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CSV_PATH + "speakers.csv");
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] fields = parseCsvLine(line);

                currentVariationData = new HashMap<>();
                currentVariationData.put("curriculum_it", fields[6]);
                currentVariationData.put("curriculum_en", fields[6]);

                processCsvRow(fields, fields[0], fields[1] + " " + fields[2], template, parentResource);
                
                count++;
                if (count % 100 == 0) {
                    currentResolver.commit();
                }
            }
        } finally {
            if (currentResolver != null) {
                currentResolver.commit();
            }
        }
    }

    /**
     * Migrates nation data from CSV to Content Fragments.
     */
    private void migrateNations() throws Exception {
        Resource modelResource = currentResolver.getResource(nationModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + nationModelPath);
        }

        appendRef = false;
        currentParentPath = nationParentPath;
        currentSingleFields = nationSingleFields;
        currentFieldIndexMap = nationFieldIndexMap;
        Resource parentResource = ensureFolderExists();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CSV_PATH + "nazioni.csv");
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] fields = parseCsvLine(line);

                currentVariationData = new HashMap<>();
                currentVariationData.put("name_it", fields[1]);
                currentVariationData.put("name_en", fields[2]);

                processCsvRow(fields, fields[0], fields[1], template, parentResource);
                
                count++;
                if (count % 100 == 0) {
                    currentResolver.commit();
                }
            }
        } finally {
            if (currentResolver != null) {
                currentResolver.commit();
            }
        }
    }

    /**
     * Migrates city data from CSV to Content Fragments.
     */
    private void migrateCities() throws Exception {

        if (!checkReferencesExist("/content/dam/fondazione/nations")) {
            throw new RepositoryException("Missing references. Migrate nations before cities.");
        }

        Resource modelResource = currentResolver.getResource(cityModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + cityModelPath);
        }

        appendRef = false;
        currentParentPath = cityParentPath;
        currentSingleFields = citySingleFields;
        currentFieldIndexMap = cityFieldIndexMap;
        Resource parentResource = ensureFolderExists();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CSV_PATH + "citta.csv");
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] fields = parseCsvLine(line);

                currentVariationData = new HashMap<>();
                currentVariationData.put("name_it", fields[2]);
                currentVariationData.put("name_en", fields[3]);

                processCsvRow(fields, fields[0], fields[2], template, parentResource);
                
                count++;
                if (count % 100 == 0) {
                    currentResolver.commit();
                }
            }
        } finally {
            if (currentResolver != null) {
                currentResolver.commit();
            }
        }
    }

    /**
     * Migrates event data from CSV to Content Fragments.
     */
    private void migrateEvents() throws Exception {

        if (!checkReferencesExist("/content/dam/fondazione/nations") ||
            !checkReferencesExist("/content/dam/fondazione/cities") ||
            !checkReferencesExist("/content/dam/fondazione/subscriptionTypes")) {
        
            throw new RepositoryException("Missing references. Migrate nations, cities and subscriptionTypes before events.");
        }

        Resource modelResource = currentResolver.getResource(eventModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + eventModelPath);
        }

        appendRef = false;
        currentParentPath = eventParentPath;
        currentSingleFields = eventSingleFields;
        currentFieldIndexMap = eventFieldIndexMap;
        Resource parentResource = ensureFolderExists();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CSV_PATH + "events.csv");
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] fields = parseCsvLine(line);

                currentVariationData = new HashMap<>();
                currentVariationData.put("titolo_it", fields[1]);
                currentVariationData.put("titolo_en", fields[2]);
                currentVariationData.put("descrizione_it", fields[5]);
                currentVariationData.put("descrizione_en", fields[6]);
                currentVariationData.put("presentation_description_it", fields[12]);
                currentVariationData.put("presentation_description_en", fields[13]);

                processCsvRow(fields, fields[0], fields[1], template, parentResource);
                
                count++;
                if (count % 100 == 0) {
                    currentResolver.commit();
                }
            }
        } finally {
            if (currentResolver != null) {
                currentResolver.commit();
            }
        }
    }
    
    /**
     * Migrates subscription data from CSV to Content Fragments.
     */
    private void migrateSubscriptionTypes() throws Exception {
        Resource modelResource = currentResolver.getResource(subscriptionModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + subscriptionModelPath);
        }

        appendRef = false;
        currentParentPath = subscriptionParentPath;
        currentSingleFields = subscriptionSingleFields;
        currentFieldIndexMap = subscriptionFieldIndexMap;
        Resource parentResource = ensureFolderExists();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CSV_PATH + "subscriptions.csv");
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] fields = parseCsvLine(line);

                currentVariationData = new HashMap<>();
                currentVariationData.put("type_it", fields[1]);
                currentVariationData.put("type_en", fields[2]);

                processCsvRow(fields, fields[0], fields[1], template, parentResource);
                
                count++;
                if (count % 100 == 0) {
                    currentResolver.commit();
                }
            }
        } finally {
            if (currentResolver != null) {
                currentResolver.commit();
            }
        }
    }

    /**
     * Connects events with their corresponding topics.
     */
    private void connectEventsTopics() throws Exception {

        Resource modelResource = currentResolver.getResource(eventModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + eventModelPath);
        }

        appendRef = true;
        currentParentPath = eventParentPath;
        currentSingleFields = eventTopicsSingleFields;
        currentFieldIndexMap = eventTopicsFieldIndexMap;
        Resource parentResource = ensureFolderExists();

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

                currentVariationData = new HashMap<>();

                processCsvRow(fields, fields[2], null, template, parentResource);
                
            }
        } finally {
            if (currentResolver != null) {
                currentResolver.commit();
            }
        }
    }


    /**
     * Migrates topic data from CSV to Content Fragments.
     */
    private void migrateMedia() throws Exception {

        if (!checkReferencesExist("/content/dam/fondazione/speakers") ||
            !checkReferencesExist("/content/dam/fondazione/topics") ||
            !checkReferencesExist("/content/dam/fondazione/events")) {
        
            throw new RepositoryException("Missing references. Migrate speakers, topics and events before media.");
        }

        Resource modelResource = currentResolver.getResource(mediaModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + mediaModelPath);
        }

        appendRef = false;
        currentParentPath = mediaParentPath;
        currentSingleFields = mediaSingleFields;
        currentFieldIndexMap = mediaFieldIndexMap;
        Resource parentResource = ensureFolderExists();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CSV_PATH + "media.csv");
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] fields = parseCsvLine(line);

                currentVariationData = new HashMap<>();
                currentVariationData.put("titolo_it", fields[5]);
                currentVariationData.put("titolo_en", fields[6]);
                currentVariationData.put("descrizione_it", fields[7]);
                currentVariationData.put("descrizione_en", fields[8]);

                processCsvRow(fields, fields[0], fields[5], template, parentResource);

                count++;
                if (count % 100 == 0) {
                    currentResolver.commit();
                }
            }
        } finally {
            if (currentResolver != null) {
                currentResolver.commit();
            }
        }
    }

    /**
     * Loads speaker images from a specified directory and links them to speaker content fragments.
     */
    private void loadLinkSpeakerImages() throws Exception {

        if (!checkReferencesExist("/content/dam/fondazione/speakers")) {
            throw new RepositoryException("Migrate speakers before loading images.");
        }

        currentParentPath = speakerImagesParentPath;
        ensureFolderExists();

        AssetManager assetManager = currentResolver.adaptTo(AssetManager.class);

        Resource modelResource = currentResolver.getResource(speakerModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + speakerParentPath);
        }

        currentParentPath = speakerParentPath;
        Resource parentResource = ensureFolderExists();
        String replace = ".PhotoContent";

        Asset defaultAsset = loadAsset("default.PhotoContent.png", SPEAKER_IMAGES_PATH, speakerImagesParentPath, replace, assetManager);
        if (defaultAsset == null) {
            throw new IllegalArgumentException("Default image not found");
        }

        Iterator<Resource> fragments = parentResource.listChildren();
        while (fragments.hasNext()) {
            Resource fragmentResource = fragments.next();
            ContentFragment cfm = fragmentResource.adaptTo(ContentFragment.class);
            ContentElement element = cfm.getElement("id");
            String id = element.getValue().getValue().toString();

            String fileName = id + replace + ".jpg";

            Asset asset = null;
            asset = loadAsset(fileName, SPEAKER_IMAGES_PATH, speakerImagesParentPath, replace, assetManager);
            if (asset == null) {
                fileName = id + replace + ".png";
                asset = loadAsset(fileName, SPEAKER_IMAGES_PATH, speakerImagesParentPath, replace, assetManager);
            }
            if (asset == null) {
                asset = defaultAsset;
            }

            String assetReference = asset.getPath();
            ContentElement photoElement = cfm.getElement("foto");
            if (photoElement != null) {
                photoElement.setContent(assetReference, photoElement.getContentType());
                currentResolver.commit();
            }
        }
    }

    /**
     * Loads event images from a specified directory and links them to event content fragments.
     */
    private void loadLinkEventImages() throws Exception {

        if (!checkReferencesExist("/content/dam/fondazione/events")) {
            throw new RepositoryException("Migrate events before loading images.");
        }

        currentParentPath = eventImagesParentPath;
        ensureFolderExists();

        AssetManager assetManager = currentResolver.adaptTo(AssetManager.class);

        Resource modelResource = currentResolver.getResource(eventModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + speakerParentPath);
        }

        currentParentPath = eventParentPath;
        Resource parentResource = ensureFolderExists();
        String replace = ".FileContent";

        Iterator<Resource> fragments = parentResource.listChildren();
        while (fragments.hasNext()) {
            Resource fragmentResource = fragments.next();
            ContentFragment cfm = fragmentResource.adaptTo(ContentFragment.class);
            ContentElement element = cfm.getElement("id");
            String id = element.getValue().getValue().toString();

            String fileName = id + replace + ".jpg";

            Asset asset = null;
            asset = loadAsset(fileName, EVENT_IMAGES_PATH, eventImagesParentPath, replace, assetManager);
            if (asset == null) {
                fileName = id + replace + ".png";
                asset = loadAsset(fileName, EVENT_IMAGES_PATH, eventImagesParentPath, replace, assetManager);
            }
            if (asset == null) {
                continue;
            }

            String assetReference = asset.getPath();
            ContentElement photoElement = cfm.getElement("presentation_image");
            if (photoElement != null) {
                photoElement.setContent(assetReference, photoElement.getContentType());
                currentResolver.commit();
            }
        }
    }

    /**
     * Loads an asset from the specified file name and returns it.
     * If the asset does not exist in the repository, it attempts to create it.
     */
    @SuppressWarnings("deprecation")
    private Asset loadAsset(String fileName, String fromPath, String toPath, String replace, AssetManager assetManager) {
        Asset asset = null;
        String assetPath = toPath + "/" + fileName.replace(replace, "");
        Resource assetResource = currentResolver.getResource(assetPath);
        
        if (assetResource != null) {
            asset = assetResource.adaptTo(Asset.class);
            return asset;
        }

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fromPath + fileName)) {
            if (inputStream != null) {
                asset = assetManager.createAsset(assetPath, inputStream, "image/jpeg", true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return asset;
    }

    /**
     * Processes a single row from the CSV file and creates or updates a Content Fragment.
     */
    private void processCsvRow(String[] fields,  String id, String name, FragmentTemplate template, Resource parentResource) throws Exception {

        ContentFragment cfm = findFragmentById(id, currentParentPath);
        
        if (cfm == null && name != null) { //controllo per i job di associazione che non devono creare fragments
            cfm = template.createFragment(parentResource, slugify(id, name), name);
        } 

        if (cfm == null) {
            throw new IllegalStateException("Failed to create or retrieve content fragment for ID: " + id);
        }

        updateFields(cfm, fields);
        
        updateContentFragmentVariations(cfm);

        // aggiunto commit ogni 100 elementi nel chiamante
        // currentResolver.commit();
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
     * @throws RepositoryException 
     */
    private void updateFields(ContentFragment cfm, String[] fields) throws ContentFragmentException, RepositoryException {
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
                    ContentFragment refFragment = findFragmentById(newVal, refPath);
                    if (refFragment == null) {
                        continue;
                    } else {
                        newVal = refFragment.getName();
                    }
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
    private Resource ensureFolderExists() throws PersistenceException {
        Resource folderResource = currentResolver.getResource(currentParentPath);
        Resource prevPath = null;
        if (folderResource == null) {
            String parentPath = StringUtils.remove(currentParentPath, ROOT_DATA);
            String[] parts = parentPath.split("/");
            StringBuilder pathBuilder = new StringBuilder();
            pathBuilder.append(ROOT_DATA);
            for (String part : parts) {
                pathBuilder.append(part).append("/");
                String currentPath = pathBuilder.toString();
                Resource currentResource = currentResolver.getResource(currentPath);
                if ( currentResource == null) {
                    Map<String, Object> properties = new HashMap<>();
                    properties.put("jcr:primaryType", "nt:folder");
                    folderResource = currentResolver.create(prevPath, part, properties);
                } else {
                    prevPath = currentResource;
                }

            }
            currentResolver.commit();
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

    /**
     * Build the slug value for fragment name
     */
    private static String slugify(String id, String name) {
        // Normalization
        String slug = name.toLowerCase()
                .replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}\\s]", "")
                .replaceAll("\\s+", "-");

        String idSlug = id + "-" + slug;

        // Max 30 chars
        if (idSlug.length() > 30) {
            idSlug = idSlug.substring(0, 30);
        }

        // Remove final cut
        idSlug = idSlug.replaceAll("-$", "");

        return idSlug;
    }

    /**
     * Find the fragment by id field
     */
    public ContentFragment findFragmentById(String id, String path) throws RepositoryException {
        String queryStr = "SELECT * FROM [dam:Asset] AS s WHERE ISDESCENDANTNODE(s, '" + path + "') AND s.[jcr:content/data/master/id] = '" + id + "'";

        Query query = queryManager.createQuery(queryStr, Query.JCR_SQL2);
        QueryResult result = query.execute();

        while (result.getRows().hasNext()) {
            String nodePath = result.getRows().nextRow().getPath();
            // using s.[jcr:content/data/master/id] instead of s.[id] in the query returns only the master, the statement below might be unnecessary
            if (nodePath.contains("/jcr:content/")) {
                nodePath = nodePath.substring(0, nodePath.indexOf("/jcr:content"));
            }
            Resource fragmentResource = currentResolver.getResource(nodePath);

            if (fragmentResource != null) {
                ContentFragment contentFragment = fragmentResource.adaptTo(ContentFragment.class);
                
                if (contentFragment != null) {
                    return contentFragment;
                }
            }
        }
        
        return null;
    }

    /**
     * check if reference path has fragments
     */
    private boolean checkReferencesExist(String referencePath) {
        Resource referenceResource = currentResolver.getResource(referencePath);
    
        if (referenceResource != null && referenceResource.hasChildren()) {
            for (Resource child : referenceResource.getChildren()) {
                ContentFragment fragment = child.adaptTo(ContentFragment.class);
                if (fragment != null) {
                    return true;
                }
            }
        }

        return false;
    }
}
