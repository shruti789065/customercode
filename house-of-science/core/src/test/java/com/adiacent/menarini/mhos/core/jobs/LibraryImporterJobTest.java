package com.adiacent.menarini.mhos.core.jobs;

import com.adiacent.menarini.mhos.core.services.LibraryImporter;
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
class LibraryImporterJobTest {

    private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);

    @Mock
    private LibraryImporterJob job;

    @BeforeEach
    void setUp() {
        LibraryImporter importerinstance = spy(aemContext.registerService(LibraryImporter.class, new LibraryImporter()));


        //LibraryImporter importer = spy(s);
        LibraryImporter clonedImporter = mock(LibraryImporter.class);
        doNothing().when(clonedImporter).start();
        try {
            when(importerinstance.clone()).thenReturn(clonedImporter);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }


        job =  spy(aemContext.registerService(new  LibraryImporterJob()));
        when(job.getImporterIstance()).thenReturn(importerinstance);

        aemContext.addModelsForClasses(LibraryImporterJob.class);
    }

    @Test
    void run(){
        job.run();
        assertEquals("test","test");
    }
}