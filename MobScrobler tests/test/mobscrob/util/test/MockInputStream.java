package mobscrob.util.test;

import java.io.IOException;
import java.io.InputStream;

/**
 * Mock wrapper for an InputStream to allow usage of JMock
 * @author Neill
 *
 */
public class MockInputStream extends InputStream {

	/**
	 * 
	 */
	private IInputStream isProxy;
	
	public MockInputStream(IInputStream isProxy) {
		this.isProxy = isProxy;
	}
	
	public int read() throws IOException {
		return isProxy.read();
	}
}