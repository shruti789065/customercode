package com.adiacent.menarini.menarinimaster.core.beans;

import lombok.Getter;

import java.util.List;

@Getter
public class CountryDepartmentBean {

		private String title;
		private String name;
		private List<KeyValueItem> department;

		public CountryDepartmentBean(String title, String name, List<KeyValueItem> department) {
			this.title = title;
			this.name = name;
			this.department = department;
		}

}
