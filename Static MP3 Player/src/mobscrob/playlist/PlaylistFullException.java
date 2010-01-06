/**
 * PlaylistFullException.java
 * @author NJ Pearman
 * @date 29 Sep 2008
 */
package mobscrob.playlist;

/**
 * @author Neill
 *
 */
public class PlaylistFullException extends Exception {
	public String getMessage() {
		return "Playlist is at maximum capacity";
	}
}
