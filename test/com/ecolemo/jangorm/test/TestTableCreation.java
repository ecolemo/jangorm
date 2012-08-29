package com.ecolemo.jangorm.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ecolemo.jangorm.manager.ModelManager;
import com.ecolemo.jangorm.manager.SQLiteModelManager;
import com.ecolemo.jangorm.test.model.Table;

public class TestTableCreation {

	@Test
	public void createTableFromModel() {
		ModelManager manager = new SQLiteModelManager("testdb.sqlite3");
		manager.dropTables(Table.class);
		manager.createTables(Table.class);
		assertEquals("table", manager.preparedQueryForMap("SELECT * FROM sqlite_master WHERE name='table'").get("name"));
	}

}
