/*
 *  Copyright 2018 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.jakala.menarini.core.listeners;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChange.ChangeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.VariationDef;
import com.day.cq.dam.api.DamConstants;
import com.jakala.menarini.core.models.ModelHelper;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.mockito.Mockito.*;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class FragmentCreateListenerTest {

    private final AemContext ctx = new AemContext();

    @InjectMocks
    private FragmentCreateListener listener;
    
    @Mock
    private ResourceResolverFactory resolverFactory;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Resource resource;

    @Mock
    private ContentFragment fragment;

    @Mock 
    private ContentElement idElement;

    @Mock
    private VariationDef variation;

    @BeforeEach
    void setUp() throws Exception {
        ctx.registerService(ResourceResolverFactory.class, resolverFactory, org.osgi.framework.Constants.SERVICE_RANKING, Integer.MAX_VALUE);
        ctx.registerService(ResourceResolver.class, resourceResolver, org.osgi.framework.Constants.SERVICE_RANKING, Integer.MAX_VALUE);
        when(resolverFactory.getServiceResourceResolver(any())).thenReturn(resourceResolver);
    }

    @Test
    void onChangeTest() {
        
        ResourceChange change = new ResourceChange(ChangeType.ADDED,"/content/test", false);

        when(resourceResolver.getResource(anyString())).thenReturn(resource);
        when(resource.getResourceType()).thenReturn(DamConstants.NT_DAM_ASSET);
        when(resource.adaptTo(ContentFragment.class)).thenReturn(fragment);
        when(fragment.getElement("id")).thenReturn(idElement);
        when(idElement.getContent()).thenReturn("");
        when(resource.getParent()).thenReturn(resource);
        when(resource.getPath()).thenReturn("/content/test");
        List<VariationDef> variationList = new ArrayList<>();
        variationList.add(variation);
        Iterator<VariationDef> variations = variationList.iterator();
        when(fragment.listAllVariations()).thenReturn(variations);
        when(fragment.getName()).thenReturn("custom");
        when(variation.getName()).thenReturn("custom_it");
        try (MockedStatic<ModelHelper> mockedHelper = mockStatic(ModelHelper.class)) {
            mockedHelper.when(() -> ModelHelper.nextSequence(any(ResourceResolver.class), anyString()))
                .thenReturn("mockedSequence");
            listener.onChange(Arrays.asList(change));
        }

    }
}