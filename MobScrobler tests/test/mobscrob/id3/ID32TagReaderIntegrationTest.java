package mobscrob.id3;

import mobscrob.mp3.MP3ResourceStream;

import org.jmock.MockObjectTestCase;

public class ID32TagReaderIntegrationTest extends MockObjectTestCase {
	private static final String DEMO_MP3 = "resource://audio/Spiritualized.mp3";
	private ID32TagReader testReader;
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testRead() throws Exception {
		TrackMetadata testData = new TrackMetadata();
		
		// create a resource stream
		MP3ResourceStream stream = new MP3ResourceStream(DEMO_MP3);
		testReader = new ID32TagReader(stream);
		testReader.readInto(testData);
	}

}
