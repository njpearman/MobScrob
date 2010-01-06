package net.sf.microlog;

import java.util.Enumeration;
import java.util.Vector;
import net.sf.microlog.util.PropertiesGetter;

/**
 * The Logger class is used for logging.
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 * @author Darius Katz
 * @author Karsten Ohme
 */
public final class Logger {

	public static final String LOG_LEVEL_STRING = "microlog.level";

	public static final String APPENDER_STRING = "microlog.appender";

	public static final String FORMATTER_STRING = "microlog.formatter";

	public static final String PROPERTY_DELIMETER = ";";

	private static final Logger LOGGER = new Logger();

	final Vector appenderList = new Vector();

	Level logLevel = Level.FATAL;

	/**
	 * Create a Logger object. This is made private to prevent the user from
	 * creating a Logger object through a constructor.
	 */
	private Logger() {
		openLog();
	}

	/**
	 * Get the Logger instance.
	 * 
	 * @return the Logger.
	 */
	public static Logger getLogger() {
		return LOGGER;
	}

	/**
	 * Reset the Logger, i.e. remove all appenders and set the log level to
	 * Level.ERROR.
	 */
	public static synchronized void resetLogger() {
		LOGGER.appenderList.removeAllElements();
		LOGGER.logLevel = Level.ERROR;
	}

	/**
	 * Get the log level.
	 * 
	 * @return Returns the logLevel.
	 */
	public Level getLogLevel() {
		return logLevel;
	}

	/**
	 * Set the log level.
	 * 
	 * @param logLevel
	 *            The logLevel to set.
	 */
	public void setLogLevel(Level logLevel) {
		this.logLevel = logLevel;
	}

	/**
	 * Configure the logger.
	 * 
	 * @param properties
	 *            Properties to configure with
	 */
	public void configure(PropertiesGetter properties) {
		if (properties != null) {
			configureLogLevel(properties.getString(LOG_LEVEL_STRING));
			configureAppender(properties);
			configureFormatter(properties);
		}
	}

	/**
	 * Configure the log level.
	 * 
	 * @param logLevel
	 *            The logLevel to set.
	 */
	private void configureLogLevel(String logLevel) {
		if (logLevel != null) {
			if (logLevel.compareTo(Level.FATAL_STRING) == 0) {
				setLogLevel(Level.FATAL);
			} else if (logLevel.compareTo(Level.ERROR_STRING) == 0) {
				setLogLevel(Level.ERROR);
			} else if (logLevel.compareTo(Level.WARN_STRING) == 0) {
				setLogLevel(Level.WARN);
			} else if (logLevel.compareTo(Level.INFO_STRING) == 0) {
				setLogLevel(Level.INFO);
			} else if (logLevel.compareTo(Level.DEBUG_STRING) == 0) {
				setLogLevel(Level.DEBUG);
			} else if (logLevel.compareTo(Level.TRACE_STRING) == 0) {
				setLogLevel(Level.TRACE);
			}
		}
	}

