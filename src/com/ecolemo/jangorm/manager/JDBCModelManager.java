package com.ecolemo.jangorm.manager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.ecolemo.jangorm.JDBCQueryHandler;
import com.ecolemo.jangorm.Model;
import com.ecolemo.jangorm.ModelException;
import com.ecolemo.jangorm.QuerySet;
import com.ecolemo.jangorm.util.DataMap;
import com.ecolemo.jangorm.util.QueryLog;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.db.SqliteDatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;

public class JDBCModelManager extends ModelManager {

	protected String url;
	private SqliteDatabaseType databaseType;

    @SuppressWarnings("unchecked")
    public <T extends Model> void insertModel(T model) {
		try {
			Dao<T, Integer> dao = DaoManager.createDao(getConnectionSource(), model.getClass());
			dao.create(model);
			model.set("id", model.getClass().getField("id").get(model));
	
		} catch (SQLException e) {
			throw new ModelException(e);
		} catch (IllegalArgumentException e) {
			throw new ModelException(e);
		} catch (SecurityException e) {
			throw new ModelException(e);
		} catch (IllegalAccessException e) {
			throw new ModelException(e);
		} catch (NoSuchFieldException e) {
			throw new ModelException(e);
		}
	}

    @SuppressWarnings("unchecked")
    public <T extends Model> void updateModel(T model) {
		try {
			Dao<T, Integer> dao = DaoManager.createDao(getConnectionSource(), model.getClass());
			dao.update(model);
	
		} catch (SQLException e) {
			throw new ModelException(e);
		}
	}
	
	@Override
	public <T extends Model> T preparedQueryForModel(Class<T> modelClass, String query, Object... parameters) {
		// dao
		return null;
	}

