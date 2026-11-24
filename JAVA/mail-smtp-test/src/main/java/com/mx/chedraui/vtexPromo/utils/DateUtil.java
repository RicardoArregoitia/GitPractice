package com.mx.chedraui.vtexPromo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static String getActuallyDate(String formatDate) {
		SimpleDateFormat formatter = new SimpleDateFormat(formatDate);
		Date date = new Date(System.currentTimeMillis());
		return formatter.format(date) + " - ";
	}

}
