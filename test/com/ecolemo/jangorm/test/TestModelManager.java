package com.ecolemo.jangorm.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ecolemo.jangorm.manager.ModelManager;
import com.ecolemo.jangorm.manager.SQLiteModelManager;

public class TestModelManager {

	@Test
	public void defaultManager() {
		ModelManager.setDefaultModelManager(new SQLiteModelManager("testdb.sqlite3"));
		ModelManager manager = ModelManager.getDefaultModelManager();
		assertEquals(1, manager.preparedQueryForValue("SELECT 1"));
	}

}
