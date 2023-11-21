package com.adiacent.menarini.menarinimaster.core.beans;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class KeyValueItemTest {

	private KeyValueItem keyValueItem;

	@Before
	public void setUp() {
		keyValueItem = new KeyValueItem();
	}

	@Test
	public void testSetAndGetKey() {
		keyValueItem.setKey("testKey");
		assertEquals("testKey", keyValueItem.getKey());
	}

	@Test
	public void testSetAndGetValue() {
		keyValueItem.setValue("testValue");
		assertEquals("testValue", keyValueItem.getValue());
	}
}
