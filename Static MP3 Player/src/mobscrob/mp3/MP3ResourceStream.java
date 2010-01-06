/**
 * MP3ResourceStream.java
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
package mobscrob.mp3;

import java.io.IOException;
import java.io.InputStream;

import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;

/**
 * @author Neill
 * 
 */
public class MP3ResourceStream extends MP3Stream {
	private static final Log log = LogFactory.getLogger(MP3ResourceStream.class);

	public MP3ResourceStream(String resourceName) throws IOException {
		super();
		final String methodName = "1";
		if (!resourceName.endsWith(MP3_EXTENSION)) {
			throw new IOException("File not MP3 format");
		}

		// get stream from file
		int index = resourceName.indexOf(':');
		String trimmed = resourceName.substring(index + 1);
		log.debug(methodName, "Getting jar resource " + trimmed);
		InputStream is = this.getClass().getResourceAsStream(trimmed);
		setInputStream(is);
		log.debug(methodName, "Got resource as stream, "
				+ (is == null ? "stream is null" : "stream not null"));
	}

	public long getStreamLength() throws InfoUnavailableException {
		throw new InfoUnavailableException(
				"Unable to determine length of stream");
	}
}
