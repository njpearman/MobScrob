/**
 * ScrobbleProcessorTest.java
 * @author NJ Pearman
 * @date 27 Oct 2008
 */
package mobscrob.player;

import mobscrob.id3.TrackMetadata;
import mobscrob.scrobbler.MobScrobbler;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @author Neill
 *
 */
public class ScrobbleProcessorTest extends MockObjectTestCase {

	private ScrobbleProcessor testProcessor;
	private Mock mockScrobbler;
	
	/**
	 * Test method for {@link mobscrob.player.ScrobbleProcessor#shutdown()}.
	 */
	public void testShutdown() {
		// null holding track
		mockScrobbler = mock(MobScrobbler.class);
		mockScrobbler.expects(once()).method("getHoldingTrack").withNoArguments().will(returnValue(null));
		mockScrobbler.expects(once()).method("shutdown").withNoArguments();
		testProcessor = new ScrobbleProcessor((MobScrobbler)mockScrobbler.proxy());
		testProcessor.shutdown();
		
		// non null holding track
		TrackMetadata testTrack = new TrackMetadata();
		mockScrobbler = mock(MobScrobbler.class);
		mockScrobbler.expects(once()).method("getHoldingTrack").withNoArguments().will(returnValue(testTrack));
		mockScrobbler.expects(once()).method("shutdown").withNoArguments();
		testProcessor = new ScrobbleProcessor((MobScrobbler)mockScrobbler.proxy());
		testProcessor.shutdown();
	}

}
