package com.ecolemo.jangorm.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.ecolemo.jangorm.ModelException;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class MySQLModelManager extends JDBCModelManager {

	private final String user;
	private final String password;

	public MySQLModelManager(String host, String database, String user, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		url = String.format("jdbc:mysql://%s/%s?useUnicode=true&characterEncoding=utf8", host, database);
		this.user = user;
		this.password = password;
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}
	
	@Override
	public ConnectionSource getConnectionSource() {
		try {
			return new JdbcConnectionSource(url, user, password);
		} catch (SQLException e) {
			throw new ModelException(e);
		}
	}
	
}
