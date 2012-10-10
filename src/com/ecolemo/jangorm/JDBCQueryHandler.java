package com.ecolemo.jangorm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ecolemo.jangorm.manager.ModelManager;

public abstract class JDBCQueryHandler<T> {

	public abstract T run(PreparedStatement stmt, String query, Object... parameters) throws SQLException;

	public T execute(ModelManager manager, String query, Object... parameters) {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = manager.getConnection();
			stmt = conn.prepareStatement(query);
			for (int i = 0; i < parameters.length; i++) {
				stmt.setObject(i + 1, parameters[i]);
			}
			return run(stmt, query, parameters);
		} catch (SQLException e) {
			throw new ModelException(e.getMessage() + "[SQL]" + query, e);
		} finally {
			try {
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				throw new ModelException(e);
			}
			try {
				if (conn != null) conn.close();
			} catch (SQLException e) {
				throw new ModelException(e);
			}
		}
		
	}
	
}
