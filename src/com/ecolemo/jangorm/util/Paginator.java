package com.ecolemo.jangorm.util;

import java.util.Iterator;

import com.ecolemo.jangorm.Model;
import com.ecolemo.jangorm.QuerySet;

public class Paginator {

	private int pageSize = 20;
	private final long count;
	private final int pageNumber;

	public Paginator(long count, int pageNumber) {
		this.count = count;
		this.pageNumber = pageNumber;
	}
	
	public int getPageNumber() {
		return pageNumber;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	
	public int getNumPages() {
		return (int) (count / pageSize + 1);
	}

	public <T extends Model> Iterator<T> paginate(QuerySet<T> objects) {
		return objects.limit((pageNumber - 1) * pageSize, pageSize).iterator();
	}

}