	@Override
	public Object preparedQueryForValue(String query, Object... parameters) {
		return new JDBCQueryHandler<Object>() {
	
			@Override
			public Object run(PreparedStatement stmt, String query, Object... parameters) throws SQLException {
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					return rs.getObject(1);
				}
				throw new ModelException("no row found: " + query);
			}
		}.execute(this, query, parameters);
	}

	@Override
	public Map<String, Object> preparedQueryForMap(String query, Object... parameters) {
		return new JDBCQueryHandler<Map<String, Object>>() {
			@Override
			public Map<String, Object> run(PreparedStatement stmt, String query, Object... parameters)
					throws SQLException {
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					Map<String, Object> result = new HashMap<String, Object>();
					ResultSetMetaData meta = rs.getMetaData();
					for (int i = 0; i < meta.getColumnCount(); i++) {
						result.put(meta.getColumnName(i + 1), rs.getObject(i + 1));
					}
					return result;
				}
				throw new ModelException("no row found: " + query);
			}
		}.execute(this, query, parameters);
	}

	@Override
	public ConnectionSource getConnectionSource() {
		try {
			return new JdbcConnectionSource(url);
		} catch (SQLException e) {
			throw new ModelException(e);
		}
	}

	@Override
	public DatabaseType getDatabaseType() {
		if (databaseType == null)
			databaseType = new SqliteDatabaseType();
		return databaseType;
	}

	@Override
	public int executeUpdate(String query, Object... parameters) {
		return new JDBCQueryHandler<Integer>() {
			@Override
			public Integer run(PreparedStatement stmt, String query, Object... parameters) throws SQLException {
				return stmt.executeUpdate();
			}
		}.execute(this, query, parameters);
	
	}

	public <T extends Model> List<T> queryForList(final QuerySet<T> querySet) {
		String sql = querySet.buildSQL();
		Object[] params = querySet.getParameters().toArray();
		System.out.println(QueryLog.generate(sql, params));
		return new JDBCQueryHandler<List<T>>() {
			@Override
			public List<T> run(PreparedStatement stmt, String query, Object... parameters) throws SQLException {
				ResultSet rs = stmt.executeQuery();
				ResultSetMetaData metaData = rs.getMetaData();
				List<T> list = new ArrayList<T>();
				DatabaseTableConfig<T> tableConfig = DatabaseTableConfig.fromClass(getConnectionSource(), querySet.getModelClass());
				while (rs.next()) {
					try {
						T model = querySet.getModelClass().newInstance();
						for (int i = 1; i <= metaData.getColumnCount(); i++) {
							String[] columnName = metaData.getColumnLabel(i).split("__");
	
							if (columnName[0].equals(tableConfig.getTableName())) {
								model.set(columnName[1], rs.getObject(i));
								if (columnName[1].endsWith("_id")) {
									String foreignKey = columnName[1].replace("_id", "");
									if (!model.has(foreignKey)) {
										Class foreignClass = querySet.getModelClass().getField(foreignKey).getType();
										Model foriegnObject = (Model) foreignClass.newInstance();
										foriegnObject.set("id", rs.getObject(i));
										model.set(foreignKey, foriegnObject);
									}
								}
							} else {
								if (!model.has(columnName[0])) {
									Class<? extends Model> foreignClass = querySet.findModelClass(columnName[0]);
									Model foriegnObject = (Model) foreignClass.newInstance();
									model.set(columnName[0], foriegnObject);
								}
								Model foriegnObject = (Model) model.get(columnName[0]);
								foriegnObject.set(columnName[1], rs.getObject(i));
							}
							
						}
						list.add(model);
					} catch (InstantiationException e) {
						throw new ModelException(e);
					} catch (IllegalAccessException e) {
						throw new ModelException(e);
					} catch (SecurityException e) {
						throw new ModelException(e);
					} catch (NoSuchFieldException e) {
						throw new ModelException(e);
					}
				}
				rs.close();
				System.out.println(list);
				return list;
			}
		}.execute(this, sql, params);
	}

	@Override
	public DataMap queryForDataMap(String sql, Object... parameters) {
		System.out.println(QueryLog.generate(sql, parameters));
		return new JDBCQueryHandler<DataMap>() {
			@Override
			public DataMap run(PreparedStatement stmt, String query, Object... parameters) throws SQLException {
				ResultSet rs = stmt.executeQuery();
				ResultSetMetaData metaData = rs.getMetaData();
				DataMap row = new DataMap();
				if (rs.next()) {
					for (int i = 1; i <= metaData.getColumnCount(); i++) {
						row.put(metaData.getColumnName(i), rs.getObject(i));
					}
				}
				rs.close();
				return row;
			}
		}.execute(this, sql, parameters);
	}

	
	public List<DataMap> queryForList(String sql, Object... parameters) {
		System.out.println(QueryLog.generate(sql, parameters));
		return new JDBCQueryHandler<List<DataMap>>() {
			@Override
			public List<DataMap> run(PreparedStatement stmt, String query, Object... parameters) throws SQLException {
				ResultSet rs = stmt.executeQuery();
				ResultSetMetaData metaData = rs.getMetaData();
				List<DataMap> list = new ArrayList<DataMap>();
				while (rs.next()) {
					DataMap row = new DataMap();
					for (int i = 1; i <= metaData.getColumnCount(); i++) {
						row.put(metaData.getColumnName(i), rs.getObject(i));
					}
					list.add(row);
				}
				rs.close();
				return list;
			}
		}.execute(this, sql, parameters);
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url);
	}

	@Override
	public void loadJSON(String model, String json) {
		try {
			
			String[] parts = model.split("\\.");
			String table = parts[parts.length - 1].toLowerCase();
			
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> object = mapper.readValue(json, Map.class);
			StringBuilder sql = new StringBuilder();
			
			sql.append("INSERT INTO `" + table + "` (`");
			sql.append(StringUtils.join(object.keySet(), "`, `"));
			sql.append("`) VALUES (");
			sql.append(StringUtils.repeat("?", ", ", object.keySet().size()));
			sql.append(")");
			System.out.println(sql + " " + object.values());
			int update = executeUpdate(sql.toString(), object.values().toArray(new Object[]{}));
			System.out.println("affected: " + update);
			
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loadJSONForUpdate(String model, String json) {
		try {
			
			String[] parts = model.split("\\.");
			String table = parts[parts.length - 1].toLowerCase();
			
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> object = mapper.readValue(json, Map.class);
			StringBuilder sql = new StringBuilder();
			
			sql.append("UPDATE `" + table + "` SET ");
			
			for (String key : object.keySet()) {
				sql.append("`" + key + "`=?, ");
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.deleteCharAt(sql.length() - 1);
			
			sql.append(" WHERE id=?");

			List values = new ArrayList(object.values());
			values.add(object.get("id"));
			System.out.println(sql + " " + values);
			int update = executeUpdate(sql.toString(), values.toArray(new Object[]{}));
			System.out.println("affected: " + update);
			
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
