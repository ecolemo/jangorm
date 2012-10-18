package com.ecolemo.jangorm;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;

public class TableConfigCache {
	public Map<String,DatabaseTableConfig> configMap = new HashMap<String,DatabaseTableConfig>();

	public void register(String name, DatabaseTableConfig config) {
		System.out.println("[tablecfg] registerd : " + name);
		configMap.put(name,  config);
	}
	public DatabaseTableConfig get(ConnectionSource source, Class<? extends Model> modelClass) throws SQLException {
		String name = modelClass.getName();
		if (configMap.containsKey(name)) {
			return configMap.get(name);
		}
		
		DatabaseTableConfig tableConfig = DatabaseTableConfig.fromClass(source, modelClass);
		register(modelClass.getName(), tableConfig);
		
		return tableConfig;
	}
	
	public static TableConfigCache _cache = new TableConfigCache();
}
