/**
 * 
 */
package mobscrob.id3;

import java.io.IOException;

/**
 * Used to throw errors in the mobscrob.id3 package
 * 
 * @author Neill
 * 
 */
public class ID3Exception extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3322982191930220242L;
	
	public static final ID3Exception UNEXPECTED_END_OF_STREAM = new ID3Exception(
			"Reached the end of the input stream reading frame...");
	public static final ID3Exception UNEXPECTED_BYTE_READ_COUNT = new ID3Exception(
			"Couldn't read expected bytes for frame body");
	public static final ID3Exception INCORRECT_FRAME_HEADER_LENGTH = new ID3Exception(
			"Incorrect frame header length");
	public static final ID3Exception INVALID_FRAME_HEADER_FLAGS = new ID3Exception(
			"Invalid flags in frame header");
	public static final ID3Exception UNSUPPORTED_FORMAT = new ID3Exception(
			"Extended frame header not currently supported");

	public ID3Exception(String msg) {
		super(msg);
	}
}
