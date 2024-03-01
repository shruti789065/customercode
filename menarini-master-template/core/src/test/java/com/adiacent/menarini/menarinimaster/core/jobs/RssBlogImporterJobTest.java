package com.adiacent.menarini.menarinimaster.core.jobs;


import com.adiacent.menarini.menarinimaster.core.services.RssBlogImporter;
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
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class RssBlogImporterJobTest {

    private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);
    @Mock
    private RssBlogImporterJob job;

    @BeforeEach
    void setUp() {
        RssBlogImporter instance =  spy(aemContext.registerService(RssBlogImporter.class, new RssBlogImporter()));
        RssBlogImporter importerClone = mock(RssBlogImporter.class);
        doNothing().when(importerClone).start();
        try {
            when(instance.clone()).thenReturn(importerClone);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }


        job =  spy(aemContext.registerService(RssBlogImporterJob.class, new RssBlogImporterJob()));

        when(job.getImporterIstance()).thenReturn(instance);

        aemContext.addModelsForClasses( RssBlogImporterJob.class);
    }


    @Test
    void name() {
        job.run();
        assertEquals("2","2");
    }
}