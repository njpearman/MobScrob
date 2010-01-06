package mobscrob.logging;

/**
 * Interface for logging
 * @author Neill
 *
 */
public interface Log {

	/**
	 * Returns a new instance of the Log implementation.
	 * @param clazz
	 * @return
	 */
	Log getInstance(Class clazz);
	
	/**
	 * Returns the class name that the Log instance is logging information
	 * about.
	 * @return
	 */
	String getClassname();
	
	/**
	 * @param methodName
	 * @param msg
	 */
	void info(String methodName, String msg);

	/**
	 * @param methodName
	 * @param msg
	 * @param t
	 */
	void info(String methodName, String msg, Throwable t);

	/**
	 * @param methodName
	 * @param msg
	 */
	void trace(String methodName, String msg);

	/**
	 * @param methodName
	 * @param msg
	 * @param t
	 */
	void trace(String methodName, String msg, Throwable t);

	/**
	 * @param methodName
	 * @param msg
	 */
	void debug(String methodName, String msg);

	/**
	 * @param methodName
	 * @param msg
	 * @param t
	 */
	void debug(String methodName, String msg, Throwable t);

	/**
	 * @param methodName
	 * @param msg
	 */
	void error(String methodName, String msg);

	/**
	 * @param methodName
	 * @param msg
	 * @param t
	 */
	void error(String methodName, String msg, Throwable t);

	/**
	 * @param methodName
	 * @param msg
	 * @param t
	 */
	void fatal(String methodName, String msg, Throwable t);

	/**
	 * @param methodName
	 * @param msg
	 */
	void warn(String methodName, String msg);

	/**
	 * @param methodName
	 * @param msg
	 * @param t
	 */
	void warn(String methodName, String msg, Throwable t);

}