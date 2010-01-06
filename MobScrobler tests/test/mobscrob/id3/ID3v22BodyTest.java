/**
 * 
 */
package mobscrob.id3;

import mobscrob.id3.AbstractID3Body.Frame;
import mobscrob.id3.MockID3InputStream.ID3MockInputStreamException;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Unit tests for the the ID3v2.2.0 ID3 tag
 * @author Neill
 *
 */
public class ID3v22BodyTest extends MockObjectTestCase {

	private AbstractID3Body testBody;
	private Mock mockHeader;
	private MockID3InputStream mockIS;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Test method for {@link mobscrob.id3.ID3Body#readNextFrame()}.
	 */
	public void testReadNextFrame() throws Exception {
		Frame result;
		byte[] testBytes;
		
		// success, 6 byte frame
		testBytes = new byte[] 
		        {
					0x00, 0x00, 0x00, 0x00, 0x00, 0x06, // header
					0x00, 0x00, 0x00, 0x00, 0x00, 0x00  // body
		        };
		
		result = readNextFrame(testBytes, new int[] { 6, 6 });
		assertNotNull(result);
		assertEquals(6, result.getLength());
		
		// success, 12 byte frame
		testBytes = new byte[] 
		        {
					0x00, 0x00, 0x00, 0x00, 0x00, 0x0C, // header
					0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // body
					0x00, 0x00, 0x00, 0x00, 0x00, 0x00
		        };
		
		result = readNextFrame(testBytes, new int[] { 6, 12 });
		assertNotNull(result);
		assertEquals(12, result.getLength());
		
		// success, frame length given as larger than array size 
		testBytes = new byte[66310];
		testBytes[0] = 0x00;
		testBytes[1] = 0x00;
		testBytes[2] = 0x00;
		testBytes[3] = 0x01;
		testBytes[4] = 0x03;
		testBytes[5] = 0x00; // header
		for(int i=6; i<testBytes.length; i++) {
			testBytes[i] = 0x00;
		}
		     		
 		result = readNextFrame(testBytes, new int[] { 6, 66304 });
 		assertNotNull(result);
 		assertEquals(66304, result.getLength());
		
		// frame size bigger than stream length
		try {
			testBytes = new byte[] 
			        {
						0x00, 0x00, 0x00, 0x02, 0x00, 0x00, // header
						0x00, 0x00, 0x00, 0x00, 0x00, 0x00  // body
			        };
			result = readNextFrame(testBytes, new int[] { 6 });
			fail("ID3MockInputStreamException.END_OF_STREAM should be " +
				 "thrown when frame size is given as large than actual " +
				 "stream size");
		} catch(ID3MockInputStreamException e) {
			assertEquals(ID3MockInputStreamException.END_OF_STREAM, e);
		}
		
		// input stream returns end of stream
		try {
			testBytes = new byte[] 
			        {
						0x00, 0x00, 0x00, 0x00, 0x00, 0x06, // header
						0x00, 0x00, 0x00, 0x00, 0x00, 0x00  // body
			        };
			result = readNextFrame(testBytes, new int[]{ -1 });
			fail("ID3Exception.UNEXPECTED_END_OF_STREAM should be thrown " +
			     "when InputStream returns end of stream");
		} catch(ID3Exception e) {
			assertEquals(ID3Exception.UNEXPECTED_END_OF_STREAM, e);
		}

		// frame larger than frame size
		try {
			testBytes = new byte[] 
			        {
						0x00, 0x00, 0x00, 0x00, 0x00, 0x06, // header
						0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // body
						0x00, 0x00, 0x00, 0x00, 0x00, 0x00
			        };
			result = readNextFrame(testBytes, new int[]{ 6, 10 });
			fail("ID3Exception.UNEXPECTED_BYTE_READ_COUNT should be " +
			     "thrown when actual frame body is larger than given " +
			     "frame size");
		} catch(ID3Exception e) {
			assertEquals(ID3Exception.UNEXPECTED_BYTE_READ_COUNT, e);
		}
	}

	/**
	 * Test method for {@link mobscrob.id3.ID3Body#readRawFrameBytes(int)}.
	 */
	public void testReadRawFrameBytes() {}

	private Frame readNextFrame(byte[] bytes, int[] readReturnValue) throws Exception {
		mockHeader = mock(ID3Header.class);
		mockHeader.expects(once()).method("majorVersion").will(returnValue(2));
		mockIS = new MockID3InputStream(bytes);
		mockIS.setReturnInt(readReturnValue);
		
		testBody = AbstractID3Body.instance((ID3Header)mockHeader.proxy(), mockIS);
		return testBody.readNextFrame();
		
	}
}
