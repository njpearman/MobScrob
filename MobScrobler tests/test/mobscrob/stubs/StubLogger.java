/**
 * 
 */
package mobscrob.stubs;

import mobscrob.logging.Log;

/**
 * @author neill
 *
 */
public class StubLogger implements Log {

	/* (non-Javadoc)
	 * @see mobscrob.logging.Log#debug(java.lang.String, java.lang.String)
	 */
	@Override
	public void debug(String methodName, String msg) { }

	/* (non-Javadoc)
	 * @see mobscrob.logging.Log#debug(java.lang.String, java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void debug(String methodName, String msg, Throwable t) { }

	/* (non-Javadoc)
	 * @see mobscrob.logging.Log#error(java.lang.String, java.lang.String)
	 */
	@Override
	public void error(String methodName, String msg) { }

	/* (non-Javadoc)
	 * @see mobscrob.logging.Log#error(java.lang.String, java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void error(String methodName, String msg, Throwable t) { }

	/* (non-Javadoc)
	 * @see mobscrob.logging.Log#fatal(java.lang.String, java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void fatal(String methodName, String msg, Throwable t) { }

	/* (non-Javadoc)
	 * @see mobscrob.logging.Log#getClassname()
	 */
	@Override
	public String getClassname() { return "StubLogger"; }

	/* (non-Javadoc)
	 * @see mobscrob.logging.Log#getInstance(java.lang.Class)
	 */
	@Override
	public Log getInstance(Class clazz) { return new StubLogger(); }

	/* (non-Javadoc)
	 * @see mobscrob.logging.Log#info(java.lang.String, java.lang.String)
	 */
	@Override
	public void info(String methodName, String msg) { }

	/* (non-Javadoc)
	 * @see mobscrob.logging.Log#info(java.lang.String, java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void info(String methodName, String msg, Throwable t) { }

	/* (non-Javadoc)
	 * @see mobscrob.logging.Log#trace(java.lang.String, java.lang.String)
	 */
	@Override
	public void trace(String methodName, String msg) { }

	/* (non-Javadoc)
	 * @see mobscrob.logging.Log#trace(java.lang.String, java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void trace(String methodName, String msg, Throwable t) { }

	/* (non-Javadoc)
	 * @see mobscrob.logging.Log#warn(java.lang.String, java.lang.String)
	 */
	@Override
	public void warn(String methodName, String msg) { }

	/* (non-Javadoc)
	 * @see mobscrob.logging.Log#warn(java.lang.String, java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void warn(String methodName, String msg, Throwable t) { }
}
