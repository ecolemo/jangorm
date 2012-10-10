package com.ecolemo.jangorm.test;

import static com.ecolemo.jangorm.util.KeyValueEntry.kv;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.ecolemo.jangorm.manager.ModelManager;
import com.ecolemo.jangorm.manager.SQLiteModelManager;
import com.ecolemo.jangorm.test.model.SampleOrder;
import com.ecolemo.jangorm.test.model.SampleTable;
import com.ecolemo.jangorm.test.model.TableGroup;

public class TestQueryForModel {

	private ModelManager manager;

	@Before
	public void setUp() {
		manager = new SQLiteModelManager("testdb.sqlite3");
		manager.dropTables(TableGroup.class, SampleTable.class, SampleOrder.class);
		manager.createTables(TableGroup.class, SampleTable.class, SampleOrder.class);
		
		ModelManager.setDefaultModelManager(manager);
	}

	@Test
	public void managerCreateModel() {
		SampleTable table = new SampleTable();
		table.name = "1번";
		table.index = 1;
		table.x = 1;
		table.y = 1;
		
		manager.insertModel(table);
		
		Map<String, Object> map = manager.preparedQueryForMap("SELECT * FROM `sampletable`");
		assertEquals("1번", map.get("name"));
		
		SampleOrder order = new SampleOrder();
		order.sampletable = table;
		order.status = "open";
		order.total = 10000;
		manager.insertModel(order);

		Map<String, Object> orderMap = manager.preparedQueryForMap("SELECT * FROM `sampleorder`");
		assertEquals(table.id, orderMap.get("sampletable_id"));
		assertEquals(10000, orderMap.get("total"));
	}
	
	@Test
	public void modelCreateModel() {
		SampleTable.objects(SampleTable.class).create(kv("name", "3번"), kv("index", 1), kv("x", 2), kv("y", 3));
		
		Map<String, Object> map = manager.preparedQueryForMap("SELECT * FROM `sampletable`");
		assertEquals("3번", map.get("name"));
	}
	
	@Test
	public void modelSave() {
		SampleTable table = new SampleTable();
		table.name = "크허허";
		table.index = 1;
		table.x = 1;
		table.y = 1;
		
		table.save();

		Map<String, Object> map = manager.preparedQueryForMap("SELECT * FROM `sampletable`");
		assertEquals("크허허", map.get("name"));
	}
	
	@Test
	public void querySetDelete() {
		SampleTable.objects(SampleTable.class).create(kv("name", "1번"), kv("index", 1), kv("x", 1), kv("y", 1));
		SampleTable.objects(SampleTable.class).create(kv("name", "2번"), kv("index", 2), kv("x", 2), kv("y", 3));
		SampleTable.objects(SampleTable.class).create(kv("name", "3번"), kv("index", 3), kv("x", 3), kv("y", 3));

		assertEquals(3, SampleTable.objects(SampleTable.class).asList().size());
		
		SampleTable.objects(SampleTable.class).filter("y=?", 1).delete();
		assertEquals(2, SampleTable.objects(SampleTable.class).asList().size());

		SampleTable.objects(SampleTable.class).delete();
		assertEquals(0, SampleTable.objects(SampleTable.class).asList().size());
		
	}
	
	@Test
	public void queryForModel() {
		manager.executeUpdate("INSERT INTO `sampletable`(name, `index`) values(?, ?)", "1번", 5);
		SampleTable table = SampleTable.objects(SampleTable.class).filter("`index`=?", 5).iterator().next();
		assertEquals("1번", table.name);
	}
	
	@Test
	public void createAndQueryRelatedObject() {
		SampleTable table1 = SampleTable.objects(SampleTable.class).create(kv("name", "1번"), kv("index", 1));
		SampleTable table2 = SampleTable.objects(SampleTable.class).create(kv("name", "2번"), kv("index", 2));
		
		table1.foreignObjects(SampleOrder.class).create(kv("status", "open"), kv("total", 4000));
		table2.foreignObjects(SampleOrder.class).create(kv("status", "open"), kv("total", 2000));
		
		SampleOrder order = SampleOrder.objects(SampleOrder.class).filter("total=?", 4000).iterator().next();
		assertEquals(table1.id, order.get("sampletable_id"));
		assertEquals(table1.id, order.sampletable.id);
		
		SampleOrder order2 = table2.foreignObjects(SampleOrder.class).iterator().next();
		assertEquals(table2.id, order2.get("sampletable_id"));
		assertEquals(2000, order2.total);
		
		// TODO NOT POPULATED. same as ORMLite. need lazy loading.
		assertEquals(null, order.sampletable.name);
	}
	
	
	@Test
	public void join() {
		TableGroup group = TableGroup.objects(TableGroup.class).create(kv("name", "1층"));
		SampleTable table = group.foreignObjects(SampleTable.class).create(kv("name", "1번"), kv("index", 1));
		table.foreignObjects(SampleOrder.class).create(kv("status", "open"), kv("total", 4000));
		
		SampleOrder order = SampleOrder.objects(SampleOrder.class).selectRelated(SampleTable.class, "sampletable").filter("total=?", 4000).iterator().next();

		assertEquals(table.id, order.get("sampletable_id"));
		assertEquals(table.id, order.sampletable.id);
		assertEquals("1번", order.sampletable.name);
		
		List<SampleTable> tables = group.foreignObjects(SampleTable.class).outerJoin(SampleOrder.class, "currentOrder", "`currentOrder`.status=?",  "open").asList();
		assertEquals("1번", tables.get(0).name);
		assertEquals(4000, tables.get(0).getModel("currentOrder").getInt("total"));
		
	}
}
