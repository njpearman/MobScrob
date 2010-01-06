/**
 * MP3Stream.java
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

/**
 * @author Neill
 * 
 */
public abstract class MP3Stream extends InputStream {

	private static final String E_NOT_INITIALISED = "Input stream has not been initialised";

	public static final String MP3_EXTENSION = ".mp3";

	private InputStream is;

	private int currentPos;

	public MP3Stream() {
		currentPos = 0;
	}

	protected void setInputStream(InputStream is) {
		if (this.is == null) {
			this.is = is;
		} else {
			try { this.is.close(); } 
			catch (Exception e) {}
			this.is = is;
		}
	}

	public abstract long getStreamLength() throws InfoUnavailableException;

	public int currentPos() {
		return this.currentPos;
	}

	public int read() throws IOException {
		if (is == null) {
			throw new IOException(E_NOT_INITIALISED);
		} else {
			int i = is.read();
			currentPos++;
			return i;
		}
	}

	public int read(byte[] b) throws IOException {
		if (is == null) {
			throw new IOException(E_NOT_INITIALISED);
		} else {
			int num = is.read(b);
			currentPos += b.length;
			return num;
		}
	}

	public long skip(long n) throws IOException {
		if (is == null) {
			throw new IOException(E_NOT_INITIALISED);
		} else {
			long skipped = is.skip(n);
			currentPos += n;
			return skipped;
		}
	}

	public synchronized void reset() throws IOException {
		throw new IOException("reset() method not implemented");
	}

	public void close() throws IOException {
		if (is != null) {
			is.close();
		}
	}

	public static final MP3Stream instance(String location) throws IOException {
		if (location == null) {
			// error
			throw new IOException("Location was given as null");
		} else if (location.indexOf("file") == 0) {
			// have file
			return new MP3FileStream(location);
		} else if (location.indexOf("resource") == 0) {
			// have resource
			return new MP3ResourceStream(location);
		}
		throw new IOException("Unexpected MP3 location type: " + location);
	}
}
