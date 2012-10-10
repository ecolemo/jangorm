package com.ecolemo.jangorm.test;

import static com.ecolemo.jangorm.util.KeyValueEntry.kv;
import junit.framework.TestCase;

import com.ecolemo.jangorm.manager.ModelManager;
import com.ecolemo.jangorm.manager.SQLiteModelManager;
import com.ecolemo.jangorm.test.model.SampleOrder;
import com.ecolemo.jangorm.test.model.SampleTable;
import com.ecolemo.jangorm.test.model.TableGroup;

public class TestSync extends TestCase {
	public void testToJSON() throws Exception {
		SampleTable table = new SampleTable();
		table.set("name", "aaaa");
		table.set("index", 4);
		SampleOrder order = new SampleOrder();
		table.set("order", order);
		
		assertEquals("{\"index\":4,\"name\":\"aaaa\"}", table.toJSON(false));
		
		System.out.println(order.toJSON(false));
	}
	
	public void testLoadJSON() throws Exception {
		ModelManager manager = new SQLiteModelManager("testdb.sqlite3");
		ModelManager.setDefaultModelManager(manager);
		manager.createTables(TableGroup.class, SampleTable.class, SampleOrder.class);

		SampleTable table = new SampleTable();
		table.set("name", "aaaa");
		table.set("index", 4);
		table.set("order", new SampleOrder());

		manager.loadJSON("com.ecolemo.jangorm.test.model.SampleTable", table.toJSON(false));
		
		assertEquals(4, SampleTable.objects(SampleTable.class).get(kv("name", "aaaa")).index);
	}
}
