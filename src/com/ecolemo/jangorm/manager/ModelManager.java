package com.ecolemo.jangorm.manager;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.ecolemo.jangorm.Model;
import com.ecolemo.jangorm.ModelException;
import com.ecolemo.jangorm.QuerySet;
import com.ecolemo.jangorm.util.DataMap;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;


public abstract class ModelManager {

	private static ModelManager defaultModelManager;
	protected ModelManagerExecutionListener executionListener;

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
	
	public abstract DataMap queryForDataMap(String sql, Object... parameters);
	public abstract List<DataMap> queryForList(String sql, Object... parameters);
	public abstract <T extends Model> List<T> queryForList(QuerySet<T> querySet);
	public abstract <T extends Model> T preparedQueryForModel(Class<T> modelClass, String query, Object... parameters);
	public abstract <T extends Model> void insertModel(T model);
	public abstract <T extends Model> void updateModel(T model);

	public void createTables(Class<? extends Model>... modelClasses) {
		try {
			for (Class<? extends Model> clazz : modelClasses) {
				TableUtils.createTableIfNotExists(getConnectionSource(), clazz);
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

	public abstract Connection getConnection() throws SQLException;

	public abstract void loadJSONModel(String table, String json);
	public abstract void loadJSONModelForUpdate(String table, String json);
	
	public void setExecutionListener(ModelManagerExecutionListener listenr) {
		this.executionListener = listenr;
	}
	
	protected String generatedId() {
        int timestamp = (int) (new Date().getTime() / 1000.0);
        String timestampStr = intToHexString(timestamp);

        String incrementId = incrementId(timestamp);
        previousTimestamp = timestamp;
    	
    	return timestampStr + incrementId + deviceId();
	}
	
	abstract protected String deviceId();
    
	private String intToHexString(int timestamp) {
		byte[] byteArray = intToByteArray(timestamp);
        String timestampStr = byteArrayToHexString(byteArray);
		return timestampStr;
	}
    
	static int incrementId = 0;
	static int previousTimestamp = -1;
    
	private String incrementId(int timestamp) {
		if (previousTimestamp == timestamp) {
			incrementId++;
		}
		else {
			incrementId=0;
		}
		
		byte[] byteArray = intToByteArray(incrementId);
		byte[] threeBytes = new byte[3];
		threeBytes[0] = byteArray[1];
		threeBytes[1] = byteArray[2];
		threeBytes[2] = byteArray[3];
		
		return byteArrayToHexString(threeBytes);
	}
	
    private static byte[] intToByteArray(final int integer) {
        ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE / 8);
        buff.putInt(integer);
        buff.order(ByteOrder.BIG_ENDIAN);
        return buff.array();
    }

	protected String byteArrayToHexString(byte[] byteArray) {
		StringBuilder s = new StringBuilder();
        for (int i=0;i<byteArray.length;i++) {
        	s.append(String.format("%02x", byteArray[i]));
        }
        // make it to hex code
        return s.toString();
	}

	public void loadJSON(Reader reader) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			List<Map> list = mapper.readValue(reader, List.class);
			for (Map object : list) {
				Class modelClass = Class.forName((String) object.get("model"));
				Model.objects(modelClass).create(new DataMap((Map) object.get("fields")));
			}
			
		} catch (JsonParseException e) {
			throw new ModelException(e);
		} catch (JsonMappingException e) {
			throw new ModelException(e);
		} catch (IOException e) {
			throw new ModelException(e);
		} catch (ClassNotFoundException e) {
			throw new ModelException(e);
		}
	}

	public void dumpJSON(Writer writer, Class<? extends Model>... modelClasses) {
		List<DataMap> objects = new ArrayList<DataMap>();
		for (Class<? extends Model> modelClass : modelClasses) {
			for (Model model : Model.objects(modelClass)) {
				DataMap object = new DataMap();
				object.put("pk", model.get("id"));
				object.put("model", modelClass.getName());
				object.put("fields", model);
				objects.add(object);
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(writer, objects);
			writer.close();
		} catch (JsonGenerationException e) {
			throw new ModelException(e);
		} catch (JsonMappingException e) {
			throw new ModelException(e);
		} catch (IOException e) {
			throw new ModelException(e);
		}
	}

}
