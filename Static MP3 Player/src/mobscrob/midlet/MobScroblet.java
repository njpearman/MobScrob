/**
 * MobScroblet.java
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
package mobscrob.midlet;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import mobscrob.alert.AlertType;
import mobscrob.alert.Alertable;
import mobscrob.event.FileCommandListener;
import mobscrob.id3.TrackMetadata;
import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;
import mobscrob.logging.MicroLog;
import mobscrob.navigator.SEFileNavigator;
import mobscrob.player.MP3Player;
import mobscrob.player.PlayProcessor;
import mobscrob.player.ScrobbleProcessor;
import mobscrob.player.TagReaderProcessor;
import mobscrob.player.TrackMetadataFilePersistor;
import mobscrob.player.TrackMetadataPersistor;
import mobscrob.player.TrackMetadataRMSPersistor;
import mobscrob.playlist.Playlist;
import mobscrob.playlist.PlaylistRMSPersister;
import mobscrob.properties.MobScrobProperties;
import mobscrob.properties.PropertiesScreen;
import mobscrob.properties.PropertyRMSPersistor;
import mobscrob.scrobbler.MobScrobblerImpl;

/**
 * MobScroblet is the main MIDlet in the MobScrobblerImpl mobile media player
 * 
 * @author Neill
 * 
 */
public class MobScroblet extends MIDlet implements CommandListener, Alertable {

	private static Log log;

	private static final String MENU_PLAYLIST = "Playlist";
	private static final String MENU_CURRENT = "Current";
	private static final String MENU_CONFIG = "Configure";

	public static final String[] MENU_ITEMS = { MENU_PLAYLIST,
			MENU_CURRENT, MENU_CONFIG };

	private Command exitCommand = new Command("Exit", Command.EXIT, 1);
	private Command navigatorCommand = new Command("Browse", Command.ITEM, 1);

	private List mainMenu;
	private Display display;
	private MP3Player player;
	private PlayProcessor tagProcessor;
	private ScrobbleProcessor scrobbleProcessor;
	private SEFileNavigator fileNavigator;
	private Playlist playlist;
	private MobScrobblerImpl scrobbler;
	private MobScrobProperties props;
	private PropertiesScreen propsScreen;

	/**
	 * Inits the MobScrob MIDlet
	 */
	public MobScroblet() {
		final String methodName = "1";

		try {
			MicroLog.configure(this);
			LogFactory.setImplementation("mobscrob.logging.MicroLog");
			log = LogFactory.getLogger(MobScroblet.class);
			
			log.info("", "*************************");
			log.info("", "  Starting MobScrobblerImpl  ");
			log.info("", "*************************");
	
			display = Display.getDisplay(this);
			initMainMenu();
			display.setCurrent(mainMenu);
	
			// load properties
			props = new MobScrobProperties(new PropertyRMSPersistor());
			props.load();
			
			// create the default return callback
			Callback returnCB = new Callback() {
				public void callback() {
					log.info(methodName, "Returning to main screen");
					display.setCurrent(mainMenu);
				}
			};
	
			player = new MP3Player(returnCB);
	
			scrobbler = new MobScrobblerImpl(props);
			scrobbler.setAlertable(this);
			
			// tag processor as a pre-processor
			tagProcessor = new TagReaderProcessor();
			tagProcessor.start();
			player.setPrePlayProcessor(tagProcessor);

			// scrobble processor as a post processor
			scrobbleProcessor = new ScrobbleProcessor(scrobbler);
			scrobbleProcessor.start();
			TrackMetadataRMSPersistor scrobblePersister = 
				new TrackMetadataRMSPersistor(TrackMetadataRMSPersistor.SCROBBLE_QUEUE_RECORD_STORE_NAME);
			scrobblePersister.load(this.scrobbleProcessor);
			player.addPostPlayProcessor(scrobbleProcessor);
			props.setScrobbleProcessor(scrobbleProcessor);
			
			playlist = new Playlist(returnCB);
	
			playlist.addFileListener(new FileCommandListener() {
				public void perform(int command, String file) {
					if (command == FileCommandListener.SELECT) {
						log.info(methodName, "Playing selected from playlist");
						player.play(file);
					}
				}
			});
			
			PlaylistRMSPersister playlistPersister = new PlaylistRMSPersister();
			playlistPersister.load(playlist);
	
			player.addPlaylist(playlist);
	
			// create file navigator as root
			fileNavigator = new SEFileNavigator(returnCB, this);
			fileNavigator.loadFileSystem();
			fileNavigator.addPlaylist(playlist);
	
			// add the listener to play MP3s selected in the FileNavigator
			fileNavigator.addFileListener(new FileCommandListener() {
				public void perform(int command, String file) {
					if (command == FileCommandListener.SELECT) {
						log.info(methodName, "Playing file " + file);
						player.play(file);
					}
				}
			});
	
			propsScreen = new PropertiesScreen(props, returnCB, new Callback() {
				public void callback() {
					log.info(methodName, "Returning to main screen");
					display.setCurrent(mainMenu);
					alert(AlertType.INFO, "Config saved");
				}
			}, this);
			propsScreen.init();
		} catch(Throwable t) {
			log.error(methodName, "Unable to initialise Mobscroblet: "+t.getMessage(), t);
		}
	}

