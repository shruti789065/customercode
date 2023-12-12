package com.adiacent.menarini.menarinimaster.core.models;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.Before;
import org.junit.Test;

public class LinkFormatterTest {

	private LinkFormatter linkFormatter;

	@Before
	public void setUp() {
		// Inizializza il LinkFormatter
		linkFormatter = new LinkFormatter();
	}

	@Test
	public void testFormattedLinkWhenAuthoredLinkIsNotEmpty() {
		// Mock SlingHttpServletRequest
		SlingHttpServletRequest request = mock(SlingHttpServletRequest.class);
		// Imposta il valore di authoredLink
		when(request.adaptTo(LinkFormatter.class)).thenReturn(linkFormatter);
		linkFormatter.authoredLink = "http://example.com";

		// Chiama il metodo init
		linkFormatter.init();

		// Verifica che formattedLink sia stato modificato correttamente

		assertEquals("http://example.com", linkFormatter.getFormattedLink());
	}

	@Test
	public void testFormattedLinkWhenAuthoredLinkIsEmpty() {
		// Mock SlingHttpServletRequest
		SlingHttpServletRequest request = mock(SlingHttpServletRequest.class);
		// Imposta il valore di authoredLink
		when(request.adaptTo(LinkFormatter.class)).thenReturn(linkFormatter);
		linkFormatter.authoredLink = ""; // Vuoto

		// Chiama il metodo init
		linkFormatter.init();

		// Verifica che formattedLink sia vuoto
		assertEquals(null, linkFormatter.getFormattedLink());
	}
}
