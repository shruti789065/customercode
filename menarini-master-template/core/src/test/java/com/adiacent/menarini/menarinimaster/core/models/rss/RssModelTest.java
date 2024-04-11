package com.adiacent.menarini.menarinimaster.core.models.rss;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({AemContextExtension.class})
public class RssModelTest {
    @Test
    void testChannelModel() {
        RssModel model = new RssModel<BlogItemModel>();
        ChannelModel ch = new ChannelModel();
        model.setChannel(ch);
        assertNotNull(model.getChannel());
    }
}
