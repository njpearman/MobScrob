/**
 * 
 */
package mobscrob.player;

import mobscrob.scrobbler.TrackQueuer;

/**
 * Interface for a play processor. Play processors are implemented in order to
 * do some secondary processing for an MP3 file either before or after it is
 * played by the MP3Player class.
 * 
 * @author Neill
 * 
 */
public interface PlayProcessor extends TrackQueuer {

	/**
	 * Starts the PlayProcessor instance
	 */
	public void start();

	/**
	 * Tells the processor to start shutdown operations.
	 */
	public void shutdown();

}
