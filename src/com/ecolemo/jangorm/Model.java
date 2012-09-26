package com.ecolemo.jangorm;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ecolemo.jangorm.manager.ModelManager;


public class Model {

	public static <T extends Model> QuerySet<T> objects(Class<T> model) {
		return new QuerySet<T>(model);
	}

	protected Map<String, Object> properties = new HashMap<String, Object>();
	protected boolean loadedFromStorage = false;
	public static SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
	
	public <T extends Model> T set(String key, Object value) {
		properties.put(key, value);
		
		try {
			Field field = getClass().getDeclaredField(key);
			field.setAccessible(true);
			if (field.getType().getName().equals("boolean")) {
				field.set(this, value != Integer.valueOf(0));
			} else if (field.getType().equals(Date.class) && value instanceof String) {
				field.set(this, dbDateFormat.parse((String)value));
			} else {
				field.set(this, value);
			}
		} catch (NoSuchFieldException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
			throw new ModelException(e);
		} catch (ParseException e) {
			throw new ModelException(e);
		}
		
		return (T) this;
	}

	public <F extends Model> ForeignObjectSet<F> foreignObjects(Class<F> model) {
		return new ForeignObjectSet<F>(this, model);
	}
	
	public Object get(String key) {
		try {
			Field field = getClass().getField(key);
			return field.get(this);
		} catch (NoSuchFieldException e) {
			return properties.get(key);
		} catch (IllegalArgumentException e) {
			throw new ModelException(e);
		} catch (IllegalAccessException e) {
			throw new ModelException(e);
		}
	}
	
	public boolean has(String key) {
		return properties.containsKey(key);
	}
	
	public Set<String> keySet() {
		return properties.keySet();
	}
	
	public void remove(String key) {
		properties.remove(key);
	}

	public int getInt(String key, int defaultValue) {
		if (properties.get(key) == null) return defaultValue;
		return (Integer) properties.get(key);
	}

	public boolean getBoolean(String key) {
		if (properties.get(key) == null) return false;
		if (properties.get(key) instanceof Integer) return (Integer) properties.get(key) != 0;
		return (Boolean) properties.get(key);
	}
	
	public int getInt(String key) {
		return (Integer) properties.get(key);
	}
	
	public String getString(String key) {
		return (String) properties.get(key);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ":" + properties.toString();
	}

	public Model getModel(String key) {
		return (Model) properties.get(key);
	}
	
	public void save() {
		System.out.println("SAVE: " + existsInStorage() + " => " + this);
		ModelManager manager = ModelManager.getDefaultModelManager();
		if (existsInStorage()) {
			manager.updateModel(this);
		} else {
			manager.insertModel(this);
		}
	}
	
	public void delete() {
		if (existsInStorage()) {
			Model.objects(getClass()).filter("id=?", get("id")).delete();
		}
	}
	
	public boolean existsInStorage() {
		return properties.containsKey("id");
	}
	
	public static class CurrentClassGetter extends SecurityManager {
	    public Class getCurrentClass() {
	        return getClassContext()[1]; 
	    }
	}	
}
