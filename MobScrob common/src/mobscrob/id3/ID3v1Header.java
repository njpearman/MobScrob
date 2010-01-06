/**
 * ID3v1Header.java
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
package mobscrob.id3;

import java.io.IOException;

/**
 * @author Neill
 * 
 */
public class ID3v1Header implements ID3Header {

	public static final int HEADER_LEN = 3;
	public static final int ID3v1_TAG_LENGTH = 128;

	private byte[] raw;

	public ID3v1Header(byte[] raw) {
		this.raw = raw;
	}

	public long bodyLength() {
		return ID3v1_TAG_LENGTH - 3;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mobscrob.id3.ID3Header#hasExtendedHeader()
	 */
	public boolean hasExtendedHeader() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mobscrob.id3.ID3Header#hasUnchronization()
	 */
	public boolean hasUnchronization() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mobscrob.id3.ID3Header#majorVersion()
	 */
	public int majorVersion() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mobscrob.id3.ID3Header#parse()
	 */
	public void parse() throws IOException {
		// check that header contains T A G

		if (raw[0] != 'T' || raw[1] != 'A' || raw[2] != 'G') {
			throw new IOException("Invalid ID3v1 tag header");
		}
	}

}
