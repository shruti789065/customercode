package com.adiacent.menarini.menarinimaster.core.servlets;

import com.adiacent.menarini.menarinimaster.core.services.RssNewsImporter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class RssNewsImporterServletTest {

    private final AemContext ctx = new AemContext(ResourceResolverType.JCR_MOCK);

    private RssNewsImporterServlet servlet;

    @BeforeEach
    void setUp() {
        RssNewsImporterServlet svl = ctx.registerService(RssNewsImporterServlet.class , new RssNewsImporterServlet() );
        servlet = spy(svl);
        ctx.addModelsForClasses(RssNewsImporterServlet.class );
    }

    @Test
    void successServlet() {

        RssNewsImporter importer =  ctx.registerService(RssNewsImporter.class , new RssNewsImporter() );
        RssNewsImporter spyImporter = spy(importer);

        RssNewsImporter mockImporter = mock(RssNewsImporter.class);

        try {
           when(spyImporter.clone()).thenReturn(mockImporter);
           doNothing().when(mockImporter).start();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        servlet.importerInstance = spyImporter;
        servlet.doGet(ctx.request(),ctx.response());
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        RssNewsImporterServlet.Response res = gson.fromJson(ctx.response().getOutputAsString(),RssNewsImporterServlet.Response.class);

        assertTrue(res.getErrors().size() == 0);
    }

    @Test
    void runningServlet() {

        servlet.running = true;

        RssNewsImporterServlet.Response response = ctx.registerService(RssNewsImporterServlet.Response.class, servlet.new Response());

        servlet.ilr = response;
        servlet.doGet(ctx.request(),ctx.response());
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        RssNewsImporterServlet.Response res = gson.fromJson(ctx.response().getOutputAsString(),RssNewsImporterServlet.Response.class);

        assertTrue("running".equals(res.getResult()));
    }

    @Test
    void withErrorServlet() {

        RssNewsImporter importerInstance = ctx.registerService(RssNewsImporter.class, new RssNewsImporter());
        RssNewsImporter spyImporter = spy(importerInstance);
        servlet.importerInstance = spyImporter;

        try {
            RssNewsImporter mockImporterInstance = mock(RssNewsImporter.class);

            when(spyImporter.clone()).thenReturn(mockImporterInstance);
            doNothing().when(mockImporterInstance).start();

            List<String> errors = new ArrayList<>();
            errors.add("error_generic");
            when(mockImporterInstance.getErrors()).thenReturn(errors);


        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        RssNewsImporterServlet.Response response = ctx.registerService(RssNewsImporterServlet.Response.class, servlet.new Response());

        servlet.ilr = response;

        servlet.doGet(ctx.request(),ctx.response());
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        RssNewsImporterServlet.Response res = gson.fromJson(ctx.response().getOutputAsString(),RssNewsImporterServlet.Response.class);

        assertTrue("KO".equals(res.getResult()));
    }

}