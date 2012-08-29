package com.ecolemo.jangorm.manager;



public class SQLiteModelManager extends JDBCModelManager {

	public SQLiteModelManager(String filename) {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		url = "jdbc:sqlite:" + filename;
	}

}
