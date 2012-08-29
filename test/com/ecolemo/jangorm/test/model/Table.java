package com.ecolemo.jangorm.test.model;

import com.ecolemo.jangorm.Model;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Table extends Model {
	@DatabaseField(generatedId=true)
	public int id;
	@DatabaseField(foreign=true)
	public TableGroup tablegroup;
	@DatabaseField
	public String name;
	@DatabaseField
	public int index;
	@DatabaseField
	public int x;
	@DatabaseField
	public int y;
	@DatabaseField
	public String type;

}