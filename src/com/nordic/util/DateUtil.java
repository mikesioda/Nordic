package com.nordic.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.swing.JFormattedTextField.AbstractFormatter;

/**
 * This utility holds convenience methods for working with Date &
 * Calendar objects.  This is also defined as the formatter used in the GUI
 * fields.
 * 
 * @author Mike Sioda
 *
 */
public class DateUtil extends AbstractFormatter {

	// "yyyy/MM/dd kk:mm:ss:SSS"
	private static final long serialVersionUID = 1L;
	private static final String datePattern = "yyyy/MM/dd kk:mm:ss";
	//private static final String datePattern = "MM/dd/yyyy";
	private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

	/**
	 * Convert date picker string value to object to hold actual
	 * date value in date model.
	 */
	@Override
	public Object stringToValue(String text) throws ParseException {
		return dateFormatter.parseObject(text);
	}

	
	/**
	 * Convert date picker object value to string to display on GUI.
	 */
	@Override
	public String valueToString(Object value) throws ParseException {
		if (value != null) {
			Calendar cal = (Calendar) value;
			return dateFormatter.format(cal.getTime());
		}

		return "";
	}

	/**
	 * Returns the date values as an Integer.
	 * 
	 * @param d - Date
	 * @return Integer date
	 */
	public static Integer getDateAsInteger(Date d) {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(d);
		String year = new Integer(c.get(GregorianCalendar.YEAR)).toString();
		String twoDigitMonth = new Integer(c.get(GregorianCalendar.MONTH) + 1)
				.toString();
		String twoDigitDate = new Integer(c.get(Calendar.DATE)).toString();

		if (twoDigitMonth.length() == 1) {
			twoDigitMonth = "0" + twoDigitMonth;
		}

		if (twoDigitDate.length() == 1) {
			twoDigitDate = "0" + twoDigitDate;
		}

		return new Integer(year + twoDigitMonth + twoDigitDate);
	}

	/**
	 * Returns date string using the given format.
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String getDateString(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
	
	/**
	 * Returns the given date formatted as MM/dd/yyyy.
	 * 
	 * @param date - Integer
	 * @return date - formatted String
	 */
	public static String getDateString(Integer date) {
		if(date==null) return "";
		if(date.toString().trim().length()<8) return date.toString().trim();
		String dateString = date.toString();
		return dateString.substring(4, 6) + "/" + dateString.substring(6)
				+ "/" + dateString.substring(0, 4);
	}
	
	/**
	 * Gets todays date in GMT.
	 * 
	 * @return GMT date.
	 */
	public static Date getGmtDate() {

		Calendar c = Calendar.getInstance();

		TimeZone z = c.getTimeZone();
		int offset = z.getRawOffset();
		int offsetHrs = offset / 1000 / 60 / 60;
		int offsetMins = offset / 1000 / 60 % 60;

		LogUtil.debug("Timezone offset (hours) = " + offsetHrs);
		LogUtil.debug("Timezone offset (mins) = " + offsetMins);

		c.add(Calendar.HOUR_OF_DAY, (-offsetHrs));
		c.add(Calendar.MINUTE, (-offsetMins));

		return c.getTime();
	}

	/**
	 * Returns formatted date (today-diff).
	 * 
	 * @param diff
	 * @return String - formatted date
	 */
	public static String getOffsetDate(int diff) {
		Calendar c = new GregorianCalendar();
		c.setTime(getGmtDate());
		c.add(Calendar.DATE, diff * -1);
		String year = new Integer(c.get(Calendar.YEAR)).toString();
		String twoDigitMonth = new Integer(c.get(Calendar.MONTH) + 1)
				.toString();
		String twoDigitDate = new Integer(c.get(Calendar.DATE)).toString();

		if (twoDigitMonth.length() == 1) {
			twoDigitMonth = "0" + twoDigitMonth;
		}

		if (twoDigitDate.length() == 1) {
			twoDigitDate = "0" + twoDigitDate;
		}

		String offsetDate = year + twoDigitMonth + twoDigitDate;
		LogUtil.info("FEED SKIPPING DATE = " + offsetDate);
		return offsetDate;
	}

}
