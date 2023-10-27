package com.adiacent.menarini.mhos.core.resources;

import com.adiacent.menarini.mhos.core.servlets.ImportLibraryServlet;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
//Help : https://www.youtube.com/watch?v=BtLqsd1MDhY
@ExtendWith({AemContextExtension.class})
class ImportLibraryResourceTest {

    private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);
    ImportLibraryResource configTest;

    @BeforeEach
    void setUp() {
        configTest = aemContext.registerService(new ImportLibraryResource());
        ImportLibraryResource.Config config = mock(ImportLibraryResource.Config.class);
        configTest.activate(config);

    }

    @Test
    void activate() {

    }

    @Test
    void get_instance() {
    }

    @Test
    void getConfig() {
    }
}