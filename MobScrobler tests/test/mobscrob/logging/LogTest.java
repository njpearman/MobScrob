/**
 * 
 */
package mobscrob.logging;

import junit.framework.TestCase;

/**
 * @author Neill
 *
 */
public class LogTest extends TestCase {

	private Log testLog;
	
	/**
	 * Test method for {@link mobscrob.logging.MicroLog#getInstance(java.lang.Class)}.
	 */
	public void testMicroLog() {
		testLog = new MicroLog().getInstance(String.class);
		assertEquals("String", testLog.getClassname());
	}

}
