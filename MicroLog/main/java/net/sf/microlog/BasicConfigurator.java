/**
 * 
 */
package net.sf.microlog;

import net.sf.microlog.appender.ConsoleAppender;
import net.sf.microlog.format.SimpleFormatter;

/**
 * Configure MicroLog with a basic configuration.
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 * @since 0.5
 */
public class BasicConfigurator {

	/**
	 * Add a <code>ConsoleAppender</code> with a <code>SimpleFormatter</code> to
	 * <code>Logger</code>.
	 */
	public static void configure() {
		Logger logger = Logger.getLogger();
		Appender appender = new ConsoleAppender();
		Formatter formatter = new SimpleFormatter();
		appender.setFormatter(formatter);
		logger.addAppender(appender);
	}
}
