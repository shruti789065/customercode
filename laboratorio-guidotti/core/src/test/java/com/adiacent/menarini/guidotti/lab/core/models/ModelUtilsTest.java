package com.adiacent.menarini.guidotti.lab.core.models;

import com.adiacent.menarini.guidotti.lab.core.utils.ModelUtils;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.jcr.Session;
import javax.jcr.RepositoryException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ModelUtilsTest {

	@Mock
	private QueryBuilder queryBuilder;

	@Mock
	private Query query;

	@Mock
	private SearchResult searchResult;

	@Mock
	private Hit hit;

	@Mock
	private ResourceResolver resourceResolver;

	@Mock
	private Resource resource;

	@Mock
	private Session session;

	@InjectMocks
	private ModelUtils modelUtils;

	private Map<String, String> predicate;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		predicate = new HashMap<>();
		predicate.put("path", "/content/sample");
		predicate.put("type", "cq:Page");
	}

	@Test
	public void testFindResourceByPredicateSuccess() throws RepositoryException {
		// Arrange
		when(queryBuilder.createQuery(any(), any(Session.class))).thenReturn(query);
		when(query.getResult()).thenReturn(searchResult);
		when(searchResult.getHits()).thenReturn(Collections.singletonList(hit));
		when(hit.getPath()).thenReturn("/content/sample/page");
		when(resourceResolver.getResource("/content/sample/page")).thenReturn(resource);

		// Act
		Resource result = ModelUtils.findResourceByPredicate(queryBuilder, predicate, session, resourceResolver);

		// Assert
		assertNotNull(result);
		assertEquals(resource, result);
		verify(resourceResolver, times(1)).getResource("/content/sample/page");
	}

	@Test
	public void testFindResourceByPredicateQueryBuilderNull() {
		// Act
		Resource result = ModelUtils.findResourceByPredicate(null, predicate, session, resourceResolver);

		// Assert
		assertNull(result);
	}

	@Test
	public void testFindResourceByPredicatePredicateNull() {
		// Act
		Resource result = ModelUtils.findResourceByPredicate(queryBuilder, null, session, resourceResolver);

		// Assert
		assertNull(result);
	}

	@Test
	public void testFindResourceByPredicateSessionNull() {
		// Act
		Resource result = ModelUtils.findResourceByPredicate(queryBuilder, predicate, null, resourceResolver);

		// Assert
		assertNull(result);
	}

	@Test
	public void testFindResourceByPredicateResourceResolverNull() {
		// Act
		Resource result = ModelUtils.findResourceByPredicate(queryBuilder, predicate, session, null);

		// Assert
		assertNull(result);
	}

	@Test
	public void testFindResourceByPredicateNoHits() {
		// Arrange
		when(queryBuilder.createQuery(any(), any(Session.class))).thenReturn(query);
		when(query.getResult()).thenReturn(searchResult);
		when(searchResult.getHits()).thenReturn(Collections.emptyList());

		// Act
		Resource result = ModelUtils.findResourceByPredicate(queryBuilder, predicate, session, resourceResolver);

		// Assert
		assertNull(result);
	}

	@Test
	public void testFindResourceByPredicateRepositoryException() throws RepositoryException {
		// Arrange
		when(queryBuilder.createQuery(any(), any(Session.class))).thenReturn(query);
		when(query.getResult()).thenReturn(searchResult);
		when(searchResult.getHits()).thenReturn(Collections.singletonList(hit));
		when(hit.getPath()).thenThrow(new RepositoryException());

		// Act & Assert
		assertThrows(RuntimeException.class, () ->
				ModelUtils.findResourceByPredicate(queryBuilder, predicate, session, resourceResolver)
		);
	}
}
