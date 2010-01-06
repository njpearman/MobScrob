/**
 * PlayProcessorImplTest.java
 * @author NJ Pearman
 * @date 24 Oct 2008
 */
package mobscrob.player;

import mobscrob.id3.TrackMetadata;

import org.jmock.MockObjectTestCase;

/**
 * @author Neill
 *
 */
public class PlayProcessorImplTest extends MockObjectTestCase {

	private static final String TRACK_NAME_1 = "TEST1";
	private static final String TRACK_NAME_2 = "TEST2";
	private static final String TRACK_NAME_3 = "TEST3";
	
	private PlayProcessorImpl testProcessor;
	
	/**
	 * Test method for {@link mobscrob.player.PlayProcessorImpl#queueAtStart(mobscrob.id3.TrackMetadata)}.
	 */
	public void testQueueAtStart() {
		TrackMetadata track;
		
		testProcessor = new PlayProcessorImpl(100){
			public void process(TrackMetadata next) {}
			public void shutdown() {}
		};
		
		// check adding to an empty queue
		track = new TrackMetadata();
		track.setArtist(TRACK_NAME_1);
		testProcessor.queueAtStart(track);
		assertEquals(1, testProcessor.getQueueSnapshot().length);
		assertEquals(TRACK_NAME_1, testProcessor.getQueueSnapshot()[0].getArtist());
		
		// check adding to a queue containing one element
		track = new TrackMetadata();
		track.setArtist(TRACK_NAME_2);
		testProcessor.queueAtStart(track);
		assertEquals(2, testProcessor.getQueueSnapshot().length);
		assertEquals(TRACK_NAME_2, testProcessor.getQueueSnapshot()[0].getArtist());
		assertEquals(TRACK_NAME_1, testProcessor.getQueueSnapshot()[1].getArtist());
				
		// check adding to a queue containing two elements
		track = new TrackMetadata();
		track.setArtist(TRACK_NAME_3);
		testProcessor.queueAtStart(track);
		assertEquals(3, testProcessor.getQueueSnapshot().length);
		assertEquals(TRACK_NAME_3, testProcessor.getQueueSnapshot()[0].getArtist());
		assertEquals(TRACK_NAME_2, testProcessor.getQueueSnapshot()[1].getArtist());
		assertEquals(TRACK_NAME_1, testProcessor.getQueueSnapshot()[2].getArtist());
	}

}
