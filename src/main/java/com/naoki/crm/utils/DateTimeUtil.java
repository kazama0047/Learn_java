package com.naoki.crm.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {

	/**获取当前时间(年月日 时分秒)**/
	public static String getSysTime(){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date date = new Date();
		String dateStr = sdf.format(date);
		
		return dateStr;
		
	}
	
}
