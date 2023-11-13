package com.adiacent.menarini.menarinimaster.core.beans;

import java.util.List;

public class CountryDepartmentBean {

		private String title;
		private String name;
		private List<KeyValueItem> department;

		public CountryDepartmentBean(String title, String name, List<KeyValueItem> department) {
			this.title = title;
			this.name = name;
			this.department = department;
		}

		public String getTitle() {
			return title;
		}

		public String getName() {
			return name;
		}

		public List<KeyValueItem> getDepartment() {
			return department;
		}

}
