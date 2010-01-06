package mobscrob.util.test;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Mock wrapper for an OutputStream to allow usage of JMock
 * @author Neill
 *
 */
public class MockOutputStream extends OutputStream {
	
	private IOutputStream osProxy;
	
	public MockOutputStream(IOutputStream osProxy) {
		this.osProxy = osProxy;
	}
	
	public void write(int b) throws IOException {
		osProxy.write(b);
	}
	
	public void write(byte[] b) throws IOException {
		osProxy.write(b, 0, b.length);
	}
}