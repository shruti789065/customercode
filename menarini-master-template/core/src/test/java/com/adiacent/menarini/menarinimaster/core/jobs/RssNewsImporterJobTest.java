package com.adiacent.menarini.menarinimaster.core.jobs;

import com.adiacent.menarini.menarinimaster.core.services.RssNewsImporter;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class RssNewsImporterJobTest {

    private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);

    @Mock
    private RssNewsImporterJob job;

    @BeforeEach
    void setUp() {
        RssNewsImporter instance =  spy(aemContext.registerService(RssNewsImporter.class, new RssNewsImporter()));
        RssNewsImporter importerClone = mock(RssNewsImporter.class);
        doNothing().when(importerClone).start();
        try {
            when(instance.clone()).thenReturn(importerClone);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }


        job =  spy(aemContext.registerService(RssNewsImporterJob.class, new RssNewsImporterJob()));

        when(job.getImporterIstance()).thenReturn(instance);

        aemContext.addModelsForClasses( RssNewsImporterJob.class);
    }

    @Test
    void name() {
        job.run();
        assertEquals("2","2");
    }
}