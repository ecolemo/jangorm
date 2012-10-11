package com.ecolemo.jangorm.util;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataMap implements Map<String, Object> {

	protected Map<String, Object> data = new HashMap<String, Object>();

	public void clear() {
		data.clear();
	}

	public boolean containsKey(Object key) {
		return data.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return data.containsValue(value);
	}

	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return data.entrySet();
	}

	public boolean equals(Object o) {
		return data.equals(o);
	}

	public Object get(Object key) {
		return data.get(key);
	}

	public int hashCode() {
		return data.hashCode();
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public Set<String> keySet() {
		return data.keySet();
	}

	public Object put(String key, Object value) {
		return data.put(key, value);
	}

	public void putAll(Map<? extends String, ? extends Object> m) {
		data.putAll(m);
	}

	public Object remove(Object key) {
		return data.remove(key);
	}

	public int size() {
		return data.size();
	}

	public Collection<Object> values() {
		return data.values();
	}


	public Object get(Object key, Object defaultValue) {
		if (data.get(key) == null) return defaultValue;
		return data.get(key);
	}
	
	public String getString(String key) {
		return (String) get(key);
	}

	public String getString(String key, String defaultValue) {
		return (String) get(key, defaultValue);
	}
	
	public int getInt(String key) {
		if (get(key) instanceof BigDecimal) return ((BigDecimal) get(key)).intValue();
		return (Integer) get(key);
	}

	public int getInt(String key, int defaultValue) {
		if (get(key) instanceof BigDecimal) return ((BigDecimal) get(key)).intValue();
		return (Integer) get(key, defaultValue);
	}

	public long getLong(String key) {
		return (Long) get(key);
	}

	public long getLong(String key, long defaultValue) {
		return (Long) get(key, defaultValue);
	}

	public boolean getBoolean(String key) {
		return (Boolean) get(key);
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		return (Boolean) get(key, defaultValue);
	}

	public Date getDate(String key) {
		return (Date) get(key);
	}
	
	@Override
	public String toString() {
		return data.toString();
	}
}
