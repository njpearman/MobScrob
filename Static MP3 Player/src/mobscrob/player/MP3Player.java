/**
 * MP3Player.java
 * 
 * This program is distributed under the terms of the GNU General Public 
 * License
 * Copyright 2008 NJ Pearman
 *
 * This file is part of MobScrob.
 *
 * MobScrob is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MobScrob is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MobScrob.  If not, see <http://www.gnu.org/licenses/>.
 */
package mobscrob.player;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;

import mobscrob.event.Listener;
import mobscrob.id3.TrackMetadata;
import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;
import mobscrob.midlet.Callback;
import mobscrob.midlet.MobScrobDisplay;
import mobscrob.playlist.Playlist;

/**
 * @author neill
 * 
 */
public class MP3Player implements Runnable, PlayerListener {

	private static final Log log = LogFactory.getLogger(MP3Player.class);

	private static final String RESOURCE = "resource:";
	private static final String FILE = "file:";
	private static final String MP3 = ".mp3";

	private Callback cb;
	private CurrentTrack trackDisplay;
	private TrackTimer timer;
	private Player mp3Player;
	private String queuedFile;
	private Playlist playlist;
	private TrackMetadata stoppedTrack;
	private TrackMetadata currentTrack;
	private PlayProcessor prePlayProcessor;
	private PlayProcessorSet postProcessors;

	public MP3Player(Callback cb) {
		this.cb = cb;
		this.trackDisplay = new CurrentTrack();
		timer = new TrackTimer();
		
		this.postProcessors = new PlayProcessorSet(2);
	}

