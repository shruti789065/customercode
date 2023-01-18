package com.adiacent.menarini.menarinimaster.core.servlets;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;

import static org.apache.sling.api.servlets.ServletResolverConstants.*;

@Component(service = { Servlet.class }, property = {
        SLING_SERVLET_RESOURCE_TYPES + "=menarinimaster/components/page",
        SLING_SERVLET_METHODS + "=GET",
        SLING_SERVLET_EXTENSIONS + "=xml",
        SLING_SERVLET_SELECTORS + "=sitemap",
})
public class SitemapServlet extends SlingSafeMethodsServlet {
    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");

    private static final String SITEMAP_NAMESPACE = "http://www.sitemaps.org/schemas/sitemap/0.9";

    private static final String HIDE_IN_NAV_PROPERTY = "hideInNav";

    @Reference
    private Externalizer externalizer;

    @Override
    protected void doGet(SlingHttpServletRequest slingRequest, SlingHttpServletResponse slingResponse)
            throws ServletException, IOException {

        slingResponse.setContentType(slingRequest.getResponseContentType());
        ResourceResolver resourceResolver = slingRequest.getResourceResolver();

        externalizer = externalizer == null ? resourceResolver.adaptTo(Externalizer.class) : externalizer;


        Resource res = slingRequest.getResource();
        // salgo dal content al nodo della pagna e poi vado sul nodo superiore
        Resource par = res.getParent();

        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        Page pageObj = pageManager.getContainingPage(par);

        XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
        try {
            XMLStreamWriter stream = outputFactory.createXMLStreamWriter(slingResponse.getWriter());

            stream.writeStartDocument("1.0");
            stream.writeStartElement("", "urlset", SITEMAP_NAMESPACE);
            stream.writeNamespace("", SITEMAP_NAMESPACE);

            // Current page
            writeXML(pageObj, stream, slingRequest);

            for (Iterator<Page> children = pageObj.listChildren(new PageFilter(), true); children.hasNext();) {
                Page childPage = (Page) children.next();
                // If condition added to make sure the pages hidden in search in page properties
                // do not show up in sitemap
                if (null != childPage) {

                    if (!childPage.getProperties().containsKey(HIDE_IN_NAV_PROPERTY)
                            || (childPage.getProperties().containsKey(HIDE_IN_NAV_PROPERTY)
                            && childPage.getProperties().get(HIDE_IN_NAV_PROPERTY).equals("false"))
                            || (childPage.getProperties().containsKey(HIDE_IN_NAV_PROPERTY)
                            && childPage.getProperties().get(HIDE_IN_NAV_PROPERTY).equals("")))
                        writeXML(childPage, stream, slingRequest);
                }
            }

            stream.writeEndElement();
            stream.writeEndDocument();

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }

    }

    private void writeXML(Page pageObj, XMLStreamWriter xmlStream, SlingHttpServletRequest slingRequest)
            throws XMLStreamException {
        xmlStream.writeStartElement(SITEMAP_NAMESPACE, "url");

        String protocolPort = "http";
        if (slingRequest.isSecure())
            protocolPort = "https";

        String locPath = this.externalizer.absoluteLink(slingRequest, protocolPort,
                String.format("%s.html", pageObj.getPath()));

        writeXMLElement(xmlStream, "loc", locPath);
        Calendar calendarObj = pageObj.getLastModified();
        if (null != calendarObj) {
            writeXMLElement(xmlStream, "lastmod", DATE_FORMAT.format(calendarObj));
        }
        xmlStream.writeEndElement();
    }

    private void writeXMLElement(final XMLStreamWriter xmlStream, final String elementName, final String xmlText)
            throws XMLStreamException {
        xmlStream.writeStartElement(SITEMAP_NAMESPACE, elementName);
        xmlStream.writeCharacters(xmlText);
        xmlStream.writeEndElement();
    }

}
