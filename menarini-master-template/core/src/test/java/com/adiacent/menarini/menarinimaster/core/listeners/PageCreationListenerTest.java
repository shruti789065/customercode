package com.adiacent.menarini.menarinimaster.core.listeners;

import com.adiacent.menarini.menarinimaster.core.models.InternalMenuModelTest;
import com.day.cq.wcm.api.PageEvent;
import com.day.cq.wcm.api.PageModification;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.resourceresolver.MockResourceResolverFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventProperties;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class PageCreationListenerTest {

    private  PageCreationListener listener = null;

    private final AemContext ctx = new AemContext(ResourceResolverType.JCR_MOCK);

    @Test
    void handleEvent() {

        InputStream is = InternalMenuModelTest.class.getResourceAsStream("internalMenuPage.json");
        ctx.load().json(is, "/content/menarinimaster/language-masters/en");

        ctx.registerService(ResourceResolverFactory.class, new MockResourceResolverFactory());
        listener = ctx.registerInjectActivateService(new PageCreationListener());
        HashMap map  = new HashMap();
        map.put("path","/content/menarinimaster/language-masters/en/about-us/the-group");
        map.put("modifiedDate", Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        map.put("type", PageModification.ModificationType.CREATED.toString());
        map.put("userId","admin");


        List pList = new ArrayList();
        pList.add(map);
        HashMap modificationMap  = new HashMap();
        modificationMap.put("modifications", pList);
        EventProperties property  = new EventProperties(modificationMap);
        Event event = new Event(PageEvent.EVENT_TOPIC, property);
        listener.handleEvent(event);
    }


}