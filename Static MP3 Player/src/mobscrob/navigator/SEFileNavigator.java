/**
 * SEFileNavigator.java
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
package mobscrob.navigator;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import mobscrob.alert.AlertType;
import mobscrob.alert.Alertable;
import mobscrob.event.FileCommandListener;
import mobscrob.event.FileCommandNotifier;
import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;
import mobscrob.midlet.Callback;
import mobscrob.midlet.MobScrobDisplay;
import mobscrob.playlist.Playlist;
import mobscrob.playlist.PlaylistFullException;
import mobscrob.playlist.Playlist.PlaylistFile;

/**
 * A Sony Ericsson implementation of a File Navigator
 * 
 * @author Neill
 * 
 */
public class SEFileNavigator implements MobScrobDisplay, CommandListener,
		FileCommandNotifier {
	private static final Log log = LogFactory.getLogger(SEFileNavigator.class);

	/** The root of this file system **/
	private DirectoryNode root;

	/** The current node being viewed **/
	private DirectoryNode currNode;

	private List currList;
	private Vector currItems;
	private Display parent;

	private Command backCmd = new Command("Back", Command.EXIT, 1);
	private Command addToPlaylistCmd = new Command("Add", Command.ITEM, 1);
	private Command replacePlaylistCmd = new Command("Replace", Command.ITEM, 1);
	private Command returnToMainCmd = new Command("Main", Command.ITEM, 1);

	private Playlist playlist;

	private Vector fcListeners;
	private Callback exitCB;
	private Alertable alerter;

	public SEFileNavigator(Callback exitCB, Alertable alerter) {
		this.root = new SystemRootNode(new DirectoryNodeManager() {
			public DirectoryNode instance(String name, DirectoryNode parent) {
				return new DirectoryNode(name, parent, this);
			}
		});

		this.alerter = alerter;
		init(exitCB);
	}

	private void init(Callback exitCB) {
		this.exitCB = exitCB;
		this.fcListeners = new Vector();

		// set current node
		this.currNode = this.root;			
		initUI(null);
		this.currItems = new Vector();
	}

	/**
	 * Init the UI List components 
	 */
	private void initUI(String[] listItems) {
		if(listItems != null) {
			this.currList = new List("Navigate", List.IMPLICIT, listItems, null);
		} else {
			this.currList = new List("Navigate", List.IMPLICIT);
		}

		this.currList.addCommand(backCmd);
		this.currList.addCommand(addToPlaylistCmd);
		this.currList.addCommand(replacePlaylistCmd);
		this.currList.addCommand(returnToMainCmd);

		this.currList.setCommandListener(this);
	}

	/**
	 * Associates the specified Playlist with this SEFileNavigator. If a
	 * Playlist has already been associated then the call to this method is
	 * ignored.
	 * 
	 * @param playlist
	 */
	public void addPlaylist(Playlist playlist) {
		final String methodName = "1";
		if (playlist != null) {
			this.playlist = playlist;
		} else {
			log.warn(methodName, "Can't add playlist - already have playlist");
		}
	}

	/**
	 * Opens the File Navigator
	 */
	public void open(Display parent) {
		final String methodName = "2";
		log.info(methodName, "Opening SEFileNavigator");

		// load only when actually being opened
		composeList(currNode.getChildrenList(), currNode.getFilesList());
		if (this.parent == null) {
			this.parent = parent;
		}
		this.parent.setCurrent(currList);
	}

	/**
	 * Clears the current List and sets the contents to the supplied
	 * DirectoryNode and file details. Also clears the current items Vector and
	 * fills with the DirectoryNode and file details.
	 * 
	 * @param directories
	 * @param files
	 */
	private void composeList(DirectoryNode[] directories, String[] files) {
		final String methodName = "3";
		
		this.currList.deleteAll();
		this.currItems.removeAllElements();

		String[] displayStrs = new String[directories.length + files.length];

		log.info(methodName, "Creating new UI List, size " + displayStrs.length);
		for (int i = 0; i < directories.length; i++) {
			this.currItems.addElement(directories[i]);
			displayStrs[i] = directories[i].name;
		}
		int offset = directories.length;
		for (int i = 0; i < files.length; i++) {
			if (files[i] == null) {
				log.warn(methodName, "Null string at " + i);
			}
			this.currItems.addElement(files[i]);
			displayStrs[offset+i] = files[i];
		}
		
		// recreating the List item is faster tan removing and adding 
		// the list items
		initUI(displayStrs);
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

		log.debug(methodName, "Navigator received command " + command);
		if (command == backCmd) {
			log.info(methodName, "Back command pressed");
			// get parent of current node
			if (!currNode.isRoot()) {
				updateCurrentNode(currNode.getParent());
			} else {
				// return to main menu..?
			}
		} else if (command == List.SELECT_COMMAND) {
			log.info(methodName, "Select / play now command pressed");
			int index = currList.getSelectedIndex();
			Object selectedItem = currItems.elementAt(index);
			if (selectedItem instanceof DirectoryNode) {
				log.info(methodName, "Got directory node at " + index);
				// open directory in list
				updateCurrentNode((DirectoryNode) selectedItem);
			} else if (selectedItem instanceof String) {
				log.info(methodName, "Got String at " + index);
				String fullPath = currNode.getAbsolutePath() + selectedItem;
				// tell listeners to start playing
				notifyFileCommandListeners(fullPath);
			} else {
				log.warn(methodName, "Got unexpected object at " + index);
			}
		} else if (command == addToPlaylistCmd) {
			if (playlist != null) {
				addPlaylistItems();
			} else {
				log.warn(methodName, "Ignoring command: no playlist associated with navigator");
			}
		} else if(command == replacePlaylistCmd) {
			if (playlist != null) {
				// first clear playlist, then re-populate
				playlist.clearPlaylist();
				addPlaylistItems();
			} else {
				log.warn(methodName, "Ignoring command: no playlist associated with navigator");
			}
		} else if (command == returnToMainCmd) {
			log.info(methodName, "Returning to main menu");
			exitCB.callback();
		} else {
			log.warn(methodName, "Unexpected command pressed");
		}
	}

	/**
	 * Updates the current node and display with newNode.
	 * 
	 * @param newNode
	 */
	public void updateCurrentNode(DirectoryNode newNode) {
		currNode = newNode;
		open(parent);
	}

	public void loadFileSystem() {
		final String methodName = "5";
		try {
			root.loadAll();
		} catch (IOException ex) {
			log.error(methodName, "Unable to load file system");
		}
	}
	
	private void addPlaylistItems() {
		final String methodName = "6";
		
		int index = currList.getSelectedIndex();
		Object selectedItem = currItems.elementAt(index);
		if (selectedItem instanceof DirectoryNode) {
			try {
				log.info(methodName, "Got directory node at " + index);
				// open directory in list
				DirectoryNode node = (DirectoryNode) selectedItem;
				Vector allFiles = new Vector();
				node.putAllAncestorFiles(allFiles);

				// add all to playlist
				Enumeration e = allFiles.elements();
				PlaylistFile next;
				log.info(methodName, "Have " + allFiles.size() + " to add to playlist");
				while (e.hasMoreElements()) {
					next = (PlaylistFile) e.nextElement();
					if (next != null) {
						try { playlist.addToPlaylist(next); }
						catch(PlaylistFullException pfe) { break; }
					}
				}
			} catch (IOException ex) {
				log.error(methodName, "Unable to add directory node contents to playlist: " + ex.getMessage(), ex);
			}
		} else if (selectedItem instanceof String) {
			log.info(methodName, "Got String at " + index);
			String fullPath = currNode.getAbsolutePath() + selectedItem;
			// add file to playlist
			PlaylistFile file = new PlaylistFile((String) selectedItem, fullPath);
			try {
				playlist.addToPlaylist(file);
				log.info(methodName, "Added file " + file.getLocation()
						+ " to playlist");
			} catch(PlaylistFullException e) {
				alerter.alert(AlertType.ERROR, "Playlist full - remove tracks first!");
			}
		} else {
			log.warn(methodName, "Got unexpected object at " + index);
		}
	}

	/**
	 * @param filename
	 */
	private void notifyFileCommandListeners(String filename) {
		Enumeration e = fcListeners.elements();
		while (e.hasMoreElements()) {
			((FileCommandListener) e.nextElement()).perform(FileCommandListener.SELECT, filename);
		}
	}

	/**
	 * Adds listener to the list of FileListeners in this FileNavigator
	 * 
	 * @param listener
	 */
	public void addFileListener(FileCommandListener listener) {
		fcListeners.addElement(listener);
	}
}
