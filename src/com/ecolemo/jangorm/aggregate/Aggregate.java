package com.ecolemo.jangorm.aggregate;


public class Aggregate {

	private final String tableName;
	private String name;
	private final String field;
	
	public Aggregate(String tableName, String name, String field) {
		this.tableName = tableName;
		this.name = name;
		this.field = field;
	}
	
	@Override
	public String toString() {
		return name + "(`" + tableName + "`.`" + field + "`) " + tableName + "__" + field + "_" + name;
	}
}
