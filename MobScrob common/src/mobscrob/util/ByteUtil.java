/**
 * ByteUtil.java
 * @date 30 Sep 2008
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
package mobscrob.util;

import java.util.Vector;

/**
 * 
 */

/**
 * @author Neill
 * 
 */
public class ByteUtil {

	/**
	 * Utility class so private constructor
	 */
	private ByteUtil() {
	}

	/**
	 * Returns a Vector of Strings representing the lines present in the byte
	 * array, separated by '\n' return feed.
	 * 
	 * @param bytes
	 * @return
	 */
	public static Vector readLines(byte[] bytes) {
		//final String methodName = "1";
		Vector lines = new Vector();
		if (bytes == null || bytes.length == 0) {
			return lines;
		}
		StringBuffer line = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] == '\n') {
				// new line
				String nextLine = line.toString();
				//log.info(methodName, "Got line: " + nextLine);
				lines.addElement(nextLine);
				line = new StringBuffer();
			} else {
				// add to current line
				line.append((char) bytes[i]);
			}
		}

		return lines;
	}

	public static void replaceSpaceWithPlus(byte[] bytes) {
		if (bytes != null) {
			for (int i = 0; i < bytes.length; i++) {
				if (bytes[i] == ' ') {
					bytes[i] = '+';
				}
			}
		}
	}
}