	/**
	 * Initialises the main menu.
	 */
	private void initMainMenu() {
		mainMenu = new List("MobScrob", List.IMPLICIT, MENU_ITEMS, null);
		mainMenu.addCommand(exitCommand);
		mainMenu.addCommand(navigatorCommand);
		mainMenu.setCommandListener(this);
	}

	protected void destroyApp(boolean unconditional) {
		final String methodName = "destroyApp";
		log.info(methodName, "Destroying");
		// stop on destroy
		if (player != null) {
			player.stopCurrentTrack();
		}

		// wait for any current submission to finish, and
		// save unsubmitted tracks to file
		scrobbleProcessor.shutdown();
		
		PlaylistRMSPersister playlistPersister = new PlaylistRMSPersister();
		playlistPersister.save(this.playlist);

		TrackMetadataPersistor persistor;
		if(props.scrobbleOffline()) {
			 persistor = new TrackMetadataFilePersistor(
					 TrackMetadataFilePersistor.SCROBBLE_QUEUE_FILENAME, 
					 TrackMetadata.getSerializer());
			 persistor.save(this.scrobbleProcessor);
		} else {
			persistor = new TrackMetadataRMSPersistor(TrackMetadataRMSPersistor.SCROBBLE_QUEUE_RECORD_STORE_NAME);
			persistor.save(this.scrobbleProcessor);
		}
		
		// need to close the log on exit
		MicroLog.close();
	}

	protected void pauseApp() {
		final String methodName = "2";
		log.info(methodName, "Pausing");
		// stop the player for the time being
		if (player != null) {
			player.stopCurrentTrack();
		}
	}

	protected void startApp() throws MIDletStateChangeException {
		final String methodName = "3";
		log.info(methodName, "Starting MobScroblet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.microedition.lcdui.CommandListener#commandAction(javax.microedition
	 * .lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command command, Displayable s) {
		final String methodName = "4";
		// just want to play a track at the moment
		if (command == exitCommand) {
			log.info(methodName, "Exiting");
			destroyApp(true);
			notifyDestroyed();
		} else if (command == List.SELECT_COMMAND) {
			int curr = mainMenu.getSelectedIndex();

			if (curr > -1 && curr < MENU_ITEMS.length) {
				String currItem = MENU_ITEMS[curr];

				if (MENU_PLAYLIST.equals(currItem)) {
					// open playlist
					log.info(methodName, "Opening playlist");
					playlist.open(this.display);
				} else if (MENU_CURRENT.equals(currItem)) {
					// show current playing file
					log.info(methodName, "Displaying currently playing track");
					player.displayCurrentTrack(this.display);
				} else if (MENU_CONFIG.equals(currItem)) {
					log.info(methodName, "Opening config screen");
					propsScreen.open(this.display);
				} else {
					log.error(methodName, "Unexpected menu item: " + currItem);
				}
			} else {
				log.error(methodName, "Invalid main menu index: " + curr);
			}
		} else if (command == navigatorCommand) {
			openNavigator();
		} else {
			log.info(methodName, "Unexpected command type: "
					+ command.getCommandType()+", "+command.getLabel());
		}
	}

	private void openNavigator() {
		final String methodName = "5";
		if (fileNavigator == null) {
			log.error(methodName, "File navigator is null!");
		} else {
			log.info(methodName, "Opening navigator...");
			fileNavigator.open(this.display);
		}
	}

	public void alert(AlertType type, String msg) {
		// create new Alert
		final Displayable currDisplay = display.getCurrent();
		final Command alertExitCommand = new Command("Done", Command.EXIT, 1);
		Alert alert = new Alert(type.getType(), msg, null, type.getUIType());
		alert.setTimeout(3000);
		alert.addCommand(alertExitCommand);
		alert.setCommandListener(new CommandListener() {
			Displayable prevDisplay = currDisplay;

			public void commandAction(Command command, Displayable s) {
				if (command == alertExitCommand) {
					// exit and display the previous display
					display.setCurrent(prevDisplay);
				}
			}
		});

		// display for 7 secs
		display.setCurrent(alert, display.getCurrent());
	}
}
