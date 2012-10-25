package com.ecolemo.jangorm.test;

import static com.ecolemo.jangorm.util.KeyValueEntry.kv;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import com.ecolemo.jangorm.manager.ModelManager;
import com.ecolemo.jangorm.manager.SQLiteModelManager;
import com.ecolemo.jangorm.test.model.SampleOrder;
import com.ecolemo.jangorm.test.model.SampleTable;
import com.ecolemo.jangorm.test.model.TableGroup;

public class TestSync {
	@Test
	public void toJSON() throws Exception {
		SampleTable table = new SampleTable();
		table.set("name", "aaaa");
		table.set("index", 4);
		SampleOrder order = new SampleOrder();
		order.set("id", "xxx");
		table.set("order", order);
		
		ObjectMapper mapper = new ObjectMapper();
		Map map = mapper.readValue(table.toJSON(), Map.class);
		assertEquals("aaaa", map.get("name"));
		assertEquals(4, map.get("index"));
		assertEquals("xxx", map.get("order_id"));
	}
	
	@Test
	public void loadJSON() throws Exception {
		ModelManager manager = new SQLiteModelManager("testdb.sqlite3");
		ModelManager.setDefaultModelManager(manager);
		manager.createTables(TableGroup.class, SampleTable.class, SampleOrder.class);

		SampleTable table = new SampleTable();
		table.set("name", "aaaa");
		table.set("index", 4);
		TableGroup group = new TableGroup();
		group.set("id", "xxx");
		table.set("tablegroup", group);

		System.out.println(table.toJSON());
		manager.loadJSONModel("com.ecolemo.jangorm.test.model.SampleTable", table.toJSON());
		
		SampleTable loaded = SampleTable.objects(SampleTable.class).get(kv("name", "aaaa"));
		assertEquals(4, loaded.index);
		assertEquals("xxx", loaded.get("tablegroup_id"));
	}
	
	
	@Test
	public void dump() {
		Class[] models = new Class[] {TableGroup.class, SampleTable.class, SampleOrder.class};
		ModelManager manager = new SQLiteModelManager("testdb.sqlite3");
		ModelManager.setDefaultModelManager(manager);
		manager.dropTables(models);
		manager.createTables(models);

		TableGroup group = TableGroup.objects(TableGroup.class).create(kv("name", "1층"));
		SampleTable table1 = SampleTable.objects(SampleTable.class).create(kv("name", "1번"), kv("tablegroup", group));
		SampleTable table2 = SampleTable.objects(SampleTable.class).create(kv("name", "2번"), kv("tablegroup", group));
		SampleOrder.objects(SampleOrder.class).create(kv("sampletable", table1), kv("total", 10000));
		SampleOrder.objects(SampleOrder.class).create(kv("sampletable", table2), kv("total", 20000));
		
		assertSavedData(group);
		
		StringWriter stringWriter = new StringWriter();
		BufferedWriter writer = new BufferedWriter(stringWriter);
		manager.dumpJSON(writer, models);
		
		manager.dropTables(models);
		manager.createTables(models);
		
		System.out.println("JSON:\n\n\n" + stringWriter.toString());
		BufferedReader reader = new BufferedReader(new StringReader(stringWriter.toString()));
		manager.loadJSON(reader);

		assertSavedData(group);
	}

	private void assertSavedData(TableGroup group) {
		TableGroup reloadedGroup = TableGroup.objects(TableGroup.class).get(kv("id", group.id));
		assertEquals("1층", reloadedGroup.get("name"));
		List<SampleTable> reloadedTables = reloadedGroup.foreignObjects(SampleTable.class).orderBy("name").asList();
		assertEquals(2, reloadedTables.size());
		assertEquals("2번", reloadedTables.get(1).name);
		assertEquals(10000, reloadedTables.get(0).foreignObjects(SampleOrder.class).get().total);
	}	
}
