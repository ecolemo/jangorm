package com.ecolemo.jangorm.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateFormats {
	public static SimpleDateFormat plainDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat dateOnly = new SimpleDateFormat("yyyyMMdd");
	public static DateFormat humaneDate = DateFormat.getDateInstance(DateFormat.MEDIUM);
}
