/**
 * MicroLog.java
 * 
 * This program is distributed under the terms of the GNU General Public 
 * License
 * Copyright 2008 NJ Pearman
 *
 * This file is part of MobScrob.
 *
 * MobScrob is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MobScrob is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MobScrob.  If not, see <http://www.gnu.org/licenses/>.
 */
package mobscrob.logging;

import java.util.Calendar;
import java.util.Date;

import javax.microedition.midlet.MIDlet;

import net.sf.microlog.Logger;
import net.sf.microlog.util.Properties;

/**
 * @author Neill
 * 
 */
public class MicroLog implements Log {

	private static Logger log = Logger.getLogger();
	private static boolean configured = false;

	private final String className;

	public MicroLog() {
		className = "MicroLog";
	}
	
	private MicroLog(Class clazz) {
		int start = clazz.getName().lastIndexOf('.') + 1;
		this.className = clazz.getName().substring(start,
				clazz.getName().length());
	}

	/**
	 * Configures the underlying Microlog Logger
	 * 
	 * @param midlet
	 */
	public static void configure(MIDlet midlet) {
		Properties props = new Properties(midlet);
		log.configure(props);
		configured = true;
	}

	public Log getInstance(Class clazz) {
		if (!configured) {
			System.out.println("ERROR: Logger not configured");
		}
		return new MicroLog(clazz);
	}

	public String getClassname() {
		return className;
	}

	/* (non-Javadoc)
	 * @see mobscrob.logging.Log#info(java.lang.String, java.lang.String)
	 */
	public void info(String methodName, String msg) {
		if (log.isInfoEnabled()) {
			log.info(buildMessage(methodName, msg));
		}
	}

	/* (non-Javadoc)
	 * @see mobscrob.logging.Log#info(java.lang.String, java.lang.String, java.lang.Throwable)
	 */
	public void info(String methodName, String msg, Throwable t) {
		if (log.isInfoEnabled()) {
			log.info(buildMessage(methodName, msg), t);
		}
	}

	/* (non-Javadoc)
	 * @see mobscrob.logging.Log#trace(java.lang.String, java.lang.String)
	 */
	public void trace(String methodName, String msg) {
		if (log.isTraceEnabled()) {
			log.trace(buildMessage(methodName, msg));
		}
	}

	/* (non-Javadoc)
	 * @see mobscrob.logging.Log#trace(java.lang.String, java.lang.String, java.lang.Throwable)
	 */
	public void trace(String methodName, String msg, Throwable t) {
		if (log.isTraceEnabled()) {
			log.trace(buildMessage(methodName, msg), t);
		}
	}

	/* (non-Javadoc)
	 * @see mobscrob.logging.Log#debug(java.lang.String, java.lang.String)
	 */
	public void debug(String methodName, String msg) {
		if (log.isDebugEnabled()) {
			log.debug(buildMessage(methodName, msg));
		}
	}

	/* (non-Javadoc)
	 * @see mobscrob.logging.Log#debug(java.lang.String, java.lang.String, java.lang.Throwable)
	 */
	public void debug(String methodName, String msg, Throwable t) {
		if (log.isDebugEnabled()) {
			log.debug(buildMessage(methodName, msg), t);
		}
	}

	/* (non-Javadoc)
	 * @see mobscrob.logging.Log#error(java.lang.String, java.lang.String)
	 */
	public void error(String methodName, String msg) {
		if (log.isErrorEnabled()) {
			log.error(buildMessage(methodName, msg));
		}
	}

	/* (non-Javadoc)
	 * @see mobscrob.logging.Log#error(java.lang.String, java.lang.String, java.lang.Throwable)
	 */
	public void error(String methodName, String msg, Throwable t) {
		if (log.isErrorEnabled()) {
			log.error(buildMessage(methodName, msg), t);
		}
	}

	/* (non-Javadoc)
	 * @see mobscrob.logging.Log#fatal(java.lang.String, java.lang.String, java.lang.Throwable)
	 */
	public void fatal(String methodName, String msg, Throwable t) {
		if (log.isFatalEnabled()) {
			log.error(buildMessage(methodName, msg), t);
		}
	}

	/* (non-Javadoc)
	 * @see mobscrob.logging.Log#warn(java.lang.String, java.lang.String)
	 */
	public void warn(String methodName, String msg) {
		if (log.isWarnEnabled()) {
			log.warn(buildMessage(methodName, msg));
		}
	}

	/* (non-Javadoc)
	 * @see mobscrob.logging.Log#warn(java.lang.String, java.lang.String, java.lang.Throwable)
	 */
	public void warn(String methodName, String msg, Throwable t) {
		if (log.isWarnEnabled()) {
			log.warn(buildMessage(methodName, msg), t);
		}
	}
	
	public static void close() {
		log.closeLog();
	}

	private String buildMessage(String methodName, String msg) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());

		StringBuffer buf = new StringBuffer();
		buf.append(" [").append(cal.get(Calendar.YEAR)).append('-').append(
				cal.get(Calendar.MONTH)).append('-').append(
				cal.get(Calendar.DAY_OF_MONTH)).append(' ').append(
				cal.get(Calendar.HOUR_OF_DAY)).append(':').append(
				cal.get(Calendar.MINUTE)).append(':').append(
				cal.get(Calendar.SECOND)).append('.').append(
				cal.get(Calendar.MILLISECOND)).append("] [").append(className)
				.append(".").append(methodName).append("] ").append(msg);

		return buf.toString();
	}

	public static class LoggingException extends Exception {
		public LoggingException(String msg) {
			super(msg);
		}
	}
}
