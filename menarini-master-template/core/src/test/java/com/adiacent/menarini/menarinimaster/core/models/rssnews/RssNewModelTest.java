package com.adiacent.menarini.menarinimaster.core.models.rssnews;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith({AemContextExtension.class})
class RssNewModelTest {
    @Test
    void testChannelModel() {
        RssNewModel model = new RssNewModel();
        ChannelModel ch = new ChannelModel();
        model.setChannel(ch);
        assertNotNull(model.getChannel());
    }
}