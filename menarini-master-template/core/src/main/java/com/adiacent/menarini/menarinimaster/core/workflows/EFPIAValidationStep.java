package com.adiacent.menarini.menarinimaster.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(property = {
        Constants.SERVICE_DESCRIPTION + "=EFPIA: Assets validation",
        Constants.SERVICE_VENDOR + "=Adiacent",
        "process.label" + "=Validation process of EFPIA assets"
})
public class EFPIAValidationStep implements WorkflowProcess {

    private static final transient Logger log = LoggerFactory.getLogger(EFPIAValidationStep.class);

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArguments) throws WorkflowException {
        final WorkflowData workflowData = workItem.getWorkflowData();
        final String path = workflowData.getPayload().toString();
        EFPIAXLSXLogger logger = null;
        try(ResourceResolver resourceResolver =  workflowSession.adaptTo(ResourceResolver.class))
        {
            String siteName;
            String reportName;
            Integer year;
            if (EFPIAUtils.isPathValid(path)) {
                siteName = EFPIAUtils.siteNameFromPath(path);
                reportName = EFPIAUtils.reportNameFromPath(path);
                year = EFPIAUtils.yearFromPath(path);
            } else {
                log.error("Invalid path: {}", path);
                return;
            }
            logger = EFPIAXLSXLogger.getLogger(siteName,reportName, workItem, workflowSession);
            workflowData.getMetaDataMap().put("isValid", false);
            Resource resource = resourceResolver.getResource(path);
            //workItem.getMetaDataMap().put("comment", "<h4>Workflow comment</h4>");
            int assetsToApprove = 0;
            if (resource != null && resource.hasChildren()) {
                for (Resource asset : resource.getChildren()) {
                    log.debug("Type: {} - Name: {} - Path: {}", asset.getResourceType(), asset.getName(), asset.getPath());
                    try {
                      EFPIAUtils.validateResource(asset);
                      log.info("Asset {} is valid", asset.getPath());
                      assetsToApprove++;
                    } catch (EFPIAUtils.EFPIABlockingValidationException e) {
                        log.error("[EFPIAValidationStep]", e);
                        return;
                    } catch (EFPIAUtils.EFPIANonBlockingValidationException e) {
                        log.debug(e.getMessage());
                    }
                }

                log.info("Found {} assets to submit for approval", assetsToApprove);
                if (assetsToApprove > 0) {
                    workflowData.getMetaDataMap().put("isValid", true);
                    double version = logger.submitForApproval(year, null);
                    workflowData.getMetaDataMap().put("version", version);
                }
            }

        }
        catch(Exception e)
        {
            log.error("[EFPIAValidationStep] ", e);
        }
    }
}
