package com.adiacent.menarini.menarinimaster.core.models;

import com.adiacent.menarini.menarinimaster.core.servlets.MailServlet;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.models.factory.ModelFactory;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;

@ExtendWith({AemContextExtension.class})
public class MessageViewerModelTest {
    private final AemContext ctx = new AemContextBuilder(ResourceResolverType.JCR_MOCK).build();
    @Mock
    MessageViewerModel model;

    @BeforeEach
    void setUp() {

        try {

            MockSlingHttpServletRequest request = spy(new MockSlingHttpServletRequest(ctx.resourceResolver(),ctx.bundleContext()));
            ResourceResolver resolver = spy(ctx.resourceResolver());


            lenient().when(request.getResourceResolver()).thenReturn(resolver);

            List<String> errors = Arrays.asList(new String[]{"error1", "error2"});
            request.getSession().setAttribute(MailServlet.ERROR_MESSAGE_ATTRIBUTE_NAME,errors);


            SlingBindings bindings = new SlingBindings();
            bindings.put(SlingBindings.REQUEST, request);
            bindings.put("sessionattrname",MailServlet.ERROR_MESSAGE_ATTRIBUTE_NAME);
            request.setAttribute(SlingBindings.class.getName(), bindings);



            ModelFactory modelFactory = ctx.getService(ModelFactory.class);
            model = modelFactory.createModel(request, MessageViewerModel.class);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    @Order(1)
    void testSessionAttribute(){
        assertNotNull(model.getMessageList());
    }

}
