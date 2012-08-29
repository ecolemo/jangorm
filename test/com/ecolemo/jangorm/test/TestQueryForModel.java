package com.ecolemo.jangorm.test;

import static com.ecolemo.jangorm.util.KeyValueEntry.kv;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.ecolemo.jangorm.manager.ModelManager;
import com.ecolemo.jangorm.manager.SQLiteModelManager;
import com.ecolemo.jangorm.test.model.Order;
import com.ecolemo.jangorm.test.model.Table;
import com.ecolemo.jangorm.test.model.TableGroup;

public class TestQueryForModel {

	private ModelManager manager;

	@Before
	public void setUp() {
		manager = new SQLiteModelManager("testdb.sqlite3");
		manager.dropTables(TableGroup.class, Table.class, Order.class);
		manager.createTables(TableGroup.class, Table.class, Order.class);
		
		ModelManager.setDefaultModelManager(manager);
	}

	@Test
	public void managerCreateModel() {
		Table table = new Table();
		table.name = "1번";
		table.index = 1;
		table.x = 1;
		table.y = 1;
		
		manager.insertModel(table);
		
		Map<String, Object> map = manager.preparedQueryForMap("SELECT * FROM `table`");
		assertEquals("1번", map.get("name"));
		
		Order order = new Order();
		order.table = table;
		order.status = "open";
		order.total = 10000;
		manager.insertModel(order);

		Map<String, Object> orderMap = manager.preparedQueryForMap("SELECT * FROM `order`");
		assertEquals(table.id, orderMap.get("table_id"));
		assertEquals(10000, orderMap.get("total"));
	}
	
	@Test
	public void modelCreateModel() {
		Table.objects(Table.class).create(kv("name", "3번"), kv("index", 1), kv("x", 2), kv("y", 3));
		
		Map<String, Object> map = manager.preparedQueryForMap("SELECT * FROM `table`");
		assertEquals("3번", map.get("name"));
	}
	
	@Test
	public void modelSave() {
		Table table = new Table();
		table.name = "크허허";
		table.index = 1;
		table.x = 1;
		table.y = 1;
		
		table.save();

		Map<String, Object> map = manager.preparedQueryForMap("SELECT * FROM `table`");
		assertEquals("크허허", map.get("name"));
	}
	
	@Test
	public void querySetDelete() {
		Table.objects(Table.class).create(kv("name", "1번"), kv("index", 1), kv("x", 1), kv("y", 1));
		Table.objects(Table.class).create(kv("name", "2번"), kv("index", 2), kv("x", 2), kv("y", 3));
		Table.objects(Table.class).create(kv("name", "3번"), kv("index", 3), kv("x", 3), kv("y", 3));

		assertEquals(3, Table.objects(Table.class).asList().size());
		
		Table.objects(Table.class).filter("y=?", 1).delete();
		assertEquals(2, Table.objects(Table.class).asList().size());

		Table.objects(Table.class).delete();
		assertEquals(0, Table.objects(Table.class).asList().size());
		
	}
	
	@Test
	public void queryForModel() {
		manager.executeUpdate("INSERT INTO `TABLE`(name, `index`) values(?, ?)", "1번", 5);
		Table table = Table.objects(Table.class).filter("`index`=?", 5).iterator().next();
		assertEquals("1번", table.name);
	}
	
	@Test
	public void createAndQueryRelatedObject() {
		Table table1 = Table.objects(Table.class).create(kv("name", "1번"), kv("index", 1));
		Table table2 = Table.objects(Table.class).create(kv("name", "2번"), kv("index", 2));
		
		table1.foreignObjects(Order.class).create(kv("status", "open"), kv("total", 4000));
		table2.foreignObjects(Order.class).create(kv("status", "open"), kv("total", 2000));
		
		Order order = Order.objects(Order.class).filter("total=?", 4000).iterator().next();
		assertEquals(table1.id, order.get("table_id"));
		assertEquals(table1.id, order.table.id);
		
		Order order2 = table2.foreignObjects(Order.class).iterator().next();
		assertEquals(table2.id, order2.get("table_id"));
		assertEquals(2000, order2.total);
		
		// TODO NOT POPULATED. same as ORMLite. need lazy loading.
		assertEquals(null, order.table.name);
	}
	
	
	@Test
	public void join() {
		TableGroup group = TableGroup.objects(TableGroup.class).create(kv("name", "1층"));
		Table table = group.foreignObjects(Table.class).create(kv("name", "1번"), kv("index", 1));
		table.foreignObjects(Order.class).create(kv("status", "open"), kv("total", 4000));
		
		Order order = Order.objects(Order.class).selectRelated(Table.class, "table").filter("total=?", 4000).iterator().next();

		assertEquals(table.id, order.get("table_id"));
		assertEquals(table.id, order.table.id);
		assertEquals("1번", order.table.name);
		
		List<Table> tables = group.foreignObjects(Table.class).outerJoin(Order.class, "currentOrder", "`currentOrder`.status=?",  "open").asList();
		assertEquals("1번", tables.get(0).name);
		assertEquals(4000, tables.get(0).getModel("currentOrder").getInt("total"));
		
	}
}
