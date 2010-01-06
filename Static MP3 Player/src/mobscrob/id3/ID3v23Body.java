/**
 * ID3v23Body.java 
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

import mobscrob.mp3.MP3Stream;

/**
 * The ID3v2.3 implementation of the ID3Body class
 * 
 * @author Neill
 * 
 */
public class ID3v23Body extends AbstractID3Body {
	private static final int ID323_FRAME_HEADER_LEN = 10;
	private static final int INVALID_FLAGS = (1 << 5) - 1;

	protected ID3v23Body(ID3Header header, MP3Stream is) {
		super(header, is);
	}

	public Frame readNextFrame() throws IOException {
		// read first ten bytes from input stream
		byte[] frameH = new byte[ID323_FRAME_HEADER_LEN];

		int byteCount;
		byteCount = is.read(frameH);
		if (byteCount != ID323_FRAME_HEADER_LEN) {
			throw ID3Exception.INCORRECT_FRAME_HEADER_LENGTH;
		}

		char[] id = new char[4];
		id[0] = (char) frameH[0];
		id[1] = (char) frameH[1];
		id[2] = (char) frameH[2];
		id[3] = (char) frameH[3];

		String header = new String(id);
		int frameLen = SynchsafeInteger.valueOf(new byte[] { frameH[4],
				frameH[5], frameH[6], frameH[7] });

		// read flags
		byte statusFlag = frameH[8];
		byte encFlag = frameH[9];

		if ((statusFlag & INVALID_FLAGS) > 0 || (encFlag & INVALID_FLAGS) > 0) {
			throw ID3Exception.INVALID_FRAME_HEADER_FLAGS;
		}

		// has extended header..?
		if ((statusFlag & 255) > 0 || (encFlag & 255) > 0) {
			throw ID3Exception.UNSUPPORTED_FORMAT;
		}

		if(ID3v234Frame.ID_ARTIST.equals(header) || 
		   ID3v234Frame.ID_ALBUM_TITLE.equals(header) ||
		   ID3v234Frame.ID_TRACK_NUMBER.equals(header) ||
		   ID3v234Frame.ID_TRACK_TITLE.equals(header) ||
		   ID3v234Frame.ID_RECORDING_TIME.equals(header)) {
			return new Frame(new String(id), frameLen, readRawFrameBytes(frameLen));
		} else {
			is.skip(frameLen);
			return new Frame(new String(id), frameLen, new byte[0]);
		}

	}
}
