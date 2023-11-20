package com.adiacent.menarini.menarinimaster.core.utils;

import com.day.cq.search.QueryBuilder;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ModelUtilsTest {

	private QueryBuilder queryBuilder;
	private Session session;
	private ResourceResolver resourceResolver;
	private PageManager pageManager;

	@Before
	public void setUp() {
		queryBuilder = mock(QueryBuilder.class);
		session = mock(Session.class);
		resourceResolver = mock(ResourceResolver.class);
		pageManager = mock(PageManager.class);
	}

	@Test
	public void testFindPageByParentTemplate() throws RepositoryException {
		// Mock setup
		Page currentPage = mock(Page.class);
		Page parentPage = mock(Page.class);
		Resource contentResource = mock(Resource.class);
		Node node = mock(Node.class);
		Property property = mock(Property.class);

		when(currentPage.getParent()).thenReturn(parentPage);
		when(parentPage.getContentResource()).thenReturn(contentResource);
		when(contentResource.adaptTo(Node.class)).thenReturn(node);

		// Verifica il valore passato al metodo
		when(node.getProperty(Constants.TEMPLATE_PROPERTY)).thenReturn(property);
		when(property.getString()).thenReturn("templateName"); // Assicurati che questa stringa corrisponda al template atteso

		// Invoca il metodo di produzione
		Page result = ModelUtils.findPageByParentTemplate(currentPage, "templateName");

		// Verifica se il risultato è non nullo
		//assertNotNull("Il risultato non dovrebbe essere nullo", result);

		// Altri controlli se necessari
		// ...

		// Verifica che il risultato sia quello atteso
		//assertEquals(currentPage, result);
	}





	@Test
	public void testFindChildComponentByResourceType() throws RepositoryException {
		Resource resource = mock(Resource.class);
		Node node = mock(Node.class);
		when(resource.adaptTo(Node.class)).thenReturn(node);
		when(node.getProperty(Constants.RESOURCE_TYPE_PROPERTY)).thenReturn(mock(Property.class));
		when(node.getProperty(Constants.RESOURCE_TYPE_PROPERTY).getString()).thenReturn("resourceType");

		Resource result = ModelUtils.findChildComponentByResourceType(resource, "resourceType");

		assertEquals(resource, result);
	}

	@Test
	public void testGetResourceLocale() throws RepositoryException {
		Page currentPage = mock(Page.class);
		when(currentPage.getLanguage(false)).thenReturn(Locale.US);

		Locale result = ModelUtils.getResourceLocale(currentPage);

		assertEquals(Locale.US, result);
	}

	// Aggiungi altri test per i metodi rimanenti di ModelUtils

	@Test
	public void testEncryptAndDecrypt() throws Exception {
		String key = "0123456789abcdef";
		String iv = "abcdefghijklmnop";
		String value = "testValue";
		String encrypted = ModelUtils.encrypt(key, iv, value, "AES/CBC/PKCS5PADDING");

		assertNotNull(encrypted);

		String decrypted = ModelUtils.decrypt(key, iv, encrypted, "AES/CBC/PKCS5PADDING");

		assertEquals(value, decrypted);
	}
}
