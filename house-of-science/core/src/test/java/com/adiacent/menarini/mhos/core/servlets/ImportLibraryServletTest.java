package com.adiacent.menarini.mhos.core.servlets;

import com.adiacent.menarini.mhos.core.services.LibraryImporter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ImportLibraryServletTest {

    private final AemContext ctx = new AemContext(ResourceResolverType.JCR_MOCK);

    @Mock
    private ImportLibraryServlet importLibraryServlet;

    @BeforeEach
    void setUp() {
        //((MockRequestPathInfo)ctx.request().getRequestPathInfo()).setExtension("json");
        ctx.addModelsForClasses(ImportLibraryServlet.class);
    }

    @Test
    void busyServletTest() {

        importLibraryServlet.running = true;

        ImportLibraryServlet.Response response = ctx.registerService(ImportLibraryServlet.Response.class, importLibraryServlet.new Response());
        importLibraryServlet.ilr = response;

        importLibraryServlet.doGet(ctx.request(),ctx.response());

        assertEquals(ctx.response().getStatus(), 200);

    }


    @Test
    void runServletWithErrors(){

        LibraryImporter importerinstance = ctx.registerService(LibraryImporter.class, new LibraryImporter());
        LibraryImporter spyedImporterinstance = spy(importerinstance);


        LibraryImporter clonedImporterInstance = mock(LibraryImporter.class);
        doNothing().when(clonedImporterInstance).start();
        when(clonedImporterInstance.getErrors()).thenReturn(Arrays.asList("Error1", "Error2"));
        try {
            when(spyedImporterinstance.clone()).thenReturn(clonedImporterInstance);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        importLibraryServlet= spy(ctx.registerService(ImportLibraryServlet.class));
        importLibraryServlet.importerInstance = spyedImporterinstance;
        importLibraryServlet.doGet(ctx.request(),ctx.response());

        assertTrue(ctx.response().getOutputAsString().contains("Error1"));
    }

    @Test
    void runServletWithoutErrors(){

        LibraryImporter importerinstance = ctx.registerService(LibraryImporter.class, new LibraryImporter());
        LibraryImporter spyedImporterinstance = spy(importerinstance);


        LibraryImporter clonedImporterInstance = mock(LibraryImporter.class);
        doNothing().when(clonedImporterInstance).start();
        when(clonedImporterInstance.getErrors()).thenReturn(new ArrayList<String>());
        try {
            when(spyedImporterinstance.clone()).thenReturn(clonedImporterInstance);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        importLibraryServlet= spy(ctx.registerService(ImportLibraryServlet.class));
        importLibraryServlet.importerInstance = spyedImporterinstance;
        importLibraryServlet.doGet(ctx.request(),ctx.response());

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        ImportLibraryServlet.Response res = gson.fromJson(ctx.response().getOutputAsString(),ImportLibraryServlet.Response.class);

        assertTrue(res.getErrors().size() == 0);
    }
}