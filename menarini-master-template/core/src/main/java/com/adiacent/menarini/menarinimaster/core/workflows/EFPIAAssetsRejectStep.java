package com.adiacent.menarini.menarinimaster.core.workflows;

import com.adobe.granite.asset.api.AssetManager;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
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

import javax.jcr.Session;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component(property = {
        Constants.SERVICE_DESCRIPTION + "=Reject EFPIA assets",
        Constants.SERVICE_VENDOR + "=Adiacent",
        "process.label" + "=Reject EFPIA assets"
})
public class EFPIAAssetsRejectStep implements WorkflowProcess {

    private static final transient Logger log = LoggerFactory.getLogger(EFPIAAssetsRejectStep.class);

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        final WorkflowData workflowData = workItem.getWorkflowData();
        final String path = workflowData.getPayload().toString();
        String siteName = EFPIAUtils.siteNameFromPath(path);
        Integer year = EFPIAUtils.yearFromPath(path);
        EFPIAXLSXLogger logger = null;

        try {
            // + Verify publish path
            logger = EFPIAXLSXLogger.getLogger(siteName, workItem, workflowSession);
            double version = workflowData.getMetaDataMap().get("version", Double.class);
            logger.reject(year, version, workItem.getMetaDataMap().get("comment", String.class));


        } catch (Exception e) {
            log.error("[EFPIAAssetsRejectStep] ", e);
        }
    }
}