	public void addPlaylist(Playlist playlist) {
		final String methodName = "1";
		if (playlist != null) {
			this.playlist = playlist;
		} else {
			log.warn(methodName, "Already have a playlist");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		final String methodName = "2";
		log.info(methodName, "Started thread...");
		try {
			// stop player if it is currently playing.
			this.stopCurrentTrack();

			// start player to play track
			this.startTrack(queuedFile);
		} catch (IOException e) {
			log.error(methodName, "IOException: " + e.getMessage(), e);
		} catch (MediaException e) {
			log.error(methodName, "MediaException: " + e.getMessage(), e);
		} catch (Throwable e) {
			log.error(methodName, "Unchecked exception: " + e.getMessage(), e);
		}
	}

	/**
	 * Sets the post processor for this player. If it has already been set, this
	 * call is ignored.
	 * 
	 * @param postProcessor
	 */
	public void setPrePlayProcessor(PlayProcessor preProcessor) {
		if (this.prePlayProcessor == null) {
			this.prePlayProcessor = preProcessor;
		}
	}
	
	public void addPostPlayProcessor(PlayProcessor processor) {
		if(processor != null) {
			postProcessors.addProcessor(processor);
		}
	}

	/**
	 * Starts playing a track
	 */
	public void play(String filename) {
		final String methodName = "3";
		queuedFile = filename;
		// play the track in a new thread
		log.info(methodName, "Starting track " + filename);
		new Thread(this).start();
	}

	/**
	 * Stops and closes the current track. This method will not allow the
	 * current track to be started up again at the point it was stopped.
	 */
	public void stopCurrentTrack() {
		final String methodName = "4";

		log.info(methodName, "Stopping track");

		// cache the track that is stopping if there is one
		// and note how long its played for
		if (currentTrack != null && mp3Player != null && mp3Player.getState() == Player.STARTED) {
			stoppedTrack = currentTrack;
			stoppedTrack.setCurrentPosition((long) (mp3Player.getMediaTime() / 1000));
			if (stoppedTrack.getCurrentPosition() == Player.TIME_UNKNOWN) {
				log.warn(methodName, "Unknown track position");
			}
		}

		if (mp3Player != null && mp3Player.getState() != Player.CLOSED) {

			try {
				mp3Player.stop();
				timer.stop();
				mp3Player.close();
				log.info(methodName, "Stopped and closed player");
			} catch (MediaException e) {
				log.error(methodName, "Error stopping player: "
						+ e.getMessage(), e);
			}
		} else {
			log.info(methodName, "Nothing to stop");
		}
	}

	/**
	 * Starts a new Player with the specified resource
	 * 
	 * @param resourceName
	 * @throws IOException
	 * @throws MediaException
	 */
	private void startTrack(String resourceName) throws IOException,
			MediaException {
		final String methodName = "5";
		log.info(methodName, "Playing " + resourceName);
		// reset the display time
		trackDisplay.resetTime();
		// set up the player as MP3 and add this as a listener
		mp3Player = Manager.createPlayer(getResourceAsStream(resourceName),
				"audio/mpeg");
		mp3Player.addPlayerListener(this);
		log.info(methodName, "Created player as MP3 and added listener");
		// set up the player and start playing
		mp3Player.prefetch();
		mp3Player.realize();
		long length = mp3Player.getDuration() / 1000;
		log.info(methodName, "Realized player, track length: " + length);
		currentTrack = new TrackMetadata(resourceName, length);
		prePlayProcess(currentTrack);
		mp3Player.start();
		new Thread(timer).start();
	}

	/**
	 * Checks that the file has .mp3 extension, and retrieves a Stream for the
	 * resource from the correct location.
	 * 
	 * @param resourceName
	 * @return
	 * @throws IOException
	 */
	public InputStream getResourceAsStream(String resourceName)
			throws IOException {
		final String methodName = "6";
		InputStream is = null;
		// check we are getting an MP3 file
		if (!resourceName.endsWith(MP3)) {
			throw new IOException("Incompatible resource: " + resourceName
					+ "\r\n    Can only play MP3.");
		}
		if (resourceName == null) {
			String msg = "Null resource type";
			log.error(methodName, msg);
			throw new IOException(msg);
		} else if (resourceName.startsWith(RESOURCE)) {
			int index = resourceName.indexOf(':');
			String trimmed = resourceName.substring(index + 1);
			log.debug(methodName, "Getting jar resource " + trimmed);
			is = this.getClass().getResourceAsStream(trimmed);
			log.info(methodName, "Got resource as stream, "
					+ (is == null ? "stream is null" : "stream not null"));
		} else if (resourceName.startsWith(FILE)) {
			log.info(methodName, "Opening MP3 file stream");
			FileConnection fc = (FileConnection) Connector.open(resourceName);
			if (!fc.exists()) {
				throw new IOException("Resource " + resourceName
						+ " cannot be found");
			}
			is = fc.openInputStream();
			log.info(methodName, "Got file as stream, "
					+ (is == null ? "stream is null" : "stream not null"));
		} else {
			String msg = "Unknown resource type: " + resourceName;
			// unknown resource type
			log.error(methodName, msg);
			throw new IOException(msg);
		}

		return is;
	}

	/**
	 * Receives notifications of events in the Player. Primarily for logging.
	 * 
	 * @param player
	 * @param event
	 * @param eventData
	 */
	public void playerUpdate(Player player, String event, Object eventData) {
		final String methodName = "7";

		log.debug(methodName, "Update from player: " + event + ", "
				+ currentTrack.getFileLocation());
		if (PlayerListener.STARTED.equals(event)) {
			log.info(methodName, "player started");
		} else if (PlayerListener.END_OF_MEDIA.equals(event)) {
			// get the track thats finished and start up the next one
			log.info(methodName, "End of media");
			timer.stop();
			// process a track that has finished, assume played time == track
			// length
			currentTrack.setCurrentPosition(currentTrack.getTrackLength());
			log.info(methodName, "Set current track position to "
					+ currentTrack.getCurrentPosition());
			postPlayProcess(currentTrack);
			if (playlist != null) {
				if (playlist.hasNext()) {
					log.info(methodName, "Playing next track in playlist");
					play(playlist.getNext().getLocation());
				}
			} else {
				log.warn(methodName,
						"Playlist is null so no more tracks to play :(");
			}
		} else if (PlayerListener.STOPPED.equals(event)) {
			// post process - note if a track is stopped then started again
			// it won't currently be processed the second time through
			// needs to be looked at to act more intelligently
			log.info(methodName, "Stopped");
			postPlayProcess(stoppedTrack);
		} else if (PlayerListener.CLOSED.equals(event)) {
			log.info(methodName, "Closed");
		}
	}

	public void prePlayProcess(TrackMetadata track) {
		final String methodName = "8";
		if (prePlayProcessor != null) {
			track.setListener(trackDisplay);
			prePlayProcessor.queueTrack(track);
			log.info(methodName, "Pre processing track");
		} else {
			log.warn(methodName, "No pre-play processor set");
		}
	}

	/**
	 * Call the post processor
	 * 
	 * @param track
	 */
	public void postPlayProcess(TrackMetadata track) {
		final String methodName = "9";
		// do something with a finished track
		if (postProcessors != null && !track.isPostProcessed()) {
			postProcessors.processByAll(track);
		} else {
			log.warn(methodName, "Not post processing track");
		}
	}

	public void displayCurrentTrack(Display parent) {
		this.trackDisplay.open(parent);
	}
	
	private class PlayProcessorSet {
		private int index;
		private PlayProcessor[] processors;
		
		PlayProcessorSet(int capacity) {
			this.index = 0;
			this.processors = new PlayProcessor[capacity];
		}
		
		/**
		 * Add a PlayProcessor to this PlayProcessorSet, provided the capacity
		 * of the set hasn't been reached.  If it has, then this call is
		 * ignored.
		 * @param processor
		 */
		void addProcessor(PlayProcessor processor) {
			final String methodName = "10";
			if(index < processors.length) {
				this.processors[index++] = processor;
			} else {
				log.error(methodName, "Unable to add more PlayProcessors, capacity reached");
			}
		}
		
		/**
		 * Process the track in all PlayProcessors contained in this set
		 * @param track
		 */
		void processByAll(TrackMetadata track) {
			final String methodName = "11";
			for(int i=0; i<processors.length; i++) {
				if(processors[i] != null) {
					processors[i].queueTrack(track);
					track.setPostProcessed(true);
					log.info(methodName, "Post processing track");
				}
			}
		}
	}

	/**
	 * Runnable class used to time a track and update the display appropriately.
	 * 
	 * @author Neill
	 * 
	 */
	public class TrackTimer implements Runnable {

		public long MICRO_SECONDS_PER_SECOND = 1000 * 1000;
		public long ONE_SECOND = 1000;

		boolean playing;
		boolean prevStopped;

		TrackTimer() {
			prevStopped = true;
		}

		public void run() {
			final String methodName = "12";
			long sleepToTime, currentTime, sleepTime;

			log.info(methodName, "Starting track timer...");
			while (!prevStopped) {
				try {
					Thread.sleep(ONE_SECOND);
				} catch (Exception e) {
				}
			}

			// start the track timer
			playing = true;
			prevStopped = false;
			log.info(methodName, "Entering track timer loop");
			sleepToTime = System.currentTimeMillis() + ONE_SECOND;
			while (playing) {
				// sleep until next second to update display
				try {
					currentTime = System.currentTimeMillis();
					sleepTime = currentTime >= sleepToTime ? 1000 : sleepToTime - currentTime;
					Thread.sleep(sleepTime);
					sleepToTime = System.currentTimeMillis() + ONE_SECOND;
					// update display time
					trackDisplay.increaseTimeByOneSecond();
				} catch (Exception e) {
					log.error(methodName, "TrackTimer was interrupted: "
							+ e.getMessage(), e);
				}
			}

			log.info(methodName, "Exited track timer loop");
			prevStopped = true;
		}

		void stop() {
			final String methodName = "stop";
			log.info(methodName, "Stopping track timer");
			playing = false;
		}
	}

	/**
	 * Display for the current track being played.
	 * 
	 * @author Neill
	 * 
	 */
	public class CurrentTrack implements MobScrobDisplay, CommandListener, Listener {
		private Command exitCmd = new Command("Main", Command.BACK, 1);
		private Command stopCmd = new Command("Stop", Command.OK, 1);
		private Command startCmd = new Command("Start", Command.OK, 1);

		private Form track;
		private StringItem timeDisplay;
		private StringItem trackDisplay;
		private StringItem artistDisplay;
		private StringItem albumDisplay;
		
		private long playTime;

		CurrentTrack() {
			playTime = 0;
			init();
		}
		
		private void init() {
			
			track = new Form("No track selected");
			
			artistDisplay = new StringItem("", "");
			trackDisplay = new StringItem("", "");
			albumDisplay = new StringItem("", "");
			timeDisplay = new StringItem("", "Time: " + 
					getDisplayTimeString(this.playTime) + " secs");
			
			artistDisplay.setFont(Font.getFont(Font.FACE_PROPORTIONAL,
					Font.STYLE_PLAIN, Font.SIZE_MEDIUM));
			trackDisplay.setFont(Font.getFont(Font.FACE_PROPORTIONAL,
					Font.STYLE_PLAIN, Font.SIZE_LARGE));
			albumDisplay.setFont(Font.getFont(Font.FACE_PROPORTIONAL,
					Font.STYLE_PLAIN, Font.SIZE_MEDIUM));

			timeDisplay.setFont(Font.getFont(Font.FACE_PROPORTIONAL,
					Font.STYLE_PLAIN, Font.SIZE_MEDIUM));

			track.addCommand(exitCmd);

			track.append(trackDisplay);
			track.append(artistDisplay);
			track.append(albumDisplay);
			track.append(timeDisplay);

			track.setCommandListener(this);
		}

		public void open(Display parent) {
			final String methodName = "13";
			if (currentTrack == null) {
				log.warn(methodName, "No current track to display");
				track.setTitle("No track selected");
			} else {
				log.warn(methodName, "Have a track to display");
				track.setTitle("Current track");

				displayCurrentTrack();
				setTime();
			}

			setActiveCommand();
			parent.setCurrent(track);
		}

		void setActiveCommand() {
			final String methodName = "setActiveCommand";
			// check whether track is currently playing or not
			if (mp3Player == null) {
				log.warn(methodName,
						"Can't set current track action - player is null");
			} else if (mp3Player.getState() == Player.STARTED) {
				track.addCommand(stopCmd);
				track.removeCommand(startCmd);
			} else {
				track.addCommand(startCmd);
				track.removeCommand(stopCmd);
			}
		}

		/**
		 * Increases the display time by one second.
		 */
		void increaseTimeByOneSecond() {
			playTime++;
			setTime();
		}
		
		/**
		 * Sets the current display details to the details of the track 
		 * currently being played. 
		 */
		void displayCurrentTrack() {
			trackDisplay.setText(currentTrack.getTrackTitle() + "\n");
			artistDisplay.setText(currentTrack.getArtist() + "\n");
			albumDisplay.setText("#" + currentTrack.getTrackNumber() + " - "
					+ currentTrack.getAlbumTitle() + "\n");
		}

		/**
		 * Resets the display time to zero.
		 */
		void resetTime() {
			playTime = 0;
			setTime();
		}
		
		/**
		 * Updates the display time. 
		 */
		void setTime() {
			if (timeDisplay != null) {
				timeDisplay.setText("Time: " + getDisplayTimeString(this.playTime));
			}
		}

		/**
		 * Determines the string to display reflecting the time in seconds
		 * passed to this method.
		 * @param timeInSeconds
		 * @return
		 */
		String getDisplayTimeString(long timeInSeconds) {
			long hours = timeInSeconds / (60*60);
			long mins = timeInSeconds / 60;
			long secs = timeInSeconds % 60;
			
			StringBuffer buf = new StringBuffer();
			
			if(hours > 0) {
				buf.append(hours).append(':');
			}
			if(mins < 10) buf.append('0');
			buf.append(mins).append(':');
			
			if(secs < 10) buf.append('0');
			buf.append(secs);
			
			return buf.toString();
			
		}
		
		public void commandAction(Command command, Displayable s) {
			final String methodName = "14";
			if (command == exitCmd) {
				// back to main screen
				cb.callback();
			} else if (command == stopCmd) {
				// stop playing
				stopCurrentTrack();
				setActiveCommand();
			} else if (command == startCmd) {
				// start playing
				try {
					if (currentTrack != null) {
						startTrack(currentTrack.getFileLocation());
						resetTime();
						setActiveCommand();
					} else {
						log.error(methodName, "No current track selected!");
					}
				} catch (Exception e) {
					log.error(methodName, "Unable to start track "
							+ currentTrack.getFileLocation(), e);
				}
			}
		}

		public void event() {
			this.displayCurrentTrack();
		}
	}
}
