/**
 * LogFactory.java
 * @author NJ Pearman
 * @date 27 Oct 2008
 */
package mobscrob.logging;

/**
 * @author Neill
 *
 */
public class LogFactory {

	private static Log logger;
	
	/**
	 * Call once to initialize the logger implementation class.
	 * @param implementation
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws LogConfigurationException 
	 */
	public static final void setImplementation(String implementation) throws ClassNotFoundException, InstantiationException, IllegalAccessException, LogConfigurationException {
		if(implementation == null) {
			System.out.println(LogConfigurationException.NULL_STRING);
			throw new LogConfigurationException(LogConfigurationException.NULL_STRING);
		} else if(LogFactory.logger != null) {
			System.out.println(LogConfigurationException.ALREADY_CONFIGURED);
			throw new LogConfigurationException(LogConfigurationException.ALREADY_CONFIGURED);
		} else {
			Class logClass = Class.forName(implementation);
			logger = (Log) logClass.newInstance();
			System.out.println("INFO: Set logging implementation");
		}
	}
	
	/**
	 * Returns an instance of the Log implementation associated
	 * with the provided Class.
	 * @param clazz
	 * @return
	 */
	public static final Log getLogger(Class clazz) {
		return logger.getInstance(clazz);
	}
}
