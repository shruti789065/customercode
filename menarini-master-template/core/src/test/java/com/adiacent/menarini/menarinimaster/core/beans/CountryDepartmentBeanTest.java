package com.adiacent.menarini.menarinimaster.core.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class CountryDepartmentBeanTest {

	private CountryDepartmentBean countryDepartmentBean;

	@Before
	public void setUp() {

		List<KeyValueItem> department = new ArrayList<>();
		department.add(new KeyValueItem());
		department.add(new KeyValueItem());

		countryDepartmentBean = new CountryDepartmentBean("Title", "Name", department);
	}

	@Test
	public void testGetTitle() {
		assertEquals("Title", countryDepartmentBean.getTitle());
	}

	@Test
	public void testGetName() {
		assertEquals("Name", countryDepartmentBean.getName());
	}

	@Test
	public void testGetDepartment() {
		assertNotNull(countryDepartmentBean.getDepartment());
		assertEquals(2, countryDepartmentBean.getDepartment().size()); // Assumi che ci siano due elementi nel dipartimento
	}
}
