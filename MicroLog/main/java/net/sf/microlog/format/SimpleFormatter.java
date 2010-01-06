package net.sf.microlog.format;

import net.sf.microlog.Formatter;
import net.sf.microlog.Level;

/**
 * A simple formatter that only outputs the level, the message and the Throwable
 * object.
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 */
public final class SimpleFormatter implements Formatter {

	private static final int INITIAL_BUFFER_SIZE = 256;

	private StringBuffer buffer = new StringBuffer(INITIAL_BUFFER_SIZE);

	private char delimeter = '-';

	/**
	 * Create a SimpleFormatter.
	 */
	public SimpleFormatter() {
	}

	/**
	 * Get the delimeter that is used between the different fields when logging.
	 * 
	 * @return the delimeter
	 */
	public char getDelimeter() {
		return delimeter;
	}

	/**
	 * Set the delimeter that is used between the different fields when logging.
	 * 
	 * @param delimeter
	 *            the delimeter to set
	 */
	public void setDelimeter(char delimeter) {
		this.delimeter = delimeter;
	}

	/**
	 * Format the given message and the Throwable object. The format is
	 * <code>{Level}{-message.toString()}{-t}</code>
	 * 
	 * @param level
	 *            the logging level. If null, it is not appended to the String.
	 * @param message
	 *            the message. If null, it is not appended to the String.
	 * @param t
	 *            the exception. If null, it is not appended to the String.
	 * @return a String that is not null.
	 */
	public String format(Level level, Object message, Throwable t) {
		if (buffer.length() > 0) {
			buffer.delete(0, buffer.length());
		}

		if (level != null) {
			buffer.append('[');
			buffer.append(level);
			buffer.append(']');
		}

		if (message != null) {
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
	 * Set the named property with the specified value. This is part og the
	 * Formatter interface, but is ignored by the SimpleFormatter.s
	 * 
	 * @param property
	 *            the property to set.
	 * @param value
	 *            the value to set.
	 */
	public void setProperty(String property, Object value) {
		// Do nothing
	}
}
