package com.adiacent.menarini.menarinimaster.core.models;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class PipelineItemModelTest {

	@Test
	void testGetCompoundValue() throws RepositoryException {
		PipelineItemModel model = new PipelineItemModel();

		Node currentNode = Mockito.mock(Node.class);
		Property compoundValueProperty = Mockito.mock(Property.class);

		when(currentNode.hasProperty("compoundValue")).thenReturn(true);
		when(currentNode.getProperty("compoundValue")).thenReturn(compoundValueProperty);
		when(compoundValueProperty.getString()).thenReturn("/content/dam/test-compound");

		model.currentNode = currentNode;
		model.init();

		String compoundValue = model.getCompoundValue();

		assertEquals("test-compound", compoundValue);
	}
}
