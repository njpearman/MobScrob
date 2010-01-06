package net.sf.microlog.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * A utility class for MicroLog.
 * 
 * @author Darius Katz
 */
public class Util {

	/**
	 * Creates a new instance of Util
	 */
	public Util() {
	}

	/**
	 * Convert a <code>long</code> timestamp to a string
	 * 
	 * @param timestamp
	 *            the timestamp.
	 * @return String the formatted timestamp as a string.
	 */
	public static final String toTimestampString(long timestamp) {
		StringBuffer ts = new StringBuffer();
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		calendar.setTime(new Date(timestamp));

		// year
		int temp = calendar.get(Calendar.YEAR);
		ts.append(temp);

		// month
		temp = calendar.get(Calendar.MONTH);
		if (temp < 10) {
			ts.append("-0");
		} else {
			ts.append("-");
		}
		ts.append(temp);

		// day
		temp = calendar.get(Calendar.DAY_OF_MONTH);
		if (temp < 10) {
			ts.append("-0");
		} else {
			ts.append("-");
		}
		ts.append(temp);

		// hour
		temp = calendar.get(Calendar.HOUR_OF_DAY);
		if (temp < 10) {
			ts.append(" 0");
		} else {
			ts.append(" ");
		}
		ts.append(temp);

		// minute
		temp = calendar.get(Calendar.MINUTE);
		if (temp < 10) {
			ts.append(":0");
		} else {
			ts.append(":");
		}
		ts.append(temp);

		// second
		temp = calendar.get(Calendar.SECOND);
		if (temp < 10) {
			ts.append(":0");
		} else {
			ts.append(":");
		}
		ts.append(temp);

		// millisecond
		temp = calendar.get(Calendar.MILLISECOND);
		ts.append(".");
		ts.append(temp);

		return ts.toString();
	}

}