	/**
	 * Configure the appender for the specified logger.
	 * 
	 * @param appenderString
	 *            the <code>String</code> to use for configuring the
	 *            <code>Appender</code>.
	 */
	private void configureAppender(PropertiesGetter properties) {
		// remove all appenders and close there log
		removeAllAppenders();
		String appenderString = properties.getString(APPENDER_STRING);
		if ((appenderString != null) && (appenderString.length() > 0)) {
			try {
				int delimiterPos = appenderString.indexOf(PROPERTY_DELIMETER);
				if (delimiterPos == -1) {
					// There is only one appender
					Class appenderClass = Class.forName(appenderString);
					Appender appender = (Appender) appenderClass.newInstance();
					appender.configure(properties);
					appender.openLog();
					addAppender(appender);
				} else {
					// Loop through all the Appenders in appenderString
					int startPos = 0;
					int endPos;
					boolean finished = false;
					do {
						// find out if and where the next string is
						delimiterPos = appenderString.indexOf(
								PROPERTY_DELIMETER, startPos);
						if (delimiterPos == -1) {
							// this is the last appender
							endPos = appenderString.length();
							finished = true;
						} else {
							// has a delimiter at the end
							endPos = delimiterPos;
						}

						// get the appender string
						String singleAppenderString = appenderString.substring(
								startPos, endPos);

						// Advance the start position
						startPos = endPos + 1;

						// create the appender
						if (singleAppenderString.length() > 0) {
							Class appenderClass = Class
									.forName(singleAppenderString);

							Appender appender = (Appender) appenderClass
									.newInstance();
							appender.configure(properties);
							appender.openLog();
							addAppender(appender);
						}
					} while (!finished);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (MicroLogException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Configure the formatter for the specified logger.
	 * 
	 * @param formatterString
	 *            the <code>String</code> to use for configuring the
	 *            <code>Formatter</code>.
	 */
	private void configureFormatter(PropertiesGetter properties) {
		String formatterString = properties.getString(FORMATTER_STRING);
		if (formatterString != null) {
			try {
				String className = formatterString;
				if (formatterString.indexOf(PROPERTY_DELIMETER) != -1) {
					className = formatterString.substring(0, formatterString
							.indexOf(PROPERTY_DELIMETER));
				}
				Class formatterClass = Class.forName(className);
				Formatter formatter = (Formatter) formatterClass.newInstance();
				int nofAppenders = getNumberOfAppenders();
				for (int index = 0; index < nofAppenders; index++) {
					Appender appender = getAppender(index);
					if (appender != null) {
						configureFormatterProperty(formatterString, formatter);
						appender.setFormatter(formatter);
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Clear the log. The call is forwarded to the appender. For some appenders
	 * this is ignored.
	 */
	public void clearLog() {
		int nofAppenders = appenderList.size();
		for (int index = 0; index < nofAppenders; index++) {
			Appender appender = (Appender) appenderList.elementAt(index);
			appender.clearLog();
		}
	}

	/**
	 * Close the log. From this point on, no logging is done.
	 */
	public void closeLog() {
		int nofAppenders = appenderList.size();
		for (int index = 0; index < nofAppenders; index++) {
			Appender appender = (Appender) appenderList.elementAt(index);
			appender.closeLog();
		}
	}

	/**
	 * Open the log. The logging is now turned on.
	 */
	public void openLog() {
		try {
			int nofAppenders = appenderList.size();
			for (int index = 0; index < nofAppenders; index++) {
				Appender appender = (Appender) appenderList.elementAt(index);
				appender.openLog();
			}

		} catch (MicroLogException e) {
			System.err.println("Failed to open appender.");
		}
	}

	/**
	 * Add the specified appender to the output appenders.
	 * 
	 * @param appender
	 *            the appender to add.
	 */
	public void addAppender(Appender appender) {
		if (appender == null) {
			throw new IllegalArgumentException(
					"Appender not allowed to be null");
		} else if (!appenderList.contains(appender)) {
			appenderList.addElement(appender);
			try {
				appender.openLog();
			} catch (MicroLogException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Remove the specified appender from the appender list.
	 * 
	 * @param appender
	 *            the appender to remove.
	 */
	public void removeAppender(Appender appender) {
		appender.closeLog();
		appenderList.removeElement(appender);
	}

	/**
	 * Remove all the appenders.
	 * 
	 */
	public void removeAllAppenders() {
		for (Enumeration e = appenderList.elements(); e.hasMoreElements();) {
			Appender appender = (Appender) e.nextElement();
			appender.closeLog();
		}
		appenderList.removeAllElements();
	}

	/**
	 * Get the number of active appenders.
	 * 
	 * @return the number of appenders.
	 */
	public int getNumberOfAppenders() {
		return appenderList.size();
	}

	/**
	 * Get the specified appender, starting at index = 0.
	 * 
	 * @param index
	 *            the index of the appender.
	 * @return the appender.
	 */
	public Appender getAppender(int index) {
		return (Appender) appenderList.elementAt(index);
	}

	/**
	 * MicroLog the message at the specified level.
	 * 
	 * @param level
	 *            the level to log at.
	 * @param message
	 *            the object to log.
	 */
	public void log(Level level, Object message) {
		this.log(level, message, null);
	}

	/**
	 * MicroLog the message and the Throwable object at the specified level.
	 * 
	 * @param level
	 *            the log level
	 * @param message
	 *            the message
	 * @param e
	 *            the exception.
	 */
	public void log(Level level, Object message, Throwable e) {
		if (checkLogLevel(level)) {
			int nofAppenders = appenderList.size();
			for (int index = 0; index < nofAppenders; index++) {
				Appender appender = (Appender) appenderList.elementAt(index);
				appender.doLog(level, message, e);
			}
		}
	}

	/**
	 * Is this LOGGER enabled for TRACE level?
	 * 
	 * @return true if logging is enabled.
	 */
	public boolean isTraceEnabled() {
		return logLevel.toInt() <= Level.TRACE_INT;
	}

	/**
	 * MicroLog the message at TRACE level.
	 * 
	 * @param message
	 *            the message to log.
	 */
	public void trace(Object message) {
		log(Level.TRACE, message.toString());
	}

	/**
	 * MicroLog the message and the Throwable object at TRACE level.
	 * 
	 * @param message
	 *            the message to log.
	 * @param t
	 *            the Throwable object to log.
	 */
	public void trace(Object message, Throwable t) {
		log(Level.TRACE, message, t);
	}

	/**
	 * Is this LOGGER enabled for DEBUG level?
	 * 
	 * @return true if logging is enabled.
	 */
	public boolean isDebugEnabled() {
		return logLevel.toInt() <= Level.DEBUG_INT;
	}

	/**
	 * MicroLog the message at DEBUG level.
	 * 
	 * @param message
	 *            the message to log.
	 */
	public void debug(Object message) {
		log(Level.DEBUG, message.toString());
	}

	/**
	 * MicroLog the message and the Throwable object at DEBUG level.
	 * 
	 * @param message
	 *            the message to log.
	 * @param t
	 *            the Throwable object to log.
	 */
	public void debug(Object message, Throwable t) {
		log(Level.DEBUG, message, t);
	}

	/**
	 * Is this LOGGER enabled for INFO level.
	 * 
	 * @return true if the INFO level is enabled.
	 */
	public boolean isInfoEnabled() {
		return logLevel.toInt() <= Level.INFO_INT;
	}

	/**
	 * MicroLog the specified message at INFO level.
	 * 
	 * @param message
	 *            the object to log.
	 */
	public void info(Object message) {
		log(Level.INFO, message);
	}

	/**
	 * MicroLog the specified message and the Throwable at INFO level.
	 * 
	 * @param message
	 *            the object to log.
	 * @param t
	 *            the <code>Throwable</code> to log.
	 */
	public void info(Object message, Throwable t) {
		log(Level.INFO, message, t);
	}

	/**
	 * Is this LOGGER enabled for WARN level?
	 * 
	 * @return true if WARN level is enabled.
	 */
	public boolean isWarnEnabled() {
		return logLevel.toInt() <= Level.WARN_INT;
	}

	/**
	 * MicroLog the specified message at WARN level.
	 * 
	 * @param message
	 *            the object to log.
	 */
	public void warn(Object message) {
		log(Level.WARN, message);
	}

	/**
	 * MicroLog the specified message and Throwable object at WARN level.
	 * 
	 * @param message
	 *            the object to log.
	 * @param t
	 *            the <code>Throwable</code> to log.
	 */
	public void warn(Object message, Throwable t) {
		log(Level.WARN, message, t);
	}

	/**
	 * Is this LOGGER enabled for ERROR level?
	 * 
	 * @return true if the ERROR level is enabled.
	 */
	public boolean isErrorEnabled() {
		return logLevel.toInt() <= Level.ERROR_INT;
	}

	/**
	 * MicroLog the specified message at ERROR level.
	 * 
	 * @param message
	 *            the object to log.
	 */
	public void error(Object message) {
		log(Level.ERROR, message);
	}

	/**
	 * MicroLog the specified message and Throwable object at ERROR level.
	 * 
	 * @param message
	 *            the object to log.
	 * @param t
	 *            the <code>Throwable</code> to log.
	 */
	public void error(Object message, Throwable t) {
		log(Level.ERROR, message, t);
	}

	/**
	 * Is this LOGGER enabled for FATAL level?
	 * 
	 * @return true if the FATAL level is enabled.
	 */
	public boolean isFatalEnabled() {
		return logLevel.toInt() <= Level.FATAL_INT;
	}

	/**
	 * MicroLog the specified message at FATAL level.
	 * 
	 * @param message
	 *            the object to log.
	 */
	public void fatal(Object message) {
		log(Level.FATAL, message);
	}

	/**
	 * MicroLog the specified message and Throwable object at FATAL level.
	 * 
	 * @param message
	 *            the object to log.
	 * @param t
	 *            the <code>Throwable</code> to log.
	 */
	public void fatal(Object message, Throwable t) {
		log(Level.FATAL, message, t);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(super.toString());
		stringBuffer.append('[');

		int nofAppenders = appenderList.size();
		for (int index = 0; index < nofAppenders; index++) {
			Appender appender = (Appender) appenderList.elementAt(index);
			stringBuffer.append(appender);
			stringBuffer.append(';');
		}
		stringBuffer.append(']');
		return stringBuffer.toString();
	}

	/**
	 * Check if the level is enabled for logging.
	 * 
	 * @param level
	 *            the level to check.
	 * @return true if logging is enabled for this level.
	 */
	private boolean checkLogLevel(Level level) {
		boolean logLevelOk = false;

		if (level != null) {
			logLevelOk = logLevel.toInt() <= level.toInt();
		}

		return logLevelOk;
	}

	/**
	 * Configure the properties of the formatter.
	 * 
	 * @param formatterString
	 *            the String that is used for configuration.
	 * @param formatter
	 *            the Formatter that shall be configured.
	 */
	private void configureFormatterProperty(String formatterString,
			Formatter formatter) {

		int stringIndex = formatterString.indexOf(PROPERTY_DELIMETER);
		int equalsIndex = formatterString.indexOf("=", stringIndex);

		while (stringIndex != -1 && equalsIndex != -1) {

			String propertyName = formatterString.substring(stringIndex + 1,
					equalsIndex);
			stringIndex = formatterString.indexOf(PROPERTY_DELIMETER,
					stringIndex + 1);

			String propertyValue = null;
			if (stringIndex != -1) {
				propertyValue = formatterString.substring(equalsIndex + 1,
						stringIndex);
				formatter.setProperty(propertyName, propertyValue);
			} else {
				propertyValue = formatterString.substring(equalsIndex + 1,
						formatterString.length());
			}

			if (propertyName != null && propertyValue != null) {
				formatter.setProperty(propertyName, propertyValue);
			}

			equalsIndex = formatterString.indexOf("=", stringIndex);
		}
	}

}
