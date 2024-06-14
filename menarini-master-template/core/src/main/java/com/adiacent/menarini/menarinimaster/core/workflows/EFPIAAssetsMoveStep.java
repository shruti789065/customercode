package com.adiacent.menarini.menarinimaster.core.workflows;

import com.adobe.granite.asset.api.AssetManager;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.replication.ReplicationStatusProvider;
import com.day.cq.replication.Replicator;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component(property = {
        Constants.SERVICE_DESCRIPTION + "=EFPIA: Assets move to publish folder",
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
        String reportName = EFPIAUtils.reportNameFromPath(path);
        EFPIAXLSXLogger logger = null;

        ResourceResolver resourceResolver =  workflowSession.adaptTo(ResourceResolver.class);
        Session jcrSession =  workflowSession.adaptTo(Session.class);
        AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);


        Integer year = EFPIAUtils.yearFromPath(path);

        String PUBLISH_DIR_PATH = EFPIAUtils.getPublishPath(siteName,reportName, year);
        try {
            // + Verify publish path
            logger = EFPIAXLSXLogger.getLogger(siteName,reportName, workItem, workflowSession);
            Resource publishDir = resourceResolver.getResource(PUBLISH_DIR_PATH);
            if (publishDir == null) {
                JcrUtil.createPath(PUBLISH_DIR_PATH, JcrConstants.NT_FOLDER, jcrSession);
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
                            JcrUtil.createPath(PUBLISH_DIR_PATH + "/" + backupFolderName, JcrConstants.NT_FOLDER, jcrSession);
                        }
                        if (assetManager != null)
                            assetManager.moveAsset(r.getPath(), PUBLISH_DIR_PATH + "/" + backupFolderName + "/" + r.getName());
                        log.info("Asset {} backed up into {}", r.getPath(), PUBLISH_DIR_PATH + "/" + backupFolderName + "/" + r.getName());
                    } catch (EFPIAUtils.EFPIABlockingValidationException e) {
                       log.warn("Asset {} not backed up: {}", r.getPath(), e.getMessage());
                    } catch (EFPIAUtils.EFPIANonBlockingValidationException e) {
                        log.debug("Asset {} not backed up: {}", r.getPath(), e.getMessage());
                    }
                }
            }
            // - Move assets in backup folder

            Resource resource = resourceResolver.getResource(path);
            //workItem.getMetaDataMap().put("comment", "<h4>Workflow comment</h4>");
            int assetsMoved = 0;
            if (resource.hasChildren()) {
                for (Resource asset : resource.getChildren()) {
                    log.debug("Type: {} - Name: {} - Path: {}", asset.getResourceType(), asset.getName(), asset.getPath());
                    try {
                        String publishingPath = PUBLISH_DIR_PATH + "/" + asset.getName();
                        EFPIAUtils.validateResource(asset);
                        log.info("Asset {} is valid", asset.getPath());
                        if (assetManager != null)
                            assetManager.moveAsset(asset.getPath(), publishingPath);
                        log.info("Asset {} moved from DRAFT to PUBLISH path: {}", asset.getPath(), publishingPath);
                        assetsMoved++;
                        if (replicator != null)
                            replicator.replicate(jcrSession, ReplicationActionType.ACTIVATE, publishingPath);
                        log.info("Publication of {} started", publishingPath);
                        Resource res = resourceResolver.getResource(publishingPath);
                        ReplicationStatus resStatus = res.adaptTo(ReplicationStatus.class);
                        log.info("Replication status for resource {} is: {}", publishingPath, resStatus);
                    } catch (EFPIAUtils.EFPIABlockingValidationException e) {
                        log.warn("Asset {} not moved: {}", asset.getPath(), e.getMessage());
                    } catch (EFPIAUtils.EFPIANonBlockingValidationException e) {
                        log.debug("Asset {} not moved: {}", asset.getPath(), e.getMessage());
                    }
                }
                log.info("{} assets moved", assetsMoved);
                double version = workflowData.getMetaDataMap().get("version", Double.class);
                logger.approve(year, version, workItem.getMetaDataMap().get("comment", String.class));
            }

        } catch (Exception e) {
            log.error("[EFPIAAssetsMoveStep] ", e);
        }
    }
}
