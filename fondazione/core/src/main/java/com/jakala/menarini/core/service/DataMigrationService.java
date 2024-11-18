package com.jakala.menarini.core.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.ContentFragmentException;
import com.adobe.cq.dam.cfm.ContentVariation;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.adobe.cq.dam.cfm.VariationDef;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.jakala.menarini.core.exceptions.RowProcessException;
import com.jakala.menarini.core.models.ModelHelper;
import com.jakala.menarini.core.service.interfaces.FileReaderServiceInterface;

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

import javax.jcr.Node;
import javax.jcr.NodeIterator;
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

    @Reference
    private FileReaderServiceInterface fileReaderService;

    private static final Logger LOGGER = LoggerFactory.getLogger(DataMigrationService.class);

    private static final String EVENTS = "events";
    private static final String CITIES = "cities";
    private static final String NATIONS = "nations";
    private static final String SUB_TYPES = "subscriptiontypes";
    private static final String SPEAKERS = "speakers";
    private static final String MEDIA = "media";
    private static final String TOPICS = "topics";
    private static final String SUPPLIERS = "suppliers";

    private static final String EVENTS_TOPICS = "eventstopics";
    private static final String EVENTS_IMAGES = "eventsimages";
    private static final String EVENTS_SPEAKERS = "eventsspeakers";
    private static final String SPEAKER_IMAGES = "speakerimages";

    private static final String NAME_IT = "name_it";
    private static final String NAME_EN = "name_en";
    private static final String PATH_SEPARATOR = "/";



    private static final String SERVICE = "data-migration-service";
    private static final String CSV_PATH = "/csv/";
    private static final String SPEAKER_IMAGES_PATH = "/speakerImages/";
    private static final String EVENT_IMAGES_PATH = "/eventImages/";
    private static final String ROOT_DATA = "/content/dam/fondazione";
    private static final String BASE_MODEL = "/conf/fondazione/settings/dam/cfm/models";
    private static final String[] OBJECTS = {EVENTS, CITIES, NATIONS, SUB_TYPES, SPEAKERS, MEDIA, TOPICS, SUPPLIERS,
        EVENTS_TOPICS, EVENTS_IMAGES, EVENTS_SPEAKERS, SPEAKER_IMAGES};
    private static final String TEMPLATE_NOT_FOUND = "Template not found: ";

    private static String eventModelPath = BASE_MODEL + "/event";
    private static String eventParentPath = ROOT_DATA + "/events";
    private static String[] eventSingleFields = {"id", "startDate", "endDate", "ref_city", "ref_nation", "venue", "ref_subscriptionType", 
                                                    "evidenceImageUrl", "programCover", "programPDF", "address", "eventType", "ref_supplier", 
                                                    "ecm", "externalSubscribeLink", "subscription", "format"};
    
    private static String[] eventTopicsSingleFields = {"id", "ref_topics"};

    private static String eventImagesParentPath = ROOT_DATA + "/images/events";

    private static String topicModelPath = BASE_MODEL + "/topic";
    private static String topicParentPath = ROOT_DATA + "/topics";
    private static String[] topicSingleFields = {"id"};

    private static String speakerModelPath = BASE_MODEL + "/speaker";
    private static String speakerParentPath = ROOT_DATA + "/speakers";
    private static String[] speakerSingleFields = {"id", "name", "surname", "company", "role", "photo"};

    private static String speakerImagesParentPath = ROOT_DATA + "/images/speakers";

    private static String nationModelPath = BASE_MODEL + "/nation";
    private static String nationParentPath = ROOT_DATA + "/nations";
    private static String[] nationSingleFields = {"id"};

    private static String cityModelPath = BASE_MODEL + "/city";
    private static String cityParentPath = ROOT_DATA + "/cities";
    private static String[] citySingleFields = {"id", "ref_nation"};

    private static String subscriptionModelPath = BASE_MODEL + "/subscription";
    private static String subscriptionParentPath = ROOT_DATA + "/subscriptionTypes";
    private static String[] subscriptionSingleFields = {"id"};

    private static String supplierModelPath = BASE_MODEL + "/supplier";
    private static String supplierParentPath = ROOT_DATA + "/suppliers";
    private static String[] supplierSingleFields = {"id", "username", "company", "name", "surname", "email"};

    private static String mediaModelPath = BASE_MODEL + "/media";
    private static String mediaParentPath = ROOT_DATA + "/media";
    private static String[] mediaSingleFields = {"id", "ref_event", "ref_speaker", "order", "date", "videoCode", 
                                                 "videoPath", "miniaturePath", "type", "ref_topic"};

    private static String[] eventSpeakersSingleFields = {"id", "ref_speakers"};

    private String[] currentSingleFields;
    private Map<String, String> currentVariationData;
    private Map<String, Object> currentFieldIndexMap;
    private boolean appendRef;
    private QueryManager queryManager;
    private String currentParentPath;
    private boolean delete;

    /**
     * Main method to initiate the data migration process.€
     * Calls individual migration methods for different content types.
     * @throws LoginException 
     * @throws RepositoryException 
     * @throws ContentFragmentException 
     * @throws IOException 
     * @throws InterruptedException 
     * @throws RowProcessException 
     */
    public void migrateData(String object, String exclusions, String deleteInput) throws LoginException, RepositoryException, IOException, ContentFragmentException, InterruptedException, RowProcessException {
        String[] exclusionList = exclusions == null ? new String[0] : exclusions.split(",");
        delete = (deleteInput != null && deleteInput.equals("true")) ? true : false;

        try (ResourceResolver resolver = getResourceResolver()) {
            queryManager = resolver.adaptTo(Session.class).getWorkspace().getQueryManager();

            if (object == null || object.isBlank()) {
                migrateAllContentsWithExclusions(resolver, exclusionList);
            } else if (Arrays.asList(OBJECTS).contains(object.toLowerCase())) {
                migrateSingleContent(resolver, object);
            } else {
                throw new IllegalArgumentException();   
            }

            //TODO connection with image and PDF assets
        } 
    }

    private void migrateSingleContent(ResourceResolver resolver, String object) throws RepositoryException, IOException, ContentFragmentException, RowProcessException {
        switch (object.toLowerCase()) {
            case EVENTS:
                migrateEvents(resolver); // subscriptionTypes cities nations suppliers
                connectEventsTopics(resolver);
                loadLinkEventImages(resolver);
                break;
            case SUPPLIERS:
                migrateSuppliers(resolver);
                break;
            case CITIES:
                migrateCities(resolver); // nations
                break;
            case NATIONS:
                migrateNations(resolver);
                break;
            case SUB_TYPES:
                migrateSubscriptionTypes(resolver);
                break;
            case SPEAKERS:
                migrateSpeakers(resolver);
                connectEventsSpeakers(resolver);
                loadLinkSpeakerImages(resolver);
                break;
            case MEDIA:
                migrateMedia(resolver); // speakers topics events
                break;
            case TOPICS:
                migrateTopics(resolver);
                break;
            case EVENTS_TOPICS:
                connectEventsTopics(resolver);
                break;
            case EVENTS_IMAGES:
                loadLinkEventImages(resolver);
                break;
            case EVENTS_SPEAKERS:
                connectEventsSpeakers(resolver);
                break;
            case SPEAKER_IMAGES:
                loadLinkSpeakerImages(resolver);
                break;
            default:
                break;
        } 
    }

    private void migrateAllContentsWithExclusions(ResourceResolver resolver, String[] exclusionList) throws IOException, RepositoryException, ContentFragmentException, InterruptedException, RowProcessException {
        if (!Arrays.asList(exclusionList).contains(TOPICS)) {
            migrateTopics(resolver);
            //Thread.sleep(2000); // Wait for 2 seconds
        }
        if (!Arrays.asList(exclusionList).contains(SUPPLIERS)) {
            migrateSuppliers(resolver);
            //Thread.sleep(2000); // Wait for 2 seconds
        }
        if (!Arrays.asList(exclusionList).contains(NATIONS)) {
            migrateNations(resolver);
            //Thread.sleep(2000); // Wait for 2 seconds
        }
        if (!Arrays.asList(exclusionList).contains(SUB_TYPES)) {
            migrateSubscriptionTypes(resolver);
            //Thread.sleep(2000); // Wait for 2 seconds
        }
        if (!Arrays.asList(exclusionList).contains(CITIES)) {
            migrateCities(resolver); // nations
            //Thread.sleep(2000); // Wait for 2 seconds
        }
        if (!Arrays.asList(exclusionList).contains(EVENTS)) {
            migrateEvents(resolver); // subscriptionTypes cities nations
            //Thread.sleep(2000); // Wait for 2 seconds
            try {
                connectEventsTopics(resolver);
            } catch (IOException | RepositoryException | ContentFragmentException e) {
                //Thread.sleep(2000); // Wait for 2 seconds
                connectEventsTopics(resolver);
            }
            //Thread.sleep(2000); // Wait for 2 seconds
            try {
                loadLinkEventImages(resolver);
            } catch (IOException | RepositoryException | ContentFragmentException e) {
                //Thread.sleep(2000); // Wait for 2 seconds
                loadLinkEventImages(resolver);
            }
            //Thread.sleep(2000); // Wait for 2 seconds
        }
        if (!Arrays.asList(exclusionList).contains(SPEAKERS)) {
            migrateSpeakers(resolver);
            //Thread.sleep(5000); // Wait for 5 seconds
            try {
                connectEventsSpeakers(resolver);
            } catch (IOException | RepositoryException | ContentFragmentException e) {
                //Thread.sleep(2000); // Wait for 2 seconds
                connectEventsSpeakers(resolver);
            }
            //Thread.sleep(5000); // Wait for 5 seconds
            try {
                loadLinkSpeakerImages(resolver);
            } catch (IOException | RepositoryException | ContentFragmentException e) {
                //Thread.sleep(2000); // Wait for 2 seconds
                loadLinkSpeakerImages(resolver);
            }
            //Thread.sleep(5000); // Wait for 5 seconds
        }
        if (!Arrays.asList(exclusionList).contains(MEDIA)) {
            migrateMedia(resolver); // speakers topics events
            //Thread.sleep(5000); // Wait for 5 seconds
        }
    }

    /**
     * Removes all fragments from the parent Path.
     */
    private void removeAll(ResourceResolver currentResolver) throws PersistenceException {
        if (!delete) {
            return;
        }
        // int batchSize = 250;
        Resource parentResource = currentResolver.getResource(currentParentPath);
        if (parentResource != null) {
            for (Resource resource : parentResource.getChildren()) {
                currentResolver.delete(resource);
            }
            // try {
                // Session session = currentResolver.adaptTo(Session.class);
                // if (session != null) {
                //     Node parentNode = parentResource.adaptTo(Node.class);
                //     if (parentNode != null && parentNode.hasNodes()) {
                //         NodeIterator children = parentNode.getNodes();
                //         int count = 0;
    
                //         while (children.hasNext()) {
                //             Node childNode = children.nextNode();
                //             childNode.remove();
                //             count++;
    
                //             if (count % batchSize == 0) {
                //                 session.save();
                //                 LOGGER.info("Committed deletion of {} nodes", count);
                //             }
                //         }
    
                //         session.save();

                //         LOGGER.info("All nodes removed from parent: {}", currentParentPath);
                //     } else {
                //         LOGGER.warn("Parent node does not exist or has no children for path: {}", currentParentPath);
                //     }
                // }
            // } catch (RepositoryException e) {
            //     throw new PersistenceException("Error while removing nodes in batches", e);
            // }
        } else {
            LOGGER.warn("Parent path does not exist: {}", currentParentPath);
        }
    }

    /**
     * Migrates suppliers data from CSV to Content Fragments.
     * @throws IOException 
     * @throws ContentFragmentException 
     * @throws RepositoryException 
     */
    private void migrateSuppliers(ResourceResolver currentResolver) throws IOException, RepositoryException, ContentFragmentException {

        Resource modelResource = currentResolver.getResource(supplierModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException(TEMPLATE_NOT_FOUND + supplierModelPath);
        }

        appendRef = false;
        currentParentPath = supplierParentPath;
        removeAll(currentResolver); // Remove all existing fragments, to be done after currentParentPath is set
        currentSingleFields = supplierSingleFields;
        currentFieldIndexMap = initSupplierFieldIndexMap();
        Resource parentResource = ensureFolderExists(currentResolver);

        try (InputStream inputStream = fileReaderService.getFileAsStream(CSV_PATH + "fornitori.csv");
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

                processCsvRow(fields, fields[0], fields[2] + " " + fields[3]+ " " + fields[4], template, parentResource, currentResolver);

                count++;
                if (count % 100 == 0) {
                    currentResolver.commit();
                }
            }
        } finally {
            if (currentResolver != null) {
                currentResolver.commit();
                currentResolver.refresh();
            }
        }
    }

    /**
     * Migrates topic data from CSV to Content Fragments.
     * @throws IOException 
     * @throws ContentFragmentException 
     * @throws RepositoryException 
     */
    private void migrateTopics(ResourceResolver currentResolver) throws IOException, RepositoryException, ContentFragmentException {

        Resource modelResource = currentResolver.getResource(topicModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException(TEMPLATE_NOT_FOUND + topicModelPath);
        }

        appendRef = false;
        currentParentPath = topicParentPath;
        removeAll(currentResolver); // Remove all existing fragments, to be done after currentParentPath is set
        currentSingleFields = topicSingleFields;
        currentFieldIndexMap = initTopicFieldIndexMap();
        Resource parentResource = ensureFolderExists(currentResolver);

        try (InputStream inputStream = fileReaderService.getFileAsStream(CSV_PATH + "topics.csv");
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
                currentVariationData.put(NAME_IT, fields[1]);
                currentVariationData.put(NAME_EN, fields[2]);

                processCsvRow(fields, fields[0], fields[1], template, parentResource, currentResolver);

                count++;
                if (count % 100 == 0) {
                    currentResolver.commit();
                }
            }
        } finally {
            if (currentResolver != null) {
                currentResolver.commit();
                currentResolver.refresh();
            }
        }
    }

    /**
     * Migrates speaker data from CSV to Content Fragments.
     * @throws IOException 
     * @throws ContentFragmentException 
     * @throws RepositoryException 
     */
    private void migrateSpeakers(ResourceResolver currentResolver) throws IOException, RepositoryException, ContentFragmentException {
        Resource modelResource = currentResolver.getResource(speakerModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException(TEMPLATE_NOT_FOUND + speakerModelPath);
        }

        appendRef = false;
        currentParentPath = speakerParentPath;
        removeAll(currentResolver); // Remove all existing fragments, to be done after currentParentPath is set
        currentSingleFields = speakerSingleFields;
        currentFieldIndexMap = initSpeakerFieldIndexMap();
        Resource parentResource = ensureFolderExists(currentResolver);

        try (InputStream inputStream = fileReaderService.getFileAsStream(CSV_PATH + "speakers.csv");
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

                processCsvRow(fields, fields[0], fields[1] + " " + fields[2], template, parentResource, currentResolver);
                
                count++;
                if (count % 100 == 0) {
                    currentResolver.commit();
                    currentResolver.refresh();
                }
            }
        } finally {
            if (currentResolver != null) {
                currentResolver.commit();
                currentResolver.refresh();
            }
        }
    }

    /**
     * Migrates nation data from CSV to Content Fragments.
     * @throws IOException 
     * @throws ContentFragmentException 
     * @throws RepositoryException 
     */
    private void migrateNations(ResourceResolver currentResolver) throws IOException, RepositoryException, ContentFragmentException {
        Resource modelResource = currentResolver.getResource(nationModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException(TEMPLATE_NOT_FOUND + nationModelPath);
        }

        appendRef = false;
        currentParentPath = nationParentPath;
        removeAll(currentResolver); // Remove all existing fragments, to be done after currentParentPath is set
        currentSingleFields = nationSingleFields;
        currentFieldIndexMap = initNationFieldIndexMap();
        Resource parentResource = ensureFolderExists(currentResolver);

        try (InputStream inputStream = fileReaderService.getFileAsStream(CSV_PATH + "nazioni.csv");
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
                currentVariationData.put(NAME_IT, fields[1]);
                currentVariationData.put(NAME_EN, fields[2]);

                processCsvRow(fields, fields[0], fields[1], template, parentResource, currentResolver);
                
                count++;
                if (count % 100 == 0) {
                    currentResolver.commit();
                }
            }
        } finally {
            if (currentResolver != null) {
                currentResolver.commit();
                currentResolver.refresh();
            }
        }
    }

    /**
     * Migrates city data from CSV to Content Fragments.
     * @throws RepositoryException 
     * @throws IOException 
     * @throws ContentFragmentException 
     */
    private void migrateCities(ResourceResolver currentResolver) throws RepositoryException, IOException, ContentFragmentException {

        if (!checkReferencesExist("/content/dam/fondazione/nations", currentResolver)) {
            throw new RepositoryException("Missing references. Migrate nations before cities.");
        }

        Resource modelResource = currentResolver.getResource(cityModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException(TEMPLATE_NOT_FOUND + cityModelPath);
        }

        appendRef = false;
        currentParentPath = cityParentPath;
        removeAll(currentResolver); // Remove all existing fragments, to be done after currentParentPath is set
        currentSingleFields = citySingleFields;
        currentFieldIndexMap = initCityFieldIndexMap();
        Resource parentResource = ensureFolderExists(currentResolver);

        try (InputStream inputStream = fileReaderService.getFileAsStream(CSV_PATH + "citta.csv");
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
                currentVariationData.put(NAME_IT, fields[2]);
                currentVariationData.put(NAME_EN, fields[3]);

                processCsvRow(fields, fields[0], fields[2], template, parentResource, currentResolver);
                
                count++;
                if (count % 100 == 0) {
                    currentResolver.commit();
                }
            }
        } finally {
            if (currentResolver != null) {
                currentResolver.commit();
                currentResolver.refresh();
            }
        }
    }

    /**
     * Migrates event data from CSV to Content Fragments.
     * @throws RepositoryException 
     * @throws IOException 
     * @throws ContentFragmentException 
     */
    private void migrateEvents(ResourceResolver currentResolver) throws RepositoryException, IOException, ContentFragmentException {

        if (!checkReferencesExist("/content/dam/fondazione/nations", currentResolver) ||
            !checkReferencesExist("/content/dam/fondazione/cities", currentResolver) ||
            !checkReferencesExist("/content/dam/fondazione/subscriptionTypes", currentResolver)) {
        
            throw new RepositoryException("Missing references. Migrate nations, cities and subscriptionTypes before events.");
        }

        Resource modelResource = currentResolver.getResource(eventModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException(TEMPLATE_NOT_FOUND + eventModelPath);
        }

        appendRef = false;
        currentParentPath = eventParentPath;
        removeAll(currentResolver); // Remove all existing fragments, to be done after currentParentPath is set
        currentSingleFields = eventSingleFields;
        currentFieldIndexMap = initEventFieldIndexMap();
        Resource parentResource = ensureFolderExists(currentResolver);

        try (InputStream inputStream = fileReaderService.getFileAsStream(CSV_PATH + "events.csv");
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
                currentVariationData.put("title_it", fields[1]);
                currentVariationData.put("title_en", fields[2]);
                currentVariationData.put("description_it", fields[5]);
                currentVariationData.put("description_en", fields[6]);
                currentVariationData.put("presentationDescription_it", fields[12]);
                currentVariationData.put("presentationDescription_en", fields[13]);

                ContentFragment cfm = processCsvRow(fields, fields[0], fields[1], template, parentResource, currentResolver);
                ContentElement slugElement = cfm.getElement("slug");
                slugElement.setContent(cfm.getName(), slugElement.getContentType());
                
                count++;
                if (count % 100 == 0) {
                    currentResolver.commit();
                    currentResolver.refresh();
                }
            }
        } finally {
            if (currentResolver != null) {
                currentResolver.commit();
                currentResolver.refresh();
            }
        }
    }
    
    /**
     * Migrates subscription data from CSV to Content Fragments.
     * @throws IOException 
     * @throws ContentFragmentException 
     * @throws RepositoryException 
     */
    private void migrateSubscriptionTypes(ResourceResolver currentResolver) throws IOException, RepositoryException, ContentFragmentException {
        Resource modelResource = currentResolver.getResource(subscriptionModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException(TEMPLATE_NOT_FOUND + subscriptionModelPath);
        }

        appendRef = false;
        currentParentPath = subscriptionParentPath;
        removeAll(currentResolver); // Remove all existing fragments, to be done after currentParentPath is set
        currentSingleFields = subscriptionSingleFields;
        currentFieldIndexMap = initSubscriptionFieldIndexMap();
        Resource parentResource = ensureFolderExists(currentResolver);

        try (InputStream inputStream = fileReaderService.getFileAsStream(CSV_PATH + "subscriptions.csv");
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

                processCsvRow(fields, fields[0], fields[1], template, parentResource, currentResolver);
                
                count++;
                if (count % 100 == 0) {
                    currentResolver.commit();
                }
            }
        } finally {
            if (currentResolver != null) {
                currentResolver.commit();
                currentResolver.refresh();
            }
        }
    }

    /**
     * Connects events with their corresponding topics.
     * @throws IOException 
     * @throws ContentFragmentException 
     * @throws RepositoryException 
     */
    private void connectEventsTopics(ResourceResolver currentResolver) throws IOException, RepositoryException, ContentFragmentException {

        Resource modelResource = currentResolver.getResource(eventModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException(TEMPLATE_NOT_FOUND + eventModelPath);
        }

        appendRef = true;
        currentParentPath = eventParentPath;
        currentSingleFields = eventTopicsSingleFields;
        currentFieldIndexMap = initEventTopicsFieldIndexMap();
        Resource parentResource = ensureFolderExists(currentResolver);

        try (InputStream inputStream = fileReaderService.getFileAsStream(CSV_PATH + "events_topics.csv");
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

                processCsvRow(fields, fields[0], null, template, parentResource, currentResolver);
                
                if(count % 100 == 0) {
                    currentResolver.commit();
                    currentResolver.refresh();
                }
            }
        } finally {
            if (currentResolver != null) {
                currentResolver.commit();
                currentResolver.refresh();
            }
        }

        List<ContentFragment> fragments = ModelHelper.findAllFragmentsByPath(currentResolver, eventParentPath);
        for (ContentFragment fragment : fragments) {
            updateVariationReferences(fragment, "topics");
        }
        currentResolver.commit();
        currentResolver.refresh();
    }

    /**
     * Connects events with their corresponding speakers.
     * @throws IOException 
     * @throws ContentFragmentException 
     * @throws RepositoryException 
     */
    private void connectEventsSpeakers(ResourceResolver currentResolver) throws IOException, RepositoryException, ContentFragmentException {

        Resource modelResource = currentResolver.getResource(eventModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException(TEMPLATE_NOT_FOUND + eventModelPath);
        }

        appendRef = true;
        currentParentPath = eventParentPath;
        currentSingleFields = eventSpeakersSingleFields;
        currentFieldIndexMap = initEventSpeakersFieldIndexMap();
        Resource parentResource = ensureFolderExists(currentResolver);

        try (InputStream inputStream = fileReaderService.getFileAsStream(CSV_PATH + "events_speakers.csv");
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

                processCsvRow(fields, fields[0], null, template, parentResource, currentResolver);
                
                if(count % 100 == 0) {
                    currentResolver.commit();
                    currentResolver.refresh();
                }
            }
        } finally {
            if (currentResolver != null) {
                currentResolver.commit();
                currentResolver.refresh();
            }
        }

        List<ContentFragment> fragments = ModelHelper.findAllFragmentsByPath(currentResolver, eventParentPath);
        for (ContentFragment fragment : fragments) {
            updateVariationReferences(fragment, "speakers");
        }
        currentResolver.commit();
        currentResolver.refresh();
    }


    /**
     * Migrates topic data from CSV to Content Fragments.
     * @throws RepositoryException 
     * @throws IOException 
     * @throws RowProcessException 
     */
    private void migrateMedia(ResourceResolver currentResolver) throws RepositoryException, IOException, RowProcessException {

        if (!checkReferencesExist("/content/dam/fondazione/speakers", currentResolver) ||
            !checkReferencesExist("/content/dam/fondazione/topics", currentResolver) ||
            !checkReferencesExist("/content/dam/fondazione/events", currentResolver)) {
        
            throw new RepositoryException("Missing references. Migrate speakers, topics and events before media.");
        }

        Resource modelResource = currentResolver.getResource(mediaModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException(TEMPLATE_NOT_FOUND + mediaModelPath);
        }

        appendRef = false;
        currentParentPath = mediaParentPath;
        removeAll(currentResolver); // Remove all existing fragments, to be done after currentParentPath is set
        currentSingleFields = mediaSingleFields;
        currentFieldIndexMap = initMediaFieldIndexMap();
        Resource parentResource = ensureFolderExists(currentResolver);

        try (InputStream inputStream = fileReaderService.getFileAsStream(CSV_PATH + "media.csv");
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
                currentVariationData.put("title_it", fields[5]);
                currentVariationData.put("title_en", fields[6]);
                currentVariationData.put("description_it", fields[7]);
                currentVariationData.put("description_en", fields[8]);

                try {
                    processCsvRow(fields, fields[0], fields[5], template, parentResource, currentResolver);
                } catch (RepositoryException | ContentFragmentException e) {
                    throw new RowProcessException("Id: " + fields[0], e);
                }

                count++;
                if (count % 100 == 0) {
                    currentResolver.commit();
                    currentResolver.refresh();
                }
            }
        } finally {
            if (currentResolver != null) {
                currentResolver.commit();
                currentResolver.refresh();
            }
        }
    }

    /**
     * Loads speaker images from a specified directory and links them to speaker content fragments.
     * @throws RepositoryException 
     * @throws PersistenceException 
     * @throws ContentFragmentException 
     */
    private void loadLinkSpeakerImages(ResourceResolver currentResolver) throws RepositoryException, PersistenceException, ContentFragmentException {

        if (!checkReferencesExist("/content/dam/fondazione/speakers", currentResolver)) {
            throw new RepositoryException("Migrate speakers before loading images.");
        }

        currentParentPath = speakerImagesParentPath;
        ensureFolderExists(currentResolver);

        AssetManager assetManager = currentResolver.adaptTo(AssetManager.class);

        Resource modelResource = currentResolver.getResource(speakerModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException(TEMPLATE_NOT_FOUND + speakerParentPath);
        }

        currentParentPath = speakerParentPath;
        Resource parentResource = ensureFolderExists(currentResolver);
        if (parentResource == null) {
            throw new IllegalStateException("Parent resource could not be created or retrieved.");
        }

        String replace = ".PhotoContent";

        Asset defaultAsset = loadAsset("default.PhotoContent.png", SPEAKER_IMAGES_PATH, speakerImagesParentPath, replace, assetManager, currentResolver);
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
            asset = loadAsset(fileName, SPEAKER_IMAGES_PATH, speakerImagesParentPath, replace, assetManager, currentResolver);
            if (asset == null) {
                fileName = id + replace + ".png";
                asset = loadAsset(fileName, SPEAKER_IMAGES_PATH, speakerImagesParentPath, replace, assetManager, currentResolver);
            }
            if (asset == null) {
                asset = defaultAsset;
            }

            String assetReference = asset.getPath();
            ContentElement photoElement = cfm.getElement("photo");
            if (photoElement != null) {
                photoElement.setContent(assetReference, photoElement.getContentType());
                currentResolver.commit();
            }
        }
    }

    /**
     * Loads event images from a specified directory and links them to event content fragments.
     * @throws RepositoryException 
     * @throws PersistenceException 
     * @throws ContentFragmentException 
     */
    private void loadLinkEventImages(ResourceResolver currentResolver) throws RepositoryException, PersistenceException, ContentFragmentException {

        if (!checkReferencesExist("/content/dam/fondazione/events", currentResolver)) {
            throw new RepositoryException("Migrate events before loading images.");
        }

        currentParentPath = eventImagesParentPath;
        ensureFolderExists(currentResolver);

        AssetManager assetManager = currentResolver.adaptTo(AssetManager.class);

        Resource modelResource = currentResolver.getResource(eventModelPath);
        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new IllegalArgumentException(TEMPLATE_NOT_FOUND + speakerParentPath);
        }

        currentParentPath = eventParentPath;
        Resource parentResource = ensureFolderExists(currentResolver);
        if (parentResource == null) {
            throw new IllegalStateException("Parent resource could not be created or retrieved.");
        }

        String replace = ".FileContent";

        Iterator<Resource> fragments = parentResource.listChildren();
        while (fragments.hasNext()) {
            Resource fragmentResource = fragments.next();
            ContentFragment cfm = fragmentResource.adaptTo(ContentFragment.class);
            ContentElement element = cfm.getElement("id");
            String id = element.getValue().getValue().toString();

            String fileName = id + replace + ".jpg";

            Asset asset = null;
            asset = loadAsset(fileName, EVENT_IMAGES_PATH, eventImagesParentPath, replace, assetManager, currentResolver);
            if (asset == null) {
                fileName = id + replace + ".png";
                asset = loadAsset(fileName, EVENT_IMAGES_PATH, eventImagesParentPath, replace, assetManager, currentResolver);
            }
            if (asset == null) {
                continue;
            }

            String assetReference = asset.getPath();
            ContentElement photoElement = cfm.getElement("presentationImage");
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
    private Asset loadAsset(String fileName, String fromPath, String toPath, String replace, AssetManager assetManager, ResourceResolver currentResolver) {
        Asset asset = null;
        String assetPath = toPath + PATH_SEPARATOR + fileName.replace(replace, "");
        Resource assetResource = currentResolver.getResource(assetPath);
        
        if (assetResource != null) {
            asset = assetResource.adaptTo(Asset.class);
            return asset;
        }

        try (InputStream inputStream = fileReaderService.getFileAsStream(fromPath + fileName)) {
            if (inputStream != null) {
                asset = assetManager.createAsset(assetPath, inputStream, "image/jpeg", true);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load asset: " + fileName, e);
        }
        return asset;
    }

    /**
     * Processes a CSV row and performs creates or updates a Content Fragment.
     *
     * @param fields          An array of strings representing the fields in the CSV row.
     * @param id              The unique identifier for the content fragment.
     * @param name            The name of the content fragment.
     * @param template        The fragment template used to create new content fragments.
     * @param parentResource  The parent resource under which the content fragment will be created.
     * @param currentResolver The resource resolver used to access and modify resources.
     * @throws RepositoryException 
     * @throws ContentFragmentException 
     * @throws Exception      If an error occurs during the processing of the CSV row.
     *
     * This method logs the ID and name, attempts to find an existing content fragment by ID,
     * and creates a new content fragment if it does not exist and the name is provided.
     * It then updates the fields of the content fragment and its variations.
     */
    private ContentFragment processCsvRow(String[] fields,  String id, String name, FragmentTemplate template, Resource parentResource, ResourceResolver currentResolver) throws RepositoryException, ContentFragmentException {

        LOGGER.info("Processing ID: {}", id);
            
        ContentFragment cfm = ModelHelper.findFragmentById(currentResolver, id, currentParentPath);
        
        if (cfm == null && name != null) { // check for association jobs that should not create fragments
            cfm = template.createFragment(parentResource, slugify(name), name);
            if (cfm == null) {
                LOGGER.error("Unable to create ContentFragment {} for ID: {}", name, id);
                throw new IllegalStateException("Failed to create or retrieve content fragment for ID: " + id);
            }
            LOGGER.info("Created ContentFragment {} for ID: {}", cfm.getName(), id);
        } else if (cfm != null) {
            LOGGER.info("Retrieved ContentFragment {} for ID: {}", cfm.getName(), id);
        } else {
            LOGGER.error("ContentFragment for ID: {} does not exist and the mandatory \"name\" parameter for creation is missing", id);
        }

        updateFields(cfm, fields, currentResolver);
        
        updateContentFragmentVariations(cfm);

        return cfm;
    }

    /**
     * Updates Content Fragment variations for different languages.
     * @throws ContentFragmentException 
     */
    private void updateContentFragmentVariations(ContentFragment cfm) throws ContentFragmentException {
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
    private void updateFields(ContentFragment cfm, String[] fields, ResourceResolver currentResolver) throws ContentFragmentException, RepositoryException {
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
                    ContentFragment refFragment = ModelHelper.findFragmentById(currentResolver, newVal, refPath);
                    if (refFragment == null) {
                        continue;
                    } else {
                        newVal = refFragment.getName();
                    }
                    String newValPath = refPath + newVal;

                    FragmentData fragmentData = elementRef.getValue();
                    Object elementContent = fragmentData.getValue();
                    boolean isArray = elementContent != null && elementContent.getClass().isArray();
                    List<String> oldVals = null;
                    if (elementContent instanceof String[] && ((String[])elementContent).length > 0) {
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

    private void updateVariationReferences(ContentFragment fragment, String field) throws ContentFragmentException {
        ContentElement element = fragment.getElement(field);
        if (element != null) {
            FragmentData fragmentData = element.getValue();

            if (fragmentData != null && fragmentData.getValue() != null) {
                Iterator<VariationDef> variations = fragment.listAllVariations();
                while (variations.hasNext()) {
                    VariationDef variation = variations.next();
                    String variationName = variation.getName();
                    ContentVariation cv = element.getVariation(variationName);
                    cv.setValue(fragmentData);
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
                LOGGER.error("{} is not a valid date", val);
            } catch (ContentFragmentException e) {
                LOGGER.error("Error setting date value", e);
            } 
            return true;
        }
        return false;
    }

    /**
     * Parses a CSV line, handling quoted fields and escaping internal quotes.
     */
    private static String[] parseCsvLine(String line) {
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
    
        // Se la linea termina con una virgola, aggiungi un campo vuoto
        if (line.endsWith(",")) {
            fields = Arrays.copyOf(fields, fields.length + 1);
            fields[fields.length - 1] = "";
        }
    
        return fields;
    }
    
    /**
     * Ensures that the specified folder path exists in the JCR repository.
     * Creates the folder structure if it doesn't exist.
     */
    private Resource ensureFolderExists(ResourceResolver currentResolver) throws PersistenceException {
        Resource folderResource = currentResolver.getResource(currentParentPath);
        Resource prevPath = null;
        if (folderResource == null) {
            String parentPath = StringUtils.remove(currentParentPath, ROOT_DATA);
            String[] parts = parentPath.split(PATH_SEPARATOR);
            StringBuilder pathBuilder = new StringBuilder();
            pathBuilder.append(ROOT_DATA);
            for (String part : parts) {
                pathBuilder.append(part).append(PATH_SEPARATOR);
                String currentPath = pathBuilder.toString();
                Resource currentResource = currentResolver.getResource(currentPath);
                if ( currentResource == null) {
                    Map<String, Object> properties = new HashMap<>();
                    properties.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_FOLDER);
                    folderResource = currentResolver.create(prevPath, part, properties);
                    prevPath = folderResource;
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
     * @throws LoginException 
     */
    private ResourceResolver getResourceResolver() throws LoginException {
        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, SERVICE);
        return resolverFactory.getServiceResourceResolver(param);
    }

    /**
     * Build the slug value for fragment name
     */
    private static String slugify(String name) {
        // Normalization
        String slug = name.toLowerCase()
                .replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}\\s]", "")
                .replaceAll("\\s+", "-");

        // Max 40 chars
        if (slug.length() > 100) {
            slug = slug.substring(0, 100);
        }

        // Remove final cut
        slug = slug.replaceAll("-$", "");

        return slug;
    }

    /**
     * Find the fragment by id field
     * @throws RepositoryException 
     */
    public ContentFragment findFragmentByIdOld(String id, String path, ResourceResolver currentResolver) throws RepositoryException {
        String queryStr = "SELECT * FROM [dam:Asset] AS s WHERE ISDESCENDANTNODE(s, '" + path + "') AND s.[" + JcrConstants.JCR_CONTENT + "/data/master/id] = '" + id + "'";

        Query query = queryManager.createQuery(queryStr, Query.JCR_SQL2);
        QueryResult result = query.execute();

        while (result.getRows().hasNext()) {
            String nodePath = result.getRows().nextRow().getPath();
            // using s.[jcr:content/data/master/id] instead of s.[id] in the query returns only the master, the statement below might be unnecessary
            if (nodePath.contains(PATH_SEPARATOR + JcrConstants.JCR_CONTENT + PATH_SEPARATOR)) {
                nodePath = nodePath.substring(0, nodePath.indexOf(PATH_SEPARATOR + JcrConstants.JCR_CONTENT));
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
    private boolean checkReferencesExist(String referencePath, ResourceResolver currentResolver) {
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

    private HashMap<String, Object> initTopicFieldIndexMap() {
        HashMap<String, Object> topicFieldIndexMap = new HashMap<>();
        topicFieldIndexMap.put("id", 0);
        return topicFieldIndexMap;
    }

    private HashMap<String, Object> initEventFieldIndexMap() {
        HashMap<String, Object> eventFieldIndexMap = new HashMap<>();
        eventFieldIndexMap.put("id", 0);
        eventFieldIndexMap.put("startDate", 3);
        eventFieldIndexMap.put("endDate", 4);
        eventFieldIndexMap.put("city", 7);
        eventFieldIndexMap.put("ref_city", "/content/dam/fondazione/cities/");
        eventFieldIndexMap.put("nation", 8);
        eventFieldIndexMap.put("ref_nation", "/content/dam/fondazione/nations/");
        eventFieldIndexMap.put("subscriptionType", 9);
        eventFieldIndexMap.put("ref_subscriptionType", "/content/dam/fondazione/subscriptionTypes/");
        eventFieldIndexMap.put("venue", 10);
        eventFieldIndexMap.put("evidenceImageUrl", 11);
        eventFieldIndexMap.put("programCover", 14);
        eventFieldIndexMap.put("programPDF", 15);
        eventFieldIndexMap.put("address", 16);
        eventFieldIndexMap.put("eventType", 17);
        eventFieldIndexMap.put("supplier", 18);
        eventFieldIndexMap.put("ref_supplier", "/content/dam/fondazione/suppliers/");
        eventFieldIndexMap.put("ecm", 19);
        eventFieldIndexMap.put("externalSubscribeLink", 20);
        eventFieldIndexMap.put("subscription", 21);
        eventFieldIndexMap.put("format", 22);
        return eventFieldIndexMap;
    }

    private Map<String, Object> initEventTopicsFieldIndexMap() {
        Map<String, Object> eventTopicsFieldIndexMap = new HashMap<>();
        eventTopicsFieldIndexMap.put("id", 0);
        eventTopicsFieldIndexMap.put(TOPICS, 1);
        eventTopicsFieldIndexMap.put("ref_topics", "/content/dam/fondazione/topics/");
        return eventTopicsFieldIndexMap;
    }

    private Map<String, Object> initSpeakerFieldIndexMap() {
        Map<String, Object> speakerFieldIndexMap = new HashMap<>();
        speakerFieldIndexMap.put("id", 0);
        speakerFieldIndexMap.put("name", 1);
        speakerFieldIndexMap.put("surname", 2);
        speakerFieldIndexMap.put("company", 3);
        speakerFieldIndexMap.put("role", 4);
        speakerFieldIndexMap.put("photo", 5);
        return speakerFieldIndexMap;
    }
    
    private Map<String, Object> initNationFieldIndexMap() {
        Map<String, Object> nationFieldIndexMap = new HashMap<>();
        nationFieldIndexMap.put("id", 0);
        return nationFieldIndexMap;
    }
    
    private Map<String, Object> initCityFieldIndexMap() {
        Map<String, Object> cityFieldIndexMap = new HashMap<>();
        cityFieldIndexMap.put("id", 0);
        cityFieldIndexMap.put("nation", 1);
        cityFieldIndexMap.put("ref_nation", "/content/dam/fondazione/nations/");
        return cityFieldIndexMap;
    }
    
    private Map<String, Object> initSubscriptionFieldIndexMap() {
        Map<String, Object> subscriptionFieldIndexMap = new HashMap<>();
        subscriptionFieldIndexMap.put("id", 0);
        return subscriptionFieldIndexMap;
    }
    
    private Map<String, Object> initSupplierFieldIndexMap() {
        Map<String, Object> supplierFieldIndexMap = new HashMap<>();
        supplierFieldIndexMap.put("id", 0);
        supplierFieldIndexMap.put("username", 1);
        supplierFieldIndexMap.put("company", 2);
        supplierFieldIndexMap.put("name", 3);
        supplierFieldIndexMap.put("surname", 4);
        supplierFieldIndexMap.put("email", 5);
        return supplierFieldIndexMap;
    }
    
    private Map<String, Object> initMediaFieldIndexMap() {
        Map<String, Object> mediaFieldIndexMap = new HashMap<>();
        mediaFieldIndexMap.put("id", 0);
        mediaFieldIndexMap.put("event", 1);
        mediaFieldIndexMap.put("ref_event", "/content/dam/fondazione/events/");
        mediaFieldIndexMap.put("speaker", 2);
        mediaFieldIndexMap.put("ref_speaker", "/content/dam/fondazione/speakers/");
        mediaFieldIndexMap.put("order", 3);
        mediaFieldIndexMap.put("date", 4);
        mediaFieldIndexMap.put("videoCode", 9);
        mediaFieldIndexMap.put("videoPath", 10);
        mediaFieldIndexMap.put("miniaturePath", 11);
        mediaFieldIndexMap.put("type", 12);
        mediaFieldIndexMap.put("topic", 13);
        mediaFieldIndexMap.put("ref_topic", "/content/dam/fondazione/topics/");
        return mediaFieldIndexMap;
    }
    
    private Map<String, Object> initEventSpeakersFieldIndexMap() {
        Map<String, Object> eventSpeakersFieldIndexMap = new HashMap<>();
        eventSpeakersFieldIndexMap.put("id", 0);
        eventSpeakersFieldIndexMap.put(SPEAKERS, 1);
        eventSpeakersFieldIndexMap.put("ref_speakers", "/content/dam/fondazione/speakers/");
        return eventSpeakersFieldIndexMap;
    }

}
