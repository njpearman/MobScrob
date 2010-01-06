package mobscrob.util.test;

/**
 * Interface to use for mocking an output stream
 * @author Neill
 *
 */
public interface IOutputStream {
	void write(int b);
	void write(byte[] b);
	void write(byte[] b, int offset, int length);
}