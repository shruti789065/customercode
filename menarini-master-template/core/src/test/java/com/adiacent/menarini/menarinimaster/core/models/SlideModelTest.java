package com.adiacent.menarini.menarinimaster.core.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.adobe.cq.wcm.core.components.models.Teaser;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.factory.ModelFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SlideModelTest {

    @Mock
    private Teaser delegate;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private Resource resource;

    @Mock
    private Resource videoResource;

    @Mock
    private Resource durationVideoResource;

    @Mock
    private ModelFactory modelFactory;

    @InjectMocks
    private SlideModel slideModel;

    @Before
    public void setUp() {
        // Mocking resource resolver and required resource structure
        when(request.getResourceResolver()).thenReturn(mock(ResourceResolver.class));
        Mockito.lenient().when(request.getResourceResolver().getResource("videoPath")).thenReturn(resource);
        Mockito.lenient().when(resource.getChild("jcr:content/metadata/xmpDM:duration")).thenReturn(durationVideoResource);

        // Initialize the SlideModel
        slideModel.init();
    }

    @Test
    public void testGetVideoFilePath() {
        slideModel.setVideoFilePath("videoPath");
        assertEquals("videoPath", slideModel.getVideoFilePath());
    }

    @Test
    public void testGetVideoFormat() {
        // Test video format detection
        slideModel.setVideoFilePath("/content/dam/menarinimaster/sample.mp4");
        slideModel.setVideoFormat("video/mp4");
        assertEquals("video/mp4", slideModel.getVideoFormat());


        // Test null case
        slideModel.setVideoFilePath("videoPath.unknown");
        slideModel.setVideoFormat(null);
	    assertNull(slideModel.getVideoFormat());
    }

    @Test
    public void testGetDuration() {
        // Mock duration video resource and its value map
        when(durationVideoResource.getValueMap()).thenReturn(mock(ValueMap.class));
        Mockito.lenient().when(durationVideoResource.getValueMap().get("xmpDM:value", String.class)).thenReturn("123");

        // Test duration retrieval
        slideModel.setVideoFilePath("videoPath");
        slideModel.setDuration("123");
        assertEquals("123", slideModel.getDuration());

        // Test null case
        slideModel.setVideoFilePath("videoPathWithoutDuration");
        slideModel.setDuration(null);
        assertNull(slideModel.getDuration());
    }


    @Test
    public void testSetDuration() {
        slideModel.setDuration("456");
        assertEquals("456", slideModel.getDuration());
    }

    @Test
    public void testSetVideoFilePath() {
        slideModel.setVideoFilePath("newVideoPath");
        assertEquals("newVideoPath", slideModel.getVideoFilePath());
    }

    @Test
    public void testSetVideoFormat() {
        slideModel.setVideoFormat("newVideoFormat");
        assertEquals("newVideoFormat", slideModel.getVideoFormat());
    }
}
