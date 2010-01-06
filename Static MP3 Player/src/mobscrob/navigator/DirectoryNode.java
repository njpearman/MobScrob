/**
 * DirectoryNode.java
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

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;
import mobscrob.playlist.Playlist;
import mobscrob.util.StringUtil;
import mobscrob.util.StringUtil.CompareResult;

/**
 * Represents a branch in a directory tree
 * 
 * @author Neill
 * 
 */
public class DirectoryNode {
	private static final Log log = LogFactory.getLogger(DirectoryNode.class);

	private static final String STR_PERIOD = ".";
	private static final String STR_FORWARD_SLASH = "/";
	private static final String STR_MP3_EXTENSION = ".mp3";
	
	String name;
	protected String absolutePath;
	protected Vector children;
	protected Vector filenames;
	private DirectoryNode parent;
	private DirectoryNodeManager manager;

	/**
	 * Creates a new DirectoryNode. The name is the directory name plus '/'
	 * appended. parent is the parent of this directory node. Passing null as
	 * parent implies that this is the root node.
	 * 
	 * @param name
	 * @param parent
	 * @param manager
	 */
	public DirectoryNode(String name, DirectoryNode parent,
			DirectoryNodeManager manager) {
		this.name = name;
		this.parent = parent;
		this.absolutePath = parent == null ? name : parent.absolutePath + name;
		this.manager = manager;
	}

	/**
	 * Returns the absolute path of this DirectoryNode
	 * 
	 * @return
	 */
	public String getAbsolutePath() {
		return this.absolutePath;
	}

	/**
	 * Determines whether this DirectoryNode is the root of a directory tree
	 * 
	 * @return
	 */
	public boolean isRoot() {
		return parent == null;
	}

	/**
	 * REturns the parent DirectoryNode of this node
	 * 
	 * @return
	 */
	public DirectoryNode getParent() {
		return parent;
	}

	/**
	 * Uses a FileConnection to get the details of this DirectoryNode and create
	 * Vectors for the children and MP3 files. Hidden directories and file types
	 * other than MP3s are ignored.
	 * 
	 * @throws IOException
	 */
	public void loadChildrenAndFiles() throws IOException {
		FileConnection fc = (FileConnection) Connector.open(absolutePath);

		try {
			if (!fc.exists()) {
				throw new IOException("Directory " + name + " does not exist");
			}

			children = new Vector();
			filenames = new Vector();
			Enumeration e = fc.list();
			addSorted(e);
		} finally {
			if (fc != null)
				fc.close();
		}
	}

	/**
	 * Iterates the elements in the Enumeration, and splits them into a sorted
	 * Vector of child directories and a sorted Vector of files.
	 * 
	 * @param e
	 * @param children
	 * @param filenames
	 */
	protected void addSorted(Enumeration e) {
		String next;

		while (e.hasMoreElements()) {
			next = e.nextElement().toString();
			if (!next.startsWith(STR_PERIOD)) {
				if (next.endsWith(STR_FORWARD_SLASH)) {
					insertOrderedChildElements(next);
				} else if (next.endsWith(STR_MP3_EXTENSION)) {
					insertOrderedFilenameElements(next);
				}
			}
		}
	}

	/**
	 * This method makes an assumption that files will be in some sort of order
	 * in a directory structure so just using a reverse bubble sort to order the
	 * elements in the DirectoryNode.
	 * 
	 * @param next
	 * @param addTo
	 */
	protected void insertOrderedChildElements(String next) {
		DirectoryNode value;
		CompareResult result;
		if (children.size() == 0) {
			// first element
			children.addElement(manager.instance(next, this));
		} else {
			boolean added = false;
			for (int i = children.size() - 1; i > -1; i--) {
				value = (DirectoryNode) children.elementAt(i);
				result = StringUtil.compare(value.name, next);
				if (result == CompareResult.GREATER_THAN
						|| result == CompareResult.EQUALS) {
					children.insertElementAt(manager.instance(next, this),
							i + 1);
					added = true;
					break;
				}
			}
			if (!added) {
				// not added before so add to start
				children.insertElementAt(manager.instance(next, this), 0);
			}
		}
	}

