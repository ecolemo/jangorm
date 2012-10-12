package com.ecolemo.jangorm.test.model;

import com.ecolemo.jangorm.Model;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class TableGroup extends Model {
	@DatabaseField(id=true)
	public String id;
	@DatabaseField 
	public String name;

}
