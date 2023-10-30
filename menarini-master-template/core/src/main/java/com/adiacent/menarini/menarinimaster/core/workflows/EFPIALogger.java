package com.adiacent.menarini.menarinimaster.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.HistoryItem;
import com.adobe.granite.workflow.exec.WorkItem;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.wcm.foundation.TextFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;

import javax.jcr.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.Format;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class EFPIALogger {


    private final String siteName;
    private final Level level;
    private int levelMask;

    private String userId;
    private Session jcrSession;
    private Node jcrNode;

    private static final String ROOT_LOG_PATH_PATTERN = "/content/dam/efpia/{0}/log";

    public static enum Level {
        DEBUG((int) (Math.pow(2, 0)), "D"),
        INFO((int) (Math.pow(2, 1)), "I"),
        WARNING((int) (Math.pow(2, 2)), "W"),
        ERROR((int) Math.pow(2, 3), "E");

        private int value;
        private String label;

        private Level(int value, String label) {
            this.value = value;
            this.label = label;
        }
    }

    public static EFPIALogger getLogger(String siteName, Level level, WorkItem workItem, WorkflowSession workflowSession) throws RepositoryException, WorkflowException, PersistenceException {
        EFPIALogger logger = new EFPIALogger(siteName, level);
        logger.jcrSession =  workflowSession.adaptTo(Session.class);
        List<HistoryItem> history = workflowSession.getHistory(workItem.getWorkflow());
        logger.userId = "nobody";
        if (workItem.getWorkflowData().getMetaDataMap() != null)
            if (workItem.getWorkflowData().getMetaDataMap().get("userId") != null)
                logger.userId = workItem.getWorkflowData().getMetaDataMap().get("userId").toString();
        if (history != null && !history.isEmpty()) {
            for (int i = history.size()-1; i >= 0; i--) {
                HistoryItem hi = history.get(i);
                if ("WorkflowCompleted".equals(hi.getAction())) {
                    logger.userId = hi.getUserId();
                    break;
                }
            }
        }
        String instancePath = StringUtils.substringBeforeLast(StringUtils.substringAfter(workItem.getWorkflow().getId(), "/var/workflow/instances"), "/");
        String logFileName = StringUtils.substringAfterLast(workItem.getWorkflow().getId(), "/") + ".log";
        String path = MessageFormat.format(ROOT_LOG_PATH_PATTERN, siteName) + instancePath;
        String[] nodeNames = StringUtils.split(path, "/");
        path = "";
        for (String nodeName : nodeNames) {
            path += "/" + nodeName;
            if (!logger.jcrSession.nodeExists(path)) {
                JcrUtil.createPath(path, "nt:folder", logger.jcrSession);
            }
        }
        path += "/" + logFileName;

        ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);
        AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);
        if (!logger.jcrSession.nodeExists(path)) {
            byte[] data = new byte[0];
            InputStream is = new ByteArrayInputStream(data);

            final ValueFactory valueFactory = logger.jcrSession.getValueFactory();
            final Binary binary = valueFactory.createBinary(is);

            Asset fileAsset = assetManager.createOrReplaceAsset(path, binary, "text/plain", true);

            //Node fileNode = fileAsset.adaptTo(Node.class);

                    /*JcrUtil.createPath(path, "nt:file", logger.jcrSession);
            logger.jcrNode = fileNode.addNode("jcr:content", "nt:resource");
            logger.jcrNode.setProperty("jcr:mimeType", "text/plain");
            logger.jcrNode.setProperty("jcr:encoding", "utf-8");*/
            resourceResolver.commit();
        } //else {
        Resource r = resourceResolver.getResource(path + "/jcr:content/renditions/original/jcr:content");
        if (r != null)
            logger.jcrNode = r.adaptTo(Node.class);
        //}
        return logger;
    }

    public EFPIALogger(String siteName, Level level) {
        this.siteName = siteName;
        this.level = level;
        this.levelMask = 0;
        int i = Level.values().length;
        do {
            i--;
            this.levelMask += Level.values()[i].value;
        } while (Level.values()[i] != level);
    }

    public void debug(String s, Object... args) {
        this.write(Level.DEBUG, s, args);
    }

    public void info(String s, Object... args) {
        this.write(Level.INFO, s, args);
    }

    public void warn(String s, Object... args) {
        this.write(Level.WARNING, s, args);
    }

    public void error(String s, Object... args) {
        this.write(Level.ERROR, s, args);
    }

    private void write(Level level, String s, Object... args) {
        if (((level.value & this.levelMask) != 0) && (jcrNode != null)) {
            try {
                if (args != null) {
                    for (Object a : args) {
                        s = StringUtils.replaceOnce(s, "{}", a.toString());
                    }
                }
                Value value = null;
                byte[] fileContent = new byte[0];
                try {
                    Property data = jcrNode.getProperty("jcr:data");
                    if (data != null) {
                        value = (Value) data.getValue();
                        InputStream is = value.getBinary().getStream();
                        fileContent = new byte[is.available()];
                        is.read(fileContent);
                    }
                } catch (RepositoryException e) {
                }


                byte[] message = (LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " - " + level.label + " - " + userId + " - " + s + "\n").getBytes(StandardCharsets.UTF_8);
                ByteBuffer buffer = ByteBuffer.wrap(new byte[fileContent.length + message.length]);
                buffer.put(fileContent);
                buffer.put(message);
                fileContent = buffer.array();
                ValueFactory factory = jcrSession.getValueFactory();
                Binary binary = factory.createBinary(new ByteArrayInputStream(fileContent));
                value = factory.createValue(binary);
                jcrNode.setProperty("jcr:data", value);
                binary.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
