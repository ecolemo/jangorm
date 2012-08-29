package com.ecolemo.jangorm.manager;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ecolemo.jangorm.Model;
import com.ecolemo.jangorm.ModelException;
import com.ecolemo.jangorm.QuerySet;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;


public abstract class ModelManager {

	private static ModelManager defaultModelManager;

	public static ModelManager getDefaultModelManager() {
		return defaultModelManager;
	}

	public static void setDefaultModelManager(ModelManager defaultModelManager) {
		ModelManager.defaultModelManager = defaultModelManager;
	}

	public abstract ConnectionSource getConnectionSource();
	public abstract DatabaseType getDatabaseType();
	
	public abstract Map<String, Object> preparedQueryForMap(String query, Object... parameters);
	public abstract Object preparedQueryForValue(String query, Object... parameters);

	public abstract int executeUpdate(String query, Object... parameters);
	
	public abstract <T extends Model> List<T> queryForList(QuerySet<T> querySet);
	public abstract <T extends Model> T preparedQueryForModel(Class<T> modelClass, String query, Object... parameters);
	public abstract <T extends Model> void insertModel(T model);
	public abstract <T extends Model> void updateModel(T model);

	public void createTables(Class<? extends Model>... modelClasses) {
		try {
			for (Class<? extends Model> clazz : modelClasses) {
				TableUtils.createTable(getConnectionSource(), clazz);
			}
		} catch (SQLException e) {
			throw new ModelException(e);
		}
	}


	public void dropTables(Class<? extends Model>... modelClasses) {
		try {
			for (Class<? extends Model> clazz : modelClasses) {
				TableUtils.dropTable(getConnectionSource(), clazz, true);
			}
		} catch (SQLException e) {
			throw new ModelException(e);
		}
		
	}
}
