package com.ecolemo.jangorm.test.model;

import java.util.Date;

import com.ecolemo.jangorm.Model;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class SampleOrder extends Model {
	@DatabaseField(id=true)
	public String id;
	@DatabaseField(foreign=true)
	public SampleTable sampletable;
	@DatabaseField
	public String status;
	@DatabaseField
	public int total;
	@DatabaseField
	public Date created;
	@DatabaseField
	public Date updated;

}
