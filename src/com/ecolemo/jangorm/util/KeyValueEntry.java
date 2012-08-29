package com.ecolemo.jangorm.util;

import java.util.Map;

public class KeyValueEntry implements Map.Entry<String, Object> {
	private String key;
	private Object value;

	public KeyValueEntry(String key, Object value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public String getKey() {
		return key;
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
	
	public static KeyValueEntry kv(String key, Object value) {
		return new KeyValueEntry(key, value);
	}
}