	protected void insertOrderedFilenameElements(String next) {
		String value;
		CompareResult result;
		if (filenames.size() == 0) {
			// first element
			filenames.addElement(next);
		} else {
			boolean added = false;
			for (int i = filenames.size() - 1; i > -1; i--) {
				value = (String) filenames.elementAt(i);
				result = StringUtil.compare(value, next);
				if (result == CompareResult.GREATER_THAN
						|| result == CompareResult.EQUALS) {
					filenames.insertElementAt(next, i + 1);
					added = true;
					break;
				}
			}
			if (!added) {
				// not added before so add to start
				filenames.insertElementAt(next, 0);
			}
		}
	}

	/**
	 * Recursively loads the ancestors of this DirectoryNode. Can be more
	 * efficient as currently each child will create a new FileConnection.
	 * 
	 * @throws IO
	 */
	public void loadAll() throws IOException {
		loadChildrenAndFiles();

		Enumeration e = children.elements();
		DirectoryNode next;
		while (e.hasMoreElements()) {
			next = (DirectoryNode) e.nextElement();
			next.loadAll();
		}
	}

	/**
	 * Empties the DirectoryNode's internal resources
	 */
	public void clear() {
		this.children.removeAllElements();
		this.filenames.removeAllElements();
	}

	/**
	 * Returns a String array containing the names of the children of this
	 * DirectoryNode
	 * 
	 * @return
	 */
	public DirectoryNode[] getChildrenList() {
		final String methodName = "1";
		if (children == null) {
			log.warn(methodName, "Null children vector");
			return new DirectoryNode[0];
		}
		DirectoryNode[] list = new DirectoryNode[children.size()];
		Enumeration e = children.elements();
		int count = 0;
		while (e.hasMoreElements()) {
			list[count++] = (DirectoryNode) e.nextElement();
		}

		return list;
	}

	/**
	 * Constructs Playlist.File objects from all files that are ancestors of
	 * this node and puts them in the specified Vector.
	 * 
	 * @return
	 */
	public void putAllAncestorFiles(Vector allAncestors) throws IOException {
		final String methodName = "2";

		if (children == null || filenames == null) {
			log.warn(methodName, name + ": need to load children");
			loadChildrenAndFiles();
		}

		// add all files from this node
		copyElementsAsPlaylistFiles(filenames, allAncestors);

		// iterate child directories to get all ancestors
		if (children == null) {
			log.warn(methodName, "Child vector is null...");
		} else {
			Enumeration e = children.elements();
			DirectoryNode next;
			while (e.hasMoreElements()) {
				next = (DirectoryNode) e.nextElement();
				log.debug(methodName, "Got directory node " + next.name);
				if (next != null) {
					// add all files from ancestors
					next.putAllAncestorFiles(allAncestors);
				}
			}
		}
	}

	/**
	 * Copies all elements in source to destination as Playlist.file objects,
	 * assuming that all elements in source are files.
	 * 
	 * @param source
	 * @param destination
	 */
	private void copyElementsAsPlaylistFiles(Vector source, Vector destination) {
		final String methodName = "3";
		if (source == null || destination == null) {
			log.error(methodName,
					"Cannot transfer contents to/from a null Vector");
		}

		Object nextFile;
		String nextStr;
		Enumeration e = source.elements();
		while (e.hasMoreElements()) {
			nextFile = e.nextElement();

			if (nextFile instanceof String) {
				nextStr = (String)nextFile;
				if (nextFile != null) {
					destination.addElement(new Playlist.PlaylistFile(nextStr,
							getAbsolutePath() + nextStr));
				}
			} else if (nextFile instanceof Playlist.PlaylistFile) {
				destination.addElement(nextFile);
			} else {
				log.info(methodName, "Unknown Vector contents: " + nextFile);
			}
		}
	}

	/**
	 * Returns a String array of the files in this DirectoryNode
	 * 
	 * @return
	 */
	public String[] getFilesList() {
		final String methodName = "4";
		if (filenames == null) {
			log.error(methodName, "Null filenames vector");
			return new String[0];
		}
		String[] list = new String[filenames.size()];
		Enumeration e = filenames.elements();
		int count = 0;
		while (e.hasMoreElements()) {
			list[count++] = String.valueOf(e.nextElement());
		}

		return list;
	}
}
