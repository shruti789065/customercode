package com.adiacent.menarini.mhos.core.models;

import com.adiacent.menarini.mhos.core.servlets.SitemapServlet;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.servlethelpers.MockRequestPathInfo;
import org.apache.sling.settings.SlingSettingsService;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@ExtendWith(AemContextExtension.class)
public class RunModeModelTest {
    private final AemContext ctx = new AemContext(ResourceResolverType.JCR_MOCK);

    @Mock
    private RunModeModel runModeModel;

    @BeforeEach
    public void setUp()  {
        runModeModel = new RunModeModel();

        SlingSettingsService service = mock(SlingSettingsService.class);
        service.getRunModes().add("publish");
        try {
            runModeModel.init();
        }catch (Exception e) {

        }
    }
    @Test
    void testGetters(){
        assertNotNull(runModeModel.getPublishMode());
    }
}
