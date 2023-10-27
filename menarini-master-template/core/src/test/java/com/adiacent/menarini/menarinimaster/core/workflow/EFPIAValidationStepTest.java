package com.adiacent.menarini.menarinimaster.core.workflow;

import com.adiacent.menarini.menarinimaster.core.workflows.EFPIAValidationStep;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adobe.granite.workflow.metadata.SimpleMetaDataMap;
import com.day.cq.dam.api.AssetManager;
import com.drew.lang.annotations.Nullable;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Session;

import java.util.function.Function;

import static junitx.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class EFPIAValidationStepTest {
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

    private final EFPIAValidationStep myWorkflow = new EFPIAValidationStep();

    private final MetaDataMap metaData = new SimpleMetaDataMap();

    private final MetaDataMap dataMetaData = new SimpleMetaDataMap();

    private final String PROPERTY = "myworkflow";

    private final String ARG="test";

    private final String NOARG="no arguments";

    @BeforeEach
    void setup() {
        lenient().when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(context.resourceResolver());
        lenient().when(workflowSession.adaptTo(Session.class)).thenReturn(session);
        lenient().when(workflow.getMetaDataMap()).thenReturn(metaData);
        lenient().when(workflowData.getMetaDataMap()).thenReturn(dataMetaData);
        lenient().when(workflowData.getPayloadType()).thenReturn("JCR_PATH");
        lenient().when(workflowData.getPayload()).thenReturn("/content/dam/menarini-ch/efpia/2022");
        lenient().when(workItem.getWorkflow()).thenReturn(workflow);
        lenient().when(workItem.getWorkflowData()).thenReturn(workflowData);

        assertNotNull(session);

        context.registerAdapter(WorkflowSession.class, Session.class, session);
        context.registerAdapter(ResourceResolver.class, AssetManager.class, new Function<ResourceResolver, AssetManager>() {
            @Nullable
            @Override
            public AssetManager apply(@Nullable ResourceResolver input) {
                return assetManager;
            }
        });
        //context.load().json(EFPIAValidationStepTest.class.getResourceAsStream("/com/adiacent/menarini/menarinimaster/core/models/EFPIATempFolder.json"), "/content/dam/menarini-ch/efpia");
        context.create().asset("/content/dam/menarini-ch/efpia/2022/001.jpg", "/com/adiacent/menarini/menarinimaster/core/models/001.jpg", "image/jpg");
        context.create().asset("/content/dam/menarini-ch/efpia/2022/002.jpg", "/com/adiacent/menarini/menarinimaster/core/models/002.jpg", "image/jpg");
    }

/*
    @Test
    public void myWorkflowWithArgs() throws Exception {
        metaData.put("PROCESS_ARGS", ARG);

        myWorkflow.execute(workItem, workflowSession, metaData);

        String arg = metaData.get("PROCESS_ARGS", String.class);
        assertNotNull(arg);
        assertEquals(ARG,arg);

        assertNotNull(session);
        Node node = session.getNode("/" + "content" + "/" + "page" + "/" + "jcr:content");
        assertNotNull(node);

        Property property = node.getProperty(PROPERTY);
        assertNotNull(property);

        String value = property.getValue().getString();
        assertNotNull(value);
        assertEquals(ARG,value);
    }
*/
    //@Test
    public void myWorkflowWithoutArgs() throws Exception {
        myWorkflow.execute(workItem, workflowSession, metaData);

        /*
        assertNotNull(session);
        Node node = session.getNode("/" + "content" + "/" + "page" + "/" + "jcr:content");
        assertNotNull(node);

        Property property = node.getProperty(PROPERTY);
        assertNotNull(property);

        String value = property.getValue().getString();
        assertNotNull(value);
        assertEquals(NOARG,value);

         */
        assertTrue((Boolean) workItem.getWorkflowData().getMetaDataMap().get("isValid"));
    }

    //@Test
    public void wrongFileNamePattern() throws Exception {
        context.create().asset("/content/dam/menarini-ch/efpia/2022/002.jpg", "/com/adiacent/menarini/menarinimaster/core/models/ERR002.jpg", "image/jpg");
    }
}
