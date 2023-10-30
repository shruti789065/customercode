package com.adiacent.menarini.menarinimaster.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.Arrays;
import java.util.List;

@Component(property = {
        Constants.SERVICE_DESCRIPTION + "=Validate EFPIA assets",
        Constants.SERVICE_VENDOR + "=Adiacent",
        "process.label" + "=Validation process of EFPIA assets"
})
public class EFPIAValidationStep implements WorkflowProcess {

    private static final transient Logger log = LoggerFactory.getLogger(EFPIAValidationStep.class);

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArguments) throws WorkflowException {
        final WorkflowData workflowData = workItem.getWorkflowData();
        final String path = workflowData.getPayload().toString();
        EFPIALogger logger = null;
        try(ResourceResolver resourceResolver =  workflowSession.adaptTo(ResourceResolver.class))
        {
            String siteName;
            if (EFPIAUtils.isPathValid(path)) {
                siteName = EFPIAUtils.siteNameFromPath(path);
            } else {
                log.error("Invalid path: {}", path);
                return;
            }
            logger = EFPIALogger.getLogger(siteName, EFPIALogger.Level.INFO, workItem, workflowSession);
            workflowData.getMetaDataMap().put("isValid", false);
            Resource resource = resourceResolver.getResource(path);
            //workItem.getMetaDataMap().put("comment", "<h4>Workflow comment</h4>");
            int assetsToApprove = 0;
            if (resource != null && resource.hasChildren()) {
                for (Resource asset : resource.getChildren()) {
                    logger.debug("Type: {} - Name: {} - Path: {}", asset.getResourceType(), asset.getName(), asset.getPath());
                    try {
                      EFPIAUtils.validateResource(asset);
                      logger.info("Asset {} is valid", asset.getPath());
                      assetsToApprove++;
                    } catch (EFPIAUtils.EFPIABlockingValidationException e) {
                        logger.error(e.getMessage());
                        return;
                    } catch (EFPIAUtils.EFPIANonBlockingValidationException e) {
                        logger.debug(e.getMessage());
                    }

                    /*
                    if (EFPIAUtils.isResourceTypeValid(asset.getResourceType())) {
                        String fileName = StringUtils.substringBeforeLast(asset.getName(), ".");
                        String fileExt = StringUtils.lowerCase(StringUtils.substringAfterLast(asset.getName(), "."));
                        if (EFPIAUtils.isFileTypeValid(fileExt)) {
                            if (EFPIAUtils.isFileNameValid(fileName)) {
                                logger.info("Asset {} is valid", asset.getPath());
                                assetsToApprove++;
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

                logger.info("Found {} assets to submit for approval", assetsToApprove);
                if (assetsToApprove > 0)
                    workflowData.getMetaDataMap().put("isValid", true);
            }

        }
        catch(Exception e)
        {
            if (logger != null)
                logger.error("[EFPIAValidationStep] ", e);
            else
                log.error("[EFPIAValidationStep] ", e);
        }
    }
}
