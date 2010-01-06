/**
 * TrackQueuer.java
 * @author NJ Pearman
 * @date 19 Oct 2008
 */
package mobscrob.scrobbler;

import mobscrob.id3.TrackMetadata;

/**
 * @author Neill
 *
 */
public interface TrackQueuer {

	/**
	 * Adds the given item to the PostPlayProcessor queue
	 * 
	 * @param track
	 */
	void queueTrack(TrackMetadata track);
	
	/**
	 * Gets a snapshot of the underlying queue in this processor.
	 * @return
	 */
	TrackMetadata[] getQueueSnapshot();
}
