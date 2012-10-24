package com.ecolemo.jangorm.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateFormats {
	public static DateFormat plain = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static DateFormat dateToMinute = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static DateFormat dateOnly = new SimpleDateFormat("yyyyMMdd");
	public static DateFormat dateOnlyDashed = new SimpleDateFormat("yyyy-MM-dd");
	public static DateFormat humaneDate = DateFormat.getDateInstance(DateFormat.MEDIUM);
	public static DateFormat androidTimePicker = new SimpleDateFormat("H:m");

}
