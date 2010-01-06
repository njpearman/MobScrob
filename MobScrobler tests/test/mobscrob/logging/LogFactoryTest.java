/**
 * LogFactoryTest.java
 * @author NJ Pearman
 * @date 28 Oct 2008
 */
package mobscrob.logging;

import org.jmock.MockObjectTestCase;

/**
 * @author Neill
 *
 */
public class LogFactoryTest extends MockObjectTestCase {

	/**
	 * Test method for {@link mobscrob.logging.LogFactory#setImplementation(java.lang.String)}.
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @throws LogConfigurationException 
	 */
	public void testSetImplementation() throws ClassNotFoundException, InstantiationException, IllegalAccessException, LogConfigurationException {
		// try null
		try {
			LogFactory.setImplementation(null);
			fail("LogConfigurationException should be thrown for null implementation String");
		} catch(LogConfigurationException e) {
			assertEquals(LogConfigurationException.NULL_STRING, e.getMessage());
		}
		
		// try non-existent class name
		try {
			LogFactory.setImplementation("some.imaginary.class.name");
			fail("ClassNotFoundException should be thrown for non-existent class name");
		} catch(ClassNotFoundException e) {}

		// try valid class name
		LogFactory.setImplementation("mobscrob.logging.MicroLog");
		
		// try setting log class again
		try {
			LogFactory.setImplementation("mobscrob.logging.MicroLog");
			fail("LogConfigurationException should be thrown for when Log implementation has already been configured");
		} catch(LogConfigurationException e) {
			assertEquals(LogConfigurationException.ALREADY_CONFIGURED, e.getMessage());
		}
	}

}
