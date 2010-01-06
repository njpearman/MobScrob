/**
 * SystemRootNode.java
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

import javax.microedition.io.file.FileSystemRegistry;

import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;

/**
 * @author Neill
 * 
 */
public class SystemRootNode extends DirectoryNode {
	private static final Log log = LogFactory.getLogger(SystemRootNode.class);

	private static final String SYSTEM_ROOT_NAME = "file:///";

	public SystemRootNode(DirectoryNodeManager manager) {
		super(SYSTEM_ROOT_NAME, null, manager);
	}

	public void loadChildrenAndFiles() throws IOException {
		final String methodName = "1";
		log.info(methodName, "Loading file system root...");
		// load roots from file registry
		Enumeration roots = FileSystemRegistry.listRoots();

		children = new Vector();
		filenames = new Vector();
		addSorted(roots);

		log.info(methodName, "Number of roots loaded: " + children.size());
	}
}
