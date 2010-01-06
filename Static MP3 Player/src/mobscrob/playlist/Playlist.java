/**
 * 
 */
package mobscrob.playlist;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import mobscrob.event.FileCommandListener;
import mobscrob.event.FileCommandNotifier;
import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;
import mobscrob.midlet.Callback;
import mobscrob.midlet.MobScrobDisplay;

/**
 * @author Neill
 * 
 */
public class Playlist implements MobScrobDisplay, CommandListener,
		FileCommandNotifier {
	private static final Log log = LogFactory.getLogger(Playlist.class);

	private static final int MAX_CAPACITY = 64;

	private Command returnToMainCmd = new Command("Main", Command.EXIT, 1);
	private Command clearPlaylistCmd = new Command("Clear", Command.ITEM, 2);
	private Command removeItemCmd = new Command("Remove", Command.ITEM, 1);

	private List currentPlaylist;

	private Callback cb;
	/** Only have one listener at the moment - the MP3Player **/
	private FileCommandListener listener;

	private Vector playlist;
	private int currentIndex;

	public Playlist(Callback callback) {
		this.cb = callback;
		playlist = new Vector();
		currentIndex = -1;

		// set up the UI
		currentPlaylist = new List("Playlist", List.IMPLICIT);
		currentPlaylist.addCommand(returnToMainCmd);
		currentPlaylist.addCommand(removeItemCmd);
		currentPlaylist.addCommand(clearPlaylistCmd);
		currentPlaylist.setCommandListener(this);
	}

	/**
	 * Adds a track to the playlist. If internal Collection has size
	 * MAX_CAPACITY then no new elements can be added to the playlist until an
	 * element has been removed.
	 * 
	 * @param track
	 */
	public void addToPlaylist(PlaylistFile file) throws PlaylistFullException {
		final String methodName = "1";
		if (playlist.size() < MAX_CAPACITY) {
			playlist.addElement(file);
			currentPlaylist.append(file.filename, null);
		} else {
			log.warn(methodName, "Maximum capacity of playlist has been reached: " + 
					 playlist.size());
			throw new PlaylistFullException();
		}
	}

	/**
	 * Gets the next track in the playlist
	 * 
	 * @return
	 */
	public PlaylistFile getNext() {
		final String methodName = "2";
		currentIndex++;
		currentPlaylist.setSelectedIndex(currentIndex, true);
		PlaylistFile track = (PlaylistFile) playlist.elementAt(currentIndex);
		log.info(methodName, "Got " + track.getLocation() + " from playlist");
		return track;
	}

	public boolean hasNext() {
		final String methodName = "3";
		int num = currentIndex + 1;
		log.info(methodName, "At track number " + num);
		return num < playlist.size();
	}

	/**
	 * Returns an array of the filenames representing all of items currently in
	 * the playlist.
	 * 
	 * @return
	 */
	public String[] getPlaylistFilenames() {
		String[] filenames = new String[playlist.size()];
		PlaylistFile next;
		for (int i = 0; i < filenames.length; i++) {
			next = (PlaylistFile) playlist.elementAt(i);
			filenames[i] = next.filename;
		}

		return filenames;
	}
	
	protected Vector getPlaylist() {
		return this.playlist;
	}

	/**
	 * Displays the Playlist's List UI object in the parent display of this
	 * Playlist
	 * 
	 * @param parent
	 *            The parent display for the Playlist
	 */
	public void open(Display parent) {
		parent.setCurrent(currentPlaylist);
	}

	/**
	 * Clears all items in the playlist
	 */
	public void clearPlaylist() {
		currentPlaylist.deleteAll();
		playlist.removeAllElements();
	}

	public void commandAction(Command command, Displayable s) {
		final String methodName = "4";
		if (command == returnToMainCmd) {
			// return to the main menu
			cb.callback();
		} else if (command == removeItemCmd) {
			currentIndex = currentPlaylist.getSelectedIndex();
			log.info(methodName, "Removing item at " + currentIndex);
			currentPlaylist.delete(currentIndex);
			playlist.removeElementAt(currentIndex);
		} else if (command == clearPlaylistCmd) {
			log.info(methodName, "Clear command..");
			clearPlaylist();
		} else if (command == List.SELECT_COMMAND) {
			log.info(methodName, "Playing track from playlist");
			// play selected track
			int selectedIndex = currentPlaylist.getSelectedIndex();
			currentIndex = selectedIndex;
			PlaylistFile file = (PlaylistFile) playlist.elementAt(selectedIndex);
			notifyFileCommandListeners(file.location);
		} else {
			log.warn(methodName, "Unexpected command in Playlist");
		}

	}

	private void notifyFileCommandListeners(String filename) {
		listener.perform(FileCommandListener.SELECT, filename);
	}

	public void addFileListener(FileCommandListener listener) {
		this.listener = listener;
	}

	public static class PlaylistFile {
		private String filename;
		private String location;

		public PlaylistFile(String filename, String location) {
			this.filename = filename;
			this.location = location;
		}

		public String getLocation() {
			return this.location;
		}
		
		protected String getName() {
			return this.filename;
		}
	}
}
