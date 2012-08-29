package com.ecolemo.jangorm.util;

import java.util.Map.Entry;

public class ID implements Entry<String, Object> {

	private Object value;

	public ID(Object value) {
		this.value = value;
	}
	
	@Override
	public String getKey() {
		return "id";
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public Object setValue(Object value) {
		this.value = value;
		return value;
	}

}
