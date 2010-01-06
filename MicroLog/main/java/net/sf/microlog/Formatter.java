package net.sf.microlog;

/**
 * This the interface for all formatters.
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 */
public interface Formatter {

	/**
	 * Format the given message and the Throwable object.
	 * 
	 * @param level
	 *            the logging level
	 * @param message
	 *            the message
	 * @param t
	 *            the exception.
	 * @return a String that is not null.
	 */
	String format(Level level, Object message, Throwable t);

	/**
	 * Set the named property with the specified value.
	 * 
	 * @param property
	 *            the property to set.
	 * @param value
	 *            the value to set.
	 */
	void setProperty(String property, Object value);
}
