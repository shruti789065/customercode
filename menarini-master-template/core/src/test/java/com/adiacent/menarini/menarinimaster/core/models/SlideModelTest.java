package com.adiacent.menarini.menarinimaster.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.factory.ModelFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.jcr.*;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.nodetype.PropertyDefinitionTemplate;
import java.math.BigDecimal;

public class SlideModelTest {

    @Mock
    Resource resource;

    @Mock
    ValueMap valueMap;

    @Mock
    ModelFactory modelFactory;

    @InjectMocks
    SlideModel slideModel = new SlideModel();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetVideoFilePath() {
        Mockito.when(valueMap.get("videoFilePath", String.class)).thenReturn("/content/dam/menarinimaster/sample.mp4");
        Mockito.when(resource.getValueMap()).thenReturn(valueMap);
        slideModel.init();
        slideModel.setVideoFilePath("/content/dam/menarinimaster/sample.mp4");
        Assert.assertEquals("/content/dam/menarinimaster/sample.mp4", slideModel.getVideoFilePath());
    }

    @Test
    public void testGetVideoFormat() {
        Mockito.when(valueMap.get("videoFilePath", String.class)).thenReturn("/content/dam/menarinimaster/sample.mp4");
        Mockito.when(resource.getValueMap()).thenReturn(valueMap);
        slideModel.init();
        slideModel.setVideoFilePath("/content/dam/menarinimaster/sample.mp4");
        slideModel.setVideoFormat("video/mp4");
        Assert.assertEquals("video/mp4", slideModel.getVideoFormat());
    }

    @Test
    public void testGetDuration() throws Exception {
        Node node = Mockito.mock(Node.class);
        Property property = Mockito.mock(Property.class);
        PropertyIterator iterator = Mockito.mock(PropertyIterator.class);
        ValueFactory valueFactory = Mockito.mock(ValueFactory.class);
        Value value = Mockito.mock(Value.class);
        NodeType nodeType = Mockito.mock(NodeType.class);
        PropertyDefinitionTemplate propertyDefinitionTemplate = Mockito.mock(PropertyDefinitionTemplate.class);
        PropertyDefinition propertyDefinition = Mockito.mock(PropertyDefinition.class);

        Mockito.when(valueFactory.createValue(Mockito.anyString())).thenReturn(value);
        Mockito.when(value.getDecimal()).thenReturn(BigDecimal.valueOf(120.0));
        Mockito.when(node.getProperty(Mockito.eq("jcr:content/metadata/xmpDM:duration"))).thenReturn(property);
        Mockito.when(node.getPrimaryNodeType()).thenReturn(nodeType);
        Mockito.when(nodeType.getName()).thenReturn("nt:unstructured");
        Mockito.when(property.getDefinition()).thenReturn(propertyDefinition);
        Mockito.when(propertyDefinition.isMultiple()).thenReturn(false);
        Mockito.when(propertyDefinition.getRequiredType()).thenReturn(PropertyType.DECIMAL);
        Mockito.when(iterator.hasNext()).thenReturn(true, false);
        Mockito.when(iterator.nextProperty()).thenReturn(property);
        Mockito.when(node.getProperties()).thenReturn(iterator);
        Mockito.when(resource.adaptTo(Node.class)).thenReturn(node);
        //Mockito.when(node.getSession().getValueFactory()).thenReturn(valueFactory);
        slideModel.init();
        slideModel.setDuration("2:00");
        Assert.assertEquals("2:00", slideModel.getDuration());
    }
}
