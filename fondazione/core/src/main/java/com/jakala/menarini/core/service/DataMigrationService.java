package com.jakala.menarini.core.service;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component(service = DataMigrationService.class)
public class DataMigrationService {

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private SlingRepository slingRepository;

    private static String CSV_PATH = "/csv/";
    private static String ROOT_PATH = "/content/fondazione/resources";
    private static String LOCATION_PATH = "/locations";
    private static String VENUE_PATH = "/venues";
    private static String EVENT_PATH = "/events";
    private static String TOPIC_PATH = "/topics";
    private static String SPEAKER_PATH = "/speakers";
    private static String NODE_TYPE = "nt:unstructured";
    private static String FOLDER_TYPE = "sling:Folder";
    private static String IT = "/it";
    private static String EN = "/en";
    private static String SERVICE = "data-migration-service";


    public void migrateData() throws Exception {
          
        try (ResourceResolver resolver = getResourceResolver()) {
           Session session = resolver.adaptTo(Session.class);
           Node root = session.getNode(ROOT_PATH);
            migrateTopics(session, root);
            migrateSpeakers(session, root);
            // migrateLocations(session, root);
            // migrateVenues(session, root);
            // migrateEvents(session, root);

            session.logout();
        }
    }


    private void migrateTopics(Session session, Node root) throws Exception {
        Node itNode = getOrCreateNode(root, TOPIC_PATH + IT);
        Node enNode = getOrCreateNode(root, TOPIC_PATH + EN);

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CSV_PATH + "topics.csv");
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) { //skip header
                    isFirstLine = false;
                    continue;
                }
                String[] fields = line.split(",");
                String id = getField(fields, 0);
                String name_it = getField(fields, 1);
                String name_en = getField(fields, 2);
                String level = "1";

                // Crea il nodo it
                Node itSubNode = itNode.hasNode(id) ? itNode.getNode(id) : itNode.addNode(id, NODE_TYPE);
                itSubNode.setProperty("id", id);
                itSubNode.setProperty("name", name_it);
                itSubNode.setProperty("level", level);
                
                // Crea il nodo en
                Node enSubNode = enNode.hasNode(id) ? enNode.getNode(id) : enNode.addNode(id, NODE_TYPE);
                enSubNode.setProperty("id", id);
                enSubNode.setProperty("name", name_en);
                enSubNode.setProperty("level", level);
            }
            session.save();
        } 
    }

    private void migrateSpeakers(Session session, Node root) throws Exception {
        Node itNode = getOrCreateNode(root, SPEAKER_PATH + IT);
        Node enNode = getOrCreateNode(root, SPEAKER_PATH + EN);

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CSV_PATH + "speakers.csv");
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) { //skip header
                    isFirstLine = false;
                    continue;
                }
                String[] fields = line.split(";");
                String id = getField(fields, 0);
                String firstname = getField(fields, 1);
                String lastname = getField(fields, 2);
                String bio = "";
                String cv = getField(fields, 3);
                String pathPhoto = "";

                // Crea il nodo it
                Node nodeIt = itNode.hasNode(id) ? itNode.getNode(id) : itNode.addNode(id, NODE_TYPE);
                nodeIt.setProperty("id", id);
                nodeIt.setProperty("firstname", firstname);
                nodeIt.setProperty("lastname", lastname);
                nodeIt.setProperty("bio", bio);
                nodeIt.setProperty("cv", cv);
                nodeIt.setProperty("pathPhoto", pathPhoto);
                
                // Crea il nodo en
                Node nodeEn = enNode.hasNode(id) ? enNode.getNode(id) : enNode.addNode(id, NODE_TYPE);
                nodeEn.setProperty("id", id);
                nodeEn.setProperty("firstname", firstname);
                nodeEn.setProperty("lastname", lastname);
                nodeEn.setProperty("bio", bio);
                nodeEn.setProperty("cv", cv);
                nodeEn.setProperty("pathPhoto", pathPhoto);
            }
            session.save();
        }
    }

    private void migrateLocations(Session session) throws Exception {
        // Similar logic for locations, using a different CSV file
    }

    private void migrateVenues(Session session) throws Exception {
        // Similar logic for venues, using a different CSV file
    }

    private void migrateEvents(Session session, Node root) throws Exception {
        Node itNode = getOrCreateNode(root, EVENT_PATH + IT);
        Node enNode = getOrCreateNode(root, EVENT_PATH + EN);

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CSV_PATH + "events.csv");
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] fields = line.split(",");
                String id = fields[0];
                String title = fields[1];
                String date = fields[2];
                String locationPath = LOCATION_PATH + fields[3];
                String venuePath = VENUE_PATH + fields[4];

                Node nodeIt = itNode.addNode(id, NODE_TYPE);
                itNode.setProperty("title", title);
                itNode.setProperty("date", date);
                itNode.setProperty("locationPath", locationPath);
                itNode.setProperty("venuePath", venuePath);

                String[] itTopicPaths = getRelatedPaths("topics", fields[0]);
                String[] itSpeakerPaths = getRelatedPaths("speakers", fields[0]);

                nodeIt.setProperty("topicPaths", itTopicPaths);
                nodeIt.setProperty("speakerPaths", itSpeakerPaths);

                Node nodeEn = enNode.addNode(id, NODE_TYPE);
                nodeEn.setProperty("title", title);
                nodeEn.setProperty("date", date);
                nodeEn.setProperty("locationPath", locationPath);
                nodeEn.setProperty("venuePath", venuePath);

                String[] enTopicPaths = getRelatedPaths("topics", fields[0]);
                String[] enSpeakerPaths = getRelatedPaths("speakers", fields[0]);

                nodeEn.setProperty("topicPaths", enTopicPaths);
                nodeEn.setProperty("speakerPaths", enSpeakerPaths);
            }
            session.save();
        }
    }

    private Node getOrCreateNode(Node parentNode, String path) throws RepositoryException {
        String[] segments = path.split("/");
        Node currentNode = parentNode;

        for (String segment : segments) {
            if (!segment.isEmpty()) {
                if (!currentNode.hasNode(segment)) {
                    currentNode = currentNode.addNode(segment, FOLDER_TYPE);
                } else {
                    currentNode = currentNode.getNode(segment);
                }
            }
        }

        return currentNode;
    }

    private String[] getRelatedPaths(String entityType, String id) throws Exception {
        return new String[]{ROOT_PATH + entityType + "/" + id};
    }

    private ResourceResolver getResourceResolver() throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, SERVICE);
        return resolverFactory.getServiceResourceResolver(param);
    }

    private String getField(String [] fields, int i){
       return  fields.length > i ? fields[i] : "";
    }
}
