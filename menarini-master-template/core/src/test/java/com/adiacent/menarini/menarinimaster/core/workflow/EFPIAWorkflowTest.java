package com.adiacent.menarini.menarinimaster.core.workflow;

import com.adiacent.menarini.menarinimaster.core.workflows.EFPIAAssetsMoveStep;
import com.adiacent.menarini.menarinimaster.core.workflows.EFPIAAssetsRejectStep;
import com.adiacent.menarini.menarinimaster.core.workflows.EFPIAValidationStep;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adobe.granite.workflow.metadata.SimpleMetaDataMap;
import com.day.cq.dam.api.AssetManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.Session;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class EFPIAWorkflowTest {
    private final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

    private final Session session = context.resourceResolver().adaptTo(Session.class);

    @Mock
    private AssetManager assetManager;

    @Mock
    private WorkflowSession workflowSession;

    @Mock
    private Workflow workflow;

    @Mock
    private WorkflowData workflowData;

    @Mock
    private WorkItem workItem;

    private final EFPIAValidationStep efpiaValidationStep = new EFPIAValidationStep();

    private final EFPIAAssetsMoveStep efpiaAssetsMoveStep = new EFPIAAssetsMoveStep();

    private final EFPIAAssetsRejectStep efpiaAssetsRejectionStep = new EFPIAAssetsRejectStep();

    private final MetaDataMap metaData = new SimpleMetaDataMap();

    private final MetaDataMap dataMetaData = new SimpleMetaDataMap();

    private final String PROPERTY = "myworkflow";

    private final String ARG="test";

    private final String NOARG="no arguments";


    @BeforeEach
    void setup() {
        ResourceResolver rr = spy(context.resourceResolver());
        lenient().doNothing().when(rr).close();

        Session s = spy(rr.adaptTo(Session.class));

        lenient().when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(rr);
        lenient().when(workflowSession.adaptTo(Session.class)).thenReturn(s);
        lenient().when(workflow.getMetaDataMap()).thenReturn(metaData);
        lenient().when(workflowData.getMetaDataMap()).thenReturn(dataMetaData);
        lenient().when(workflowData.getPayloadType()).thenReturn("JCR_PATH");
        lenient().when(workflowData.getPayload()).thenReturn("/content/dam/menarini-ch/efpia/2022");
        lenient().when(workItem.getWorkflow()).thenReturn(workflow);
        lenient().when(workItem.getWorkflowData()).thenReturn(workflowData);
        lenient().when(rr.adaptTo(AssetManager.class)).thenReturn(assetManager);

        assertNotNull(session);
    /*
        context.registerAdapter(WorkflowSession.class, Session.class, session);
*/
        /*
        context.registerAdapter(ResourceResolver.class, AssetManager.class, new Function<ResourceResolver, AssetManager>() {
            @Nullable
            @Override
            public AssetManager apply(@Nullable ResourceResolver input) {
                return assetManager;
            }
        });
*/      try {
            context.create().resource("/content/dam/menarini-ch/efpia/2022", "jcr:primaryType", "nt:folder");
            context.create().asset("/content/dam/menarini-ch/efpia/2022/001.jpg", "/com/adiacent/menarini/menarinimaster/core/models/001.jpg", "image/jpg");
            context.create().asset("/content/dam/menarini-ch/efpia/2022/002.jpg", "/com/adiacent/menarini/menarinimaster/core/models/002.jpg", "image/jpg");
            context.create().asset("/content/dam/efpia/menarini-ch/log/EFPIA_ReportHeaders.xlsx", "/com/adiacent/menarini/menarinimaster/core/models/RH.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Double version;

    @Test
    public void testEFPIAValidationStep() throws Exception {
        efpiaValidationStep.execute(workItem, workflowSession, metaData);
        assertTrue((Boolean) workItem.getWorkflowData().getMetaDataMap().get("isValid"));
        this.version = workItem.getWorkflowData().getMetaDataMap().get("version", Double.class);
    }

    @Test
    public void testEFPIAAssetsMoveStep() throws Exception {
        workItem.getWorkflowData().getMetaDataMap().put("version", this.version);
        efpiaAssetsMoveStep.execute(workItem, workflowSession, metaData);
    }

    @Test
    public void testEFPIAAssetsRejectionStep() throws Exception {
        workItem.getWorkflowData().getMetaDataMap().put("version", this.version);
        efpiaAssetsRejectionStep.execute(workItem, workflowSession, metaData);
    }
}
