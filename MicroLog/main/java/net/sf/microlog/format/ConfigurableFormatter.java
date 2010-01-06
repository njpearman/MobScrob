package net.sf.microlog.format;

import java.util.Date;

import net.sf.microlog.Formatter;
import net.sf.microlog.Level;

/**
 * A formatter that could be configured how the formatting shall be. It is not
 * possible to set the order of the output.
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 */
public class ConfigurableFormatter implements Formatter {

	public static final int NO_TIME = 0;

	public static final int DATE_TO_STRING = 1;

	public static final int TIME_IN_MILLIS = 2;

	public static final String PROPERTY_LEVEL = "level";

	public static final String PROPERTY_MESSAGE = "message";

	public static final String PROPERTY_DATE = "date";

	private static final int INITIAL_BUFFER_SIZE = 256;

	private StringBuffer buffer = new StringBuffer(INITIAL_BUFFER_SIZE);

	private int timeFormat = NO_TIME;

	private boolean printLevel = true;

	private boolean printMessage = true;

	private char delimeter = '-';

	/**
	 * Create a ConfigurableFormatter.
	 */
	public ConfigurableFormatter() {
	}

	/**
	 * @see net.sf.microlog.Formatter#format(net.sf.microlog.Level,
	 *      java.lang.Object, java.lang.Throwable)
	 */
	public String format(Level level, Object message, Throwable t) {
		if (buffer.length() > 0) {
			buffer.delete(0, buffer.length());
		}

		if (printLevel && level != null) {
			buffer.append('[');
			buffer.append(level);
			buffer.append(']');
		}

		if (timeFormat == DATE_TO_STRING) {
			buffer.append(delimeter);
			buffer.append((new Date()).toString());
		} else if (timeFormat == TIME_IN_MILLIS) {
			buffer.append(delimeter);
			buffer.append(System.currentTimeMillis());
		}

		if (printMessage && message != null) {
			buffer.append(delimeter);
			buffer.append(message);
		}

		if (t != null) {
			buffer.append(delimeter);
			buffer.append(t);
		}

		return buffer.toString();
	}

	/**
	 * Set the properties for the ConfigurableFormatter. The following are valid
	 * properties: PROPERTY_LEVEL PROPERTY_MESSAGE PROPERTY_DATE
	 * 
	 * @see net.sf.microlog.Formatter#setProperty(java.lang.String,
	 *      java.lang.Object)
	 */
	public void setProperty(String property, Object value) {

		boolean booleanValue = toBoolean(value);

		if (property.compareTo(PROPERTY_LEVEL) == 0) {
			setPrintLevel(booleanValue);
		} else if (property.compareTo(PROPERTY_MESSAGE) == 0) {
			setPrintMessage(booleanValue);
		} else if (property.compareTo(PROPERTY_DATE) == 0) {
			String propertyValue = value.toString();

			if (propertyValue.compareTo("") == 0) {

			}
		}
	}

	/**
	 * @return Returns the printLevel.
	 */
	public final boolean isPrintLevel() {
		return printLevel;
	}

	/**
	 * @param printLevel
	 *            The printLevel to set.
	 */
	public final void setPrintLevel(boolean printLevel) {
		this.printLevel = printLevel;
	}

	/**
	 * @return Returns the timeFormat.
	 */
	public final int getTimeFormat() {
		return timeFormat;
	}

	/**
	 * @param timeFormat
	 *            The timeFormat to set.
	 */
	public final void setTimeFormat(int timeFormat) {
		this.timeFormat = timeFormat;
	}

	/**
	 * @return Returns the printMessage.
	 */
	public final boolean isPrintMessage() {
		return printMessage;
	}

	/**
	 * @param printMessage
	 *            The printMessage to set.
	 */
	public final void setPrintMessage(boolean printMessage) {
		this.printMessage = printMessage;
	}

	/**
	 * @return Returns the delimeter.
	 */
	public final char getDelimeter() {
		return delimeter;
	}

	/**
	 * @param delimeter
	 *            The delimeter to set.
	 */
	public final void setDelimeter(char delimeter) {
		this.delimeter = delimeter;
	}

	/**
	 * Converts the supplied value to a boolean.
	 * 
	 * @param value
	 *            the value.
	 * @return a boolean.
	 */
	private boolean toBoolean(Object value) {
		String valueString = value.toString();
		boolean toBoolean = false;

		if ((valueString.compareTo("true") == 0)
				|| (valueString.compareTo("on") == 0)) {
			toBoolean = true;
		} else if ((valueString.compareTo("false") == 0)
				|| (valueString.compareTo("off") == 0)) {
			toBoolean = false;
		}

		return toBoolean;
	}
}
