/**
 * TrackMetadataPersistor.java
 * @author NJ Pearman
 * @date 4 Oct 2008
 */
package mobscrob.player;

/**
 * @author Neill
 *
 */
public interface TrackMetadataPersistor {
	/**
	 * Loads the persisted TrackMetadata queue into the 
	 * specified PlayProcessor.
	 * @param processor
	 */
	void load(PlayProcessor processor);
	
	/**
	 * Saves the underlying TrackMetadata queue in the PlayProcessor.
	 * @param processor
	 * @return
	 */
	boolean save(PlayProcessor processor);
}
