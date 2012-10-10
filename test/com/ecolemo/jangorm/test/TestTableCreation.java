package com.ecolemo.jangorm.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ecolemo.jangorm.manager.ModelManager;
import com.ecolemo.jangorm.manager.SQLiteModelManager;
import com.ecolemo.jangorm.test.model.SampleTable;

public class TestTableCreation {

	@Test
	public void createTableFromModel() {
		ModelManager manager = new SQLiteModelManager("testdb.sqlite3");
		manager.dropTables(SampleTable.class);
		manager.createTables(SampleTable.class);
		assertEquals("table", manager.preparedQueryForMap("SELECT * FROM sqlite_master WHERE name='table'").get("name"));
	}

}
