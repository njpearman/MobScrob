/**
 * AbstractID3Body.java
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

import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;
import mobscrob.mp3.MP3Stream;

/**
 * @author Neill
 * 
 */
public abstract class AbstractID3Body {
	private static final Log log = LogFactory.getLogger(AbstractID3Body.class);

	private final ID3Header header;

	/** The stream to read the body tags from **/
	protected MP3Stream is;
	private boolean readComplete;

	public AbstractID3Body(ID3Header header, MP3Stream is) {
		this.header = header;
		this.is = is;
		this.readComplete = false;
	}

	public abstract Frame readNextFrame() throws IOException;

	public boolean readComplete() {
		if (is.currentPos() > header.bodyLength()) {
			readComplete = true;
		}
		return readComplete;
	}

	/**
	 * @param frameLen
	 * @return
	 * @throws IOException
	 */
	protected byte[] readRawFrameBytes(int frameLen) throws IOException {
		final String methodName = "1";

		byte[] rawBytes = new byte[frameLen];
		int byteCount = is.read(rawBytes);
		if (byteCount < 0) {
			log.info(methodName, "End of stream while reading frame");
			readComplete = true;
		} else if (byteCount != frameLen) {
			throw ID3Exception.UNEXPECTED_BYTE_READ_COUNT;
		}
		return rawBytes;
	}

	public static AbstractID3Body instance(ID3Header header, MP3Stream is) {
		final String methodName = "2";

		// return the appropriate ID3Body implementation
		// for the version number

		if (header instanceof ID3v1Header) {
			return new ID3v1Body(header, is);
		} else {
			switch (header.majorVersion()) {
			case ID3v2Header.VERSION_2_2:
				log.info(methodName, "v2.2");
				return new ID3v22Body(header, is);
			case ID3v2Header.VERSION_2_3:
				log.info(methodName, "v2.3");
				return new ID3v23Body(header, is);
			case ID3v2Header.VERSION_2_4:
				log.info(methodName, "v2.4");
				return new ID3v24Body(header, is);
			default:
				return null;
			}
		}
	}

	/**
	 * A frame of the tag body
	 * 
	 * @author Neill
	 * 
	 */
	public class Frame {
		private final String id;
		private final int length;
		protected final byte[] rawBytes;

		public Frame(String id, int length, byte[] raw) {
			this.id = id;
			this.length = length;
			this.rawBytes = raw;
		}

		public String getId() {
			return this.id;
		}

		public int getLength() {
			return this.length;
		}

		/**
		 * Returns the raw bytes as a String, determining whether the content is
		 * 8-bit or 16-bit little endian. Any other format is unsupported.
		 * 
		 * @return
		 */
		public String getContentsAsString() {
			final String methodName = "3";
			if(rawBytes.length < 1) {
				log.info(methodName, "No content for tag "+id);
				return "N/A";
			}
			// check whether double byte
			if ((rawBytes[1] & 0xFF) == 0xFF && (rawBytes[2] & 0xFF) == 0xFE) {
				log.info(methodName, "Have little endian 16-bit frame");

				// have little endian, so pull out the odd bytes
				int length = (rawBytes.length - 3) / 2;
				byte[] leBytes = new byte[length];
				for (int i = 0; i < leBytes.length; i++) {
					int pos = (2 * i) + 3;
					if (pos >= rawBytes.length) {
						log.error(methodName,
								"Little endian double byte array index out of bounds: "
										+ pos);
					} else {
						leBytes[i] = rawBytes[pos];
					}
				}
				return new String(leBytes);
			} else {
				// need to ignore first byte for some reason...
				return new String(rawBytes, 1, rawBytes.length - 1);
			}
		}
	}

//	public class ID3v232Frame extends Frame {
//
//		private boolean compressed;
//		private final int decompressedSize;
//		private final byte encType;
//		private final byte grouping;
//
//		public ID3v232Frame(String frameId, int frameLen, byte[] raw,
//				boolean compressed, int decompressedSize, byte encType,
//				byte grouping) {
//			super(frameId, frameLen, raw);
//			this.compressed = compressed;
//			this.decompressedSize = decompressedSize;
//			this.encType = encType;
//			this.grouping = grouping;
//		}
//
//	}
}
