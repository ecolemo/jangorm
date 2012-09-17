package com.ecolemo.jangorm;

import java.util.Map.Entry;

public class ForeignObjectSet<T extends Model> extends QuerySet<T> {

	protected Model foreignObject;

	public ForeignObjectSet(Model object, Class<T> modelClass) {
		super(modelClass);
		
		this.foreignObject = object;
		
		whereClause.add("`" + getTableName(modelClass) + "`." + getTableName(object.getClass()) + "_id=?");
		whereParameters.add(object.get("id"));
	}

	public T create(Entry<String, Object>... entries) {
		
		try {
			T object = (T) modelClass.newInstance();
			for (Entry<String, Object> entry : entries) {
				object.set(entry.getKey(), entry.getValue());
			}
			object.set(foreignObject.getClass().getSimpleName().toLowerCase(), foreignObject);
			manager.insertModel(object);
			return object;
//			object.set(getTableName(foreignObject.getClass()), foreignObject);
//			dao.create(object);
//			dao.refresh(object);
//			return object;
//			
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public void add(T relatedObject) {
		relatedObject.set(foreignObject.getClass().getSimpleName().toLowerCase(), foreignObject);
		relatedObject.save();
	}

}
