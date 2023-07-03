package com.adiacent.menarini.menarinimaster.core.models;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class LinkFormatterTest {

	@Mock
	private SlingHttpServletRequest request;

	@Optional
	private String authoredLink;

	private LinkFormatter linkFormatter;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		linkFormatter = new LinkFormatter();
		linkFormatter.authoredLink = authoredLink;
	}

	@Test
	void testGetFormattedLink_WhenAuthoredLinkIsNotEmpty() {
		String expectedFormattedLink = "https://example.com";
		String modifiedLink = "https://example.com";
		when(request.getResource()).thenReturn(null);
		when(request.adaptTo(String.class)).thenReturn(modifiedLink);
		authoredLink = "https://example.com";
		linkFormatter.authoredLink = authoredLink;

		linkFormatter.init();
		String actualFormattedLink = linkFormatter.getFormattedLink();

		assertEquals(expectedFormattedLink, actualFormattedLink);
	}

	@Test
	void testGetFormattedLink_WhenAuthoredLinkIsEmpty() {
		String expectedFormattedLink = null;
		authoredLink = "";
		linkFormatter.authoredLink = authoredLink;

		linkFormatter.init();
		String actualFormattedLink = linkFormatter.getFormattedLink();

		assertEquals(expectedFormattedLink, actualFormattedLink);
	}

	// Add more test methods for other scenarios if needed

}
