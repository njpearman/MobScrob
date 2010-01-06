/**
 * ID3v2Header
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
 * Parses and represents an ID3v2 header. Note that this header only conforms to
 * ID3v2.2 and ID3v2.3 and NOT ID3v2.4
 * 
 * @author Neill
 */
public class ID3v2Header implements ID3Header {

	public static final int HEADER_LEN = 10;

	public static final int VERSION_2_2 = 2;
	public static final int VERSION_2_3 = 3;
	public static final int VERSION_2_4 = 4;

	private static final int FLAG_UNSYNCHRONIZATION = 1 << 7;
	private static final int FLAG_EXTENDED_HEADER = 1 << 6;
	private static final int FLAG_EXPERIMENTAL_HEADER = 1 << 5;
	private static final int FLAG_FOOTER = 1 << 4;

	private static final int INVALID_FLAGS = 7;

	private byte[] raw;

	private int majorVersion;
	private int revision;
	private long bodyLength;

	private boolean hasUnchronization;
	private boolean hasExtendedHeader;

	/**
	 * Constructs an ID3Header with the specified raw header data
	 * 
	 * @param raw
	 */
	public ID3v2Header(byte[] raw) throws IOException {
		if (raw.length != 10) {
			throw new IOException("Header data must be ten bytes long");
		}
		this.raw = raw;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mobscrob.id3.IID3Header#parse()
	 */
	public void parse() throws IOException {
		verifyIsID3();
		readVersion();
		parseFlags();
		bodyLength = determineTagLength();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mobscrob.id3.IID3Header#bodyLength()
	 */
	public long bodyLength() {
		return bodyLength;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mobscrob.id3.IID3Header#majorVersion()
	 */
	public int majorVersion() {
		return this.majorVersion;
	}

	/**
	 * Verifies that the header starts with the bytes I, D and 3
	 * 
	 * @throws IOException
	 */
	protected void verifyIsID3() throws IOException {
		if (raw[0] != 0x49) { // I
			throw new IOException("Invalid ID3 header, not got I: "
					+ (char) raw[0]);
		} else if (raw[1] != 0x44) { // D
			throw new IOException("Invalid ID3 header, not got D: "
					+ (char) raw[1]);
		} else if (raw[2] != 0x33) { // 3
			throw new IOException("Invalid ID3 header, not got 3: "
					+ (char) raw[2]);
		}
	}

	/**
	 * Reads the major version and revision from the header bytes. Major
	 * versions supported are 2 and 3. Revision number should always be 0 (zero)
	 * 
	 * @throws IOException
	 */
	protected void readVersion() throws IOException {
		majorVersion = raw[3];
		revision = raw[4];

		if (majorVersion < 2 || majorVersion > 4) {
			throw new IOException("Unsupported major version " + majorVersion);
		} else if (revision != 0) {
			throw new IOException("Revision number not zero");
		}
	}

	/**
	 * Parses the flags in the header
	 * 
	 * @throws IOException
	 */
	protected void parseFlags() throws IOException {
		long flags = raw[5];

		if ((flags & FLAG_UNSYNCHRONIZATION) > 0) {
			hasUnchronization = true;
			flags ^= FLAG_UNSYNCHRONIZATION;
		}
		if (majorVersion > 2 && (flags & FLAG_EXTENDED_HEADER) > 0) {
			hasExtendedHeader = true;
			flags ^= FLAG_EXTENDED_HEADER;
		}
		if (majorVersion > 3 && (flags & FLAG_EXPERIMENTAL_HEADER) > 0) {
			flags ^= FLAG_EXPERIMENTAL_HEADER;

		}
		if (majorVersion > 3 && (flags & FLAG_FOOTER) > 0) {
			flags ^= FLAG_FOOTER;
		}
		if ((flags & INVALID_FLAGS) > 0) {
			throw new IOException("Unexpected flags in header");
		}
	}

	/**
	 * Determines the length of the tag from the header bytes and returns this
	 * as a long. The maximum value is (2^7)-1 If the header contains invalid
	 * values for the tag length and IOException is thrown.
	 * 
	 * @return
	 * @throws IOException
	 */
	protected long determineTagLength() throws IOException {
		return SynchsafeInteger.valueOf(new byte[] { raw[6], raw[7], raw[8],
				raw[9] });
	}

	/**
	 * Need a protected setter for unit testing
	 * 
	 * @param value
	 */
	protected void setMajorVersion(int value) {
		this.majorVersion = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mobscrob.id3.IID3Header#hasUnchronization()
	 */
	public boolean hasUnchronization() {
		return hasUnchronization;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mobscrob.id3.IID3Header#hasExtendedHeader()
	 */
	public boolean hasExtendedHeader() {
		return hasExtendedHeader;
	}
}
