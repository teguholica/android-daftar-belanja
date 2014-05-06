package com.waiki.daftarbelanja.common;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Helper {
	
	public static String toCurrencyFormat(int total, String langCode, String countryCode) {
        String result = "";
        
        Locale locale = new Locale(langCode, countryCode);
        NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(locale);
        
        result = rupiahFormat.format(total);
        
        return result;
    }
	
	public static Date stringToDate(String value, String format) {
		Date date = null;
		try {
			date = new SimpleDateFormat(format, Locale.getDefault()).parse(value);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static String dateToString(Date value, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
		String dateStr = dateFormat.format(value);
		return dateStr;
	}

}
