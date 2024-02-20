package com.adiacent.menarini.menarinimaster.core.models.rss;

import com.adiacent.menarini.menarinimaster.core.models.EnclosureModel;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.adobe.cq.wcm.core.components.testing.mock.ContextPlugins.CORE_COMPONENTS;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({AemContextExtension.class})
class ChannelModelTest {

    private final AemContext ctx = new AemContextBuilder(ResourceResolverType.JCR_MOCK).plugin(CORE_COMPONENTS).build();

    @Test
    public void testGetTitle() {
        ChannelModel model = new ChannelModel();
        model.setTitle("test");
        assertTrue(model.getTitle().equals("test"));
    }
    @Test
    public void testGetDescription() {
        ChannelModel model = new ChannelModel();
        model.setDescription("test");
        assertTrue(model.getDescription().equals("test"));
    }

    @Test
    public void testGetLink() {
        ChannelModel model = new ChannelModel();
        model.setLink("test");
        assertTrue(model.getLink().equals("test"));
    }

    @Test
    public void testGetItem() {
        ChannelModel model = new ChannelModel();
        NewsItemModel item = new NewsItemModel();
        item.setDescription("item_descr");
        item.setGuid("guid");
        item.setCreator("author");
        item.setTitle("item_title");
        item.setPubDate(Calendar.getInstance().getTime());
        item.setLink("item_link");
        item.setEnclosure(new EnclosureModel());

        model.setItem(item);
        List<NewsItemModel> listItem = new ArrayList<>();
        listItem.add(item);
        model.setItems(listItem);
        assertNotNull(model.getItem());
        assertNotNull(((NewsItemModel)model.getItem()).getEnclosure());
        assertNotNull(((NewsItemModel)model.getItem()).getPubDate());
        assertEquals(0, ((NewsItemModel)model.getItem()).getTitle().compareTo("item_title"));
        assertEquals(0, ((NewsItemModel)model.getItem()).getDescription().compareTo("item_descr"));
        assertEquals(0, ((NewsItemModel)model.getItem()).getCreator().compareTo("author"));
        assertEquals(0, ((NewsItemModel)model.getItem()).getLink().compareTo("item_link"));
        assertEquals(1, model.getItems().size());
        assertEquals(0, ((NewsItemModel)model.getItem()).getGuid().compareTo("guid"));
    }



}