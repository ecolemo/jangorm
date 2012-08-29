package com.ecolemo.jangorm.util;

public class QueryLog {

	public static String generate(String sql, Object[] params) {
		String sqlLog = sql;
		for (Object param : params) {
			if (param == null) {
				sqlLog = sqlLog.replaceFirst("\\?", "NULL");
			} else if (param instanceof Number) {
				sqlLog = sqlLog.replaceFirst("\\?", param.toString());
			} else {
				sqlLog = sqlLog.substring(0, sqlLog.indexOf("?")) + 
				"'" + param.toString() + "'" +
				sqlLog.substring(sqlLog.indexOf("?") + 1);
			}
		}
		return sqlLog;
	}
	
}
