/**
 * MP3FileStream.java
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

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

/**
 * @author Neill
 * 
 */
public class MP3FileStream extends MP3Stream {

	private final String filename;

	private long fileSize;

	public MP3FileStream(String filename) throws IOException {
		super();

		this.filename = filename;
		openStream(filename);
	}

	public synchronized void reset() throws IOException {
		// have to close stream then reopen it
		this.close();
		this.openStream(filename);
	}

	private void openStream(String filename) throws IOException {
		if (!filename.endsWith(MP3_EXTENSION)) {
			throw new IOException("File not MP3 format");
		}

		FileConnection fc = null;
		// get stream from file
		try {
			fc = (FileConnection) Connector.open(filename);
			if (!fc.exists()) {
				throw new IOException("File does not exist");
			}

			fileSize = fc.fileSize();

			setInputStream(fc.openInputStream());
		} finally {
			if (fc != null) {
				try {
					fc.close();
				} catch (Exception ex) {
				}
			}
		}
	}

	/**
	 * Return the length of the stream in bytes, as defined by the
	 * FileConnection object used to open the stream.
	 * 
	 * @return
	 */
	public long getStreamLength() throws InfoUnavailableException {
		return fileSize;
	}
}
