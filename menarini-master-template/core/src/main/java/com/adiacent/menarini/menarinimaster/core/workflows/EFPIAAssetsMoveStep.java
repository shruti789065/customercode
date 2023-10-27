package com.adiacent.menarini.menarinimaster.core.workflows;

import com.adobe.granite.asset.api.AssetManager;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.Participant;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.replication.ReplicationStatusProvider;
import com.day.cq.replication.Replicator;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component(property = {
        Constants.SERVICE_DESCRIPTION + "=Move EFPIA assets to publish folder",
        Constants.SERVICE_VENDOR + "=Adiacent",
        "process.label" + "=Move EFPIA Assets to publish folder"
})
public class EFPIAAssetsMoveStep implements WorkflowProcess {

    @Reference
    Replicator replicator;
    @Reference
    ReplicationStatusProvider replicationStatusProvider;

    private static final transient Logger log = LoggerFactory.getLogger(EFPIAAssetsMoveStep.class);

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        final WorkflowData workflowData = workItem.getWorkflowData();
        final String path = workflowData.getPayload().toString();
        String siteName = EFPIAUtils.siteNameFromPath(path);
        EFPIALogger logger = null;

        ResourceResolver resourceResolver =  workflowSession.adaptTo(ResourceResolver.class);
        Session jcrSession =  workflowSession.adaptTo(Session.class);
        AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);

        String[] pathArr = StringUtils.split(path, "/");
        String year = pathArr[pathArr.length-1];

        String PUBLISH_DIR_PATH = EFPIAUtils.getPublishPath(siteName, year);
        try {
            // + Verify publish path
            logger = EFPIALogger.getLogger(siteName, EFPIALogger.Level.DEBUG, workItem, workflowSession);
            Resource publishDir = resourceResolver.getResource(PUBLISH_DIR_PATH);
            if (publishDir == null) {
                String[] nodeNames = StringUtils.split(PUBLISH_DIR_PATH, "/");
                String publishPath = "";
                for (String nodeName : nodeNames) {
                    publishPath += "/" + nodeName;
                    if (!jcrSession.nodeExists(publishPath)) {
                        JcrUtil.createPath(publishPath, "nt:folder", jcrSession);
                    }
                }
                publishDir = resourceResolver.getResource(PUBLISH_DIR_PATH);
            }
            // - Verify publish path

            // + Move assets in backup folder
            if (publishDir.getChildren() != null) {
                String backupFolderName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
                for (Resource r : publishDir.getChildren()) {
                    try {
                        EFPIAUtils.validateResource(r);
                        if (!jcrSession.nodeExists(PUBLISH_DIR_PATH + "/" + backupFolderName)) {
                            JcrUtil.createPath(PUBLISH_DIR_PATH + "/" + backupFolderName, "nt:folder", jcrSession);
                        }
                        assetManager.moveAsset(r.getPath(), PUBLISH_DIR_PATH + "/" + backupFolderName + "/" + r.getName());
                        logger.info("Asset {} backed up into {}", r.getPath(), PUBLISH_DIR_PATH + "/" + backupFolderName + "/" + r.getName());
                    } catch (EFPIAUtils.EFPIABlockingValidationException e) {
                       logger.warn("Asset {} not backed up: {}", r.getPath(), e.getMessage());
                    } catch (EFPIAUtils.EFPIANonBlockingValidationException e) {
                        logger.debug("Asset {} not backed up: {}", r.getPath(), e.getMessage());
                    }
                    /*
                    if (EFPIAUtils.isResourceTypeValid(r.getResourceType())) {
                        String fileName = StringUtils.substringBeforeLast(r.getName(), ".");
                        String fileExt = StringUtils.lowerCase(StringUtils.substringAfterLast(r.getName(), "."));
                        if (EFPIAUtils.isFileTypeValid(fileExt)) {
                            if (EFPIAUtils.isFileNameValid(fileName)) {
                                if (!jcrSession.nodeExists(PUBLISH_DIR_PATH + "/" + backupFolderName)) {
                                    JcrUtil.createPath(PUBLISH_DIR_PATH + "/" + backupFolderName, "nt:folder", jcrSession);
                                }
                                assetManager.moveAsset(r.getPath(), PUBLISH_DIR_PATH + "/" + backupFolderName + "/" + r.getName());
                                logger.info("Asset {} backed up into {}", r.getPath(), PUBLISH_DIR_PATH + "/" + backupFolderName + "/" + r.getName());
                            }
                        }
                    }
                     */
                }
            }
            // - Move assets in backup folder

            Resource resource = resourceResolver.getResource(path);
            //workItem.getMetaDataMap().put("comment", "<h4>Workflow comment</h4>");
            int assetsMoved = 0;
            if (resource.hasChildren()) {
                for (Resource asset : resource.getChildren()) {
                    logger.debug("Type: {} - Name: {} - Path: {}", asset.getResourceType(), asset.getName(), asset.getPath());
                    try {
                        String publishingPath = PUBLISH_DIR_PATH + "/" + asset.getName();
                        EFPIAUtils.validateResource(asset);
                        logger.info("Asset {} is valid", asset.getPath());
                        assetManager.moveAsset(asset.getPath(), publishingPath);
                        logger.info("Asset {} moved from DRAFT to PUBLISH path: {}", asset.getPath(), publishingPath);
                        assetsMoved++;
                        replicator.replicate(jcrSession, ReplicationActionType.ACTIVATE, publishingPath);
                        logger.info("Publication of {} started", publishingPath);
                        Resource res = resourceResolver.getResource(publishingPath);
                        ReplicationStatus resStatus = res.adaptTo(ReplicationStatus.class);
                        logger.info("Replication status for resource {} is: {}", publishingPath, resStatus);
                    } catch (EFPIAUtils.EFPIABlockingValidationException e) {
                        logger.warn("Asset {} not moved: {}", asset.getPath(), e.getMessage());
                    } catch (EFPIAUtils.EFPIANonBlockingValidationException e) {
                        logger.debug("Asset {} not moved: {}", asset.getPath(), e.getMessage());
                    }
                    /*
                    if (EFPIAUtils.isResourceTypeValid(asset.getResourceType())) {
                        String fileName = StringUtils.substringBeforeLast(asset.getName(), ".");
                        String fileExt = StringUtils.lowerCase(StringUtils.substringAfterLast(asset.getName(), "."));
                        if (EFPIAUtils.isFileTypeValid(fileExt)) {
                            if (EFPIAUtils.isFileNameValid(fileName)) {
                                logger.info("Asset {} is valid", asset.getPath());
                                assetManager.moveAsset(asset.getPath(), PUBLISH_DIR_PATH + "/" + asset.getName());
                                logger.info("Asset {} moved from DRAFT to PUBLISH path: {}", asset.getPath(), PUBLISH_DIR_PATH + "/" + asset.getName());
                                assetsMoved++;
                            } else {
                                logger.error("File name '{}' not acceptable", fileName);
                                return;
                            }
                        } else {
                            logger.error("File extension {} is not allowed. Asset {} rejected", fileExt, asset.getName());
                            return;
                        }
                    } else {
                        logger.info("Resource type {} for resource {} is not acceptable. Skipped.", asset.getResourceType(), asset.getName());
                    }
                     */
                }
                logger.info("{} assets moved", assetsMoved);

            }

        } catch (Exception e) {
            if (logger != null)
                logger.error("[EFPIAAssetsMoveStep] ", e);
            else
                log.error("[EFPIAAssetsMoveStep] ", e);
        }
    }
}
