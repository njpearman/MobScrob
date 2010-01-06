package net.sf.microlog.appender;

import java.io.PrintStream;

import net.sf.microlog.Level;
import net.sf.microlog.util.PropertiesGetter;

/**
 * An appender for the console, i.e. the logs are appended to System.out.
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 * @since 0.1
 */
public class ConsoleAppender extends AbstractAppender {

	private PrintStream console = System.out;

	/**
	 * Create a ConsoleAppender.
	 */
	public ConsoleAppender() {
	}

	/**
	 * Set the console that the output will be appended to.
	 * 
	 * @param console
	 *            The console to set.
	 */
	public final void setOut(PrintStream console) {
		this.console = console;
	}

	/**
	 * Do the logging.
	 * 
	 * @param level
	 *            the level at which the logging shall be done.
	 * @param message
	 *            the message to log.
	 * @param t
	 *            the exception to log.
	 */
	public void doLog(Level level, Object message, Throwable t) {
		if (logOpen && formatter != null) {
			console.println(formatter.format(level, message, t));
		} else if (formatter == null) {
			System.err.println("Please set a formatter.");
		}
	}

	/**
	 * Do nothing, as this is not applicable for the ConsoleAppender.
	 * 
	 * @see mobscrob.logging.microlog.appender.AbstractAppender#clearLog()
	 */
	public void clearLog() {
		if (console != null) {
			console.flush();
		}
	}

	/**
	 * @see mobscrob.logging.microlog.appender.AbstractAppender#closeLog()
	 */
	public void closeLog() {
		if (console != null) {
			console.flush();
		}
		logOpen = false;
	}

	/**
	 * @see mobscrob.logging.microlog.appender.AbstractAppender#openLog()
	 */
	public void openLog() {
		logOpen = true;
	}

	/**
	 * Get the size of the log. Always returns SIZE_UNDEFINED, since it is not
	 * applicable to the ConsoleAppender.
	 * 
	 * @return the size of the log.
	 */
	public long getLogSize() {
		return SIZE_UNDEFINED;
	}

	/**
	 * @see mobscrob.logging.microlog.appender.AbstractAppender#configure(PropertiesGetter)
	 */
	public synchronized void configure(PropertiesGetter properties) {
		// nothing to do until now
	}

}
