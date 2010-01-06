/**
 * LogConfigurationException.java
 * @author NJ Pearman
 * @date 28 Oct 2008
 */
package mobscrob.logging;

/**
 * @author Neill
 *
 */
public class LogConfigurationException extends Exception {
	public static final String NULL_STRING = "ERROR: null String passed as logging class name";
	public static final String ALREADY_CONFIGURED = "ERROR: Already have a logging class set";
	public LogConfigurationException(String msg) {
		super(msg);
	}
}
