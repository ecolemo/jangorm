package com.ecolemo.jangorm.manager;

import com.ecolemo.jangorm.Model;

public interface ModelManagerExecutionListener {
	<T extends Model> void onInsert(T model);
	<T extends Model> void onUpdate(T model);
//	<T extends Model> void onDelete(T model);
	void onExecuteUpdate(String query, Object... parameters);
}
