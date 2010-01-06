package net.sf.microlog;

import net.sf.microlog.util.PropertiesGetter;

/**
 * The interface that all Appender classes must implement.
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 */
public interface Appender {

	int SIZE_UNDEFINED = -1;

	/**
	 * Do the logging.
	 * 
	 * @param level
	 *            the logging level
	 * @param message
	 *            the message to log.
	 * @param t
	 *            the exception to log.
	 */
	void doLog(Level level, Object message, Throwable t);

	/**
	 * Clear the log.
	 */
	void clearLog();

	/**
	 * Close the log. The consequence is that the logging is disabled until the
	 * log is opened. The logging could be enabled by calling
	 * <code>openLog()</code>.
	 */
	void closeLog();

	/**
	 * Open the log. The consequence is that the logging is enabled.
	 * 
	 * @throws MicroLogException
	 *             when some internal exception has occurred.
	 */
	void openLog() throws MicroLogException;

	/**
	 * Get the size of the log. This may not be applicable to all types of
	 * appenders.
	 * 
	 * @return the size of the log.
	 */
	long getLogSize();

	/**
	 * Set the formatter to use.
	 * 
	 * @param formatter
	 *            The formatter to set.
	 */
	void setFormatter(Formatter formatter);

	/**
	 * Get the formatter that is in use.
	 * 
	 * @return Returns the formatter.
	 */
	Formatter getFormatter();

	/**
	 * Configure the appender.
	 * 
	 * @param properties
	 *            Properties to configure with
	 */
	void configure(PropertiesGetter properties);

}