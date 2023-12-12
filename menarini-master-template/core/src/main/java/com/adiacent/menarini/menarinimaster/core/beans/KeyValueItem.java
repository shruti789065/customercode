package com.adiacent.menarini.menarinimaster.core.beans;

import lombok.Getter;

@Getter
public class KeyValueItem {
	private String key;
	private String value;

	public void setKey(String key) {
		this.key = key;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
