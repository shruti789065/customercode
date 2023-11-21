package com.adiacent.menarini.menarinimaster.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RunModeModelTest {

	@Mock
	private SlingSettingsService slingSettingsService;

	@Mock
	private SlingHttpServletRequest slingHttpServletRequest;

	@Mock
	private Resource resource;

	@InjectMocks
	private RunModeModel runModeModel;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetPublishModeWhenPublishModeActive() {
		when(slingSettingsService.getRunModes()).thenReturn(Collections.singleton("publish"));

		runModeModel.init();

		assertTrue(runModeModel.getPublishMode());
	}

	@Test
	public void testGetPublishModeWhenPublishModeNotActive() {
		when(slingSettingsService.getRunModes()).thenReturn(Collections.singleton("author"));

		runModeModel.init();

		assertFalse(runModeModel.getPublishMode());
	}
}
