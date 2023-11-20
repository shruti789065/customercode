package com.adiacent.menarini.menarinimaster.core.models;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.ListItem;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomTabsListItemTest {

	@Mock
	private ListItem wrappedListItem;

	private CustomTabsListItem customTabsListItem;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		customTabsListItem = new CustomTabsListItem(wrappedListItem);
	}

	@Test
	void testGetTitle() {
		String expectedTitle = "Sample Title";
		when(wrappedListItem.getTitle()).thenReturn(expectedTitle);

		String actualTitle = customTabsListItem.getTitle();

		assertEquals(expectedTitle, actualTitle);
	}

	@Test
	void testGetDescription() {
		String expectedDescription = "Sample Description";
		when(wrappedListItem.getDescription()).thenReturn(expectedDescription);

		String actualDescription = customTabsListItem.getDescription();

		assertEquals(expectedDescription, actualDescription);
	}

	@Test
	void testGetURL() {
		String expectedURL = "https://example.com";
		when(wrappedListItem.getURL()).thenReturn(expectedURL);

		String actualURL = customTabsListItem.getURL();

		assertEquals(expectedURL, actualURL);
	}

	@Test
	void testGetId() {
		String wrappedItemId = "12345";
		when(wrappedListItem.getId()).thenReturn(wrappedItemId);

		String expectedId = "megamenu-" + wrappedItemId;
		String actualId = customTabsListItem.getId();

		assertEquals(expectedId, actualId);
	}

	// Add more test methods for the remaining methods of CustomTabsListItem

	@Test
	void testGetPath() {
		String expectedURL = "https://example.com";
		when(wrappedListItem.getPath()).thenReturn(expectedURL);

		String actualPath= customTabsListItem.getPath();

		assertEquals(expectedURL, actualPath);

	}

	@Test
	void testGetName() {
		String expectedURL = "https://example.com";
		when(wrappedListItem.getName()).thenReturn(expectedURL);

		String actualName = customTabsListItem.getName();

		assertEquals(expectedURL, actualName);

	}

	@Test
	void testGetTeaserResource() {

		Resource currentResource = mock(Resource.class);
		when(wrappedListItem.getTeaserResource()).thenReturn(currentResource);
		Resource actualResource = customTabsListItem.getTeaserResource();
		assertEquals(currentResource, actualResource);


	}

	@Test
	void testGetLink() {

		Link link = mock(Link.class);
		when(wrappedListItem.getLink()).thenReturn(link);
		Link actualLink = customTabsListItem.getLink();
		assertEquals(link, actualLink);

	}

	@Test
	void testGetLastModified() {

		Calendar expectedLastModified = Calendar.getInstance();
		when(wrappedListItem.getLastModified()).thenReturn(expectedLastModified);

		Calendar actualLastModified = customTabsListItem.getLastModified();

		assertEquals(expectedLastModified, actualLastModified);

	}


}
