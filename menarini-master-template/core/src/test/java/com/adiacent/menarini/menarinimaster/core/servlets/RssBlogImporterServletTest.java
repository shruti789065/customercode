package com.adiacent.menarini.menarinimaster.core.servlets;

import com.adiacent.menarini.menarinimaster.core.services.RssBlogImporter;
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
import static org.mockito.Mockito.doNothing;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class RssBlogImporterServletTest {
    private final AemContext ctx = new AemContext(ResourceResolverType.JCR_MOCK);

    private RssBlogImporterServlet servlet;

    @BeforeEach
    void setUp() {
        RssBlogImporterServlet svl = ctx.registerService(RssBlogImporterServlet.class , new RssBlogImporterServlet() );
        servlet = spy(svl);
        ctx.addModelsForClasses(RssBlogImporterServlet.class );
    }

    @Test
    void successServlet() {

        RssBlogImporter importer =  ctx.registerService(RssBlogImporter.class , new RssBlogImporter() );
        RssBlogImporter spyImporter = spy(importer);

        RssBlogImporter mockImporter = mock(RssBlogImporter.class);

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
        RssBlogImporterServlet.Response res = gson.fromJson(ctx.response().getOutputAsString(),RssBlogImporterServlet.Response.class);

        assertTrue(res.getErrors().size() == 0);
    }

    @Test
    void runningServlet() {

        servlet.running = true;

        RssBlogImporterServlet.Response response = ctx.registerService(RssBlogImporterServlet.Response.class, servlet.new Response());

        servlet.ilr = response;
        servlet.doGet(ctx.request(),ctx.response());
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        RssBlogImporterServlet.Response res = gson.fromJson(ctx.response().getOutputAsString(),RssBlogImporterServlet.Response.class);

        assertTrue("running".equals(res.getResult()));
    }

    @Test
    void withErrorServlet() {

        RssBlogImporter importerInstance = ctx.registerService(RssBlogImporter.class, new RssBlogImporter());
        RssBlogImporter spyImporter = spy(importerInstance);
        servlet.importerInstance = spyImporter;

        try {
            RssBlogImporter mockImporterInstance = mock(RssBlogImporter.class);

            when(spyImporter.clone()).thenReturn(mockImporterInstance);
            doNothing().when(mockImporterInstance).start();

            List<String> errors = new ArrayList<>();
            errors.add("error_generic");
            when(mockImporterInstance.getErrors()).thenReturn(errors);


        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        RssBlogImporterServlet.Response response = ctx.registerService(RssBlogImporterServlet.Response.class, servlet.new Response());

        servlet.ilr = response;

        servlet.doGet(ctx.request(),ctx.response());
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        RssBlogImporterServlet.Response res = gson.fromJson(ctx.response().getOutputAsString(),RssBlogImporterServlet.Response.class);

        assertTrue("KO".equals(res.getResult()));
    }

}