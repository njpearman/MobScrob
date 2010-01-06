/**
 * ID32TagReader.java
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

import mobscrob.id3.AbstractID3Body.Frame;
import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;
import mobscrob.mp3.InfoUnavailableException;
import mobscrob.mp3.MP3Stream;
import mobscrob.util.StreamUtil;

/**
 * Initial implementation of an ID3v2 tag reader. This parser only parses
 * ID3v2.2 and ID3v2.3 tags and DOES NOT parse ID3v2.4 tags
 * 
 * @author Neill
 * 
 */
public class ID32TagReader {
	private static final Log log = LogFactory.getLogger(ID32TagReader.class);

	private final MP3Stream stream;
	private byte[] headerBytes;

	public ID32TagReader(MP3Stream stream) throws IOException {
		this.stream = stream;
	}

	public void readInto(TrackMetadata data) throws IOException,
			InfoUnavailableException {
		final String methodName = "1";

		try {
			/**
			 * @TODO need to look for tags at the end of files
			 */

			// try to get header from start or end of file
			ID3Header header = null;
			try {
				// ID3v2 header first
				if (header == null) {
					// reset the stream
					stream.reset();
					try {
						header = readID3v2Header();
					} catch (IOException e) {
						log.warn(methodName, "Unable to read ID3v2 header");
					}
				}

				// then ID3v1 header
				if (header == null) {
					stream.reset();
					try {
						header = readID3v1Header();
					} catch (IOException e) {
						log.warn(methodName, "Unable to read ID3v1 header");
					} catch (InfoUnavailableException e) {
						log.warn(methodName, "Unable to read ID3v1 header");
					}
				}

				// finally try ID3v2 footer
				if (header == null) {
					try {
						header = readID3v2Footer(true);
					} catch (IOException e) {
						log.warn(methodName, "Unable to read ID3v2 footer");
					}
				}

				if (header == null) {
					throw new IOException("Unable to read ID3 header");
				}
			} catch (IOException e) {
				throw new IOException("Unable to read ID3v2 tag");
			}

			// check for extended header
			if (header.hasExtendedHeader()) {
				throw new IOException("Extended header not currently supported");
			}

			log.info(methodName, "Got tag header, tag length "
					+ header.bodyLength());

			// now read the bytes for the body
			AbstractID3Body body = AbstractID3Body.instance(header, stream);

			Frame frame;
			long startTime = System.currentTimeMillis();

			while (!body.readComplete()) {
				try {
					frame = body.readNextFrame();

					if (frame != null) {
						// is this frame one of the frames we want..?
						TrackMetadataUtil.addMetadata(data, frame);
					}
				} catch (IOException ex) {
					log.error(methodName,
							"Unable to read frame, continuing to try reading next frame: "
									+ ex.getMessage(), ex);
				}
			}

			log.info(methodName, "Read data " + data + "\r\nTook "
					+ (System.currentTimeMillis() - startTime) + " millis");
		} finally {
			StreamUtil.closeInputStream(stream);
		}
	}

	public ID3Header readID3v1Header() throws IOException,
			InfoUnavailableException {
		final String methodName = "2";

		log.info(methodName, "Attempting to read ID3v1 tag header");

		headerBytes = new byte[ID3v1Header.HEADER_LEN];

		// need to reset stream and then skip to end
		stream.skip(stream.getStreamLength() - ID3v1Header.ID3v1_TAG_LENGTH);

		int byteCount = stream.read(headerBytes);
		if (byteCount != ID3v1Header.HEADER_LEN) {
			throw new IOException(
					"Unable to read first header bytes, byte count "
							+ byteCount);
		}
		log.info(methodName, "Read ID3v1 header from file: "
				+ new String(headerBytes, "UTF-8"));

		ID3Header header = new ID3v1Header(headerBytes);
		header.parse();

		return header;
	}

	public ID3Header readID3v2Header() throws IOException {
		final String methodName = "3";
		log.info(methodName, "Attempting to read ID3v2 tag header..");

		headerBytes = new byte[ID3v2Header.HEADER_LEN];

		// read header (first 10 bytes)
		int byteCount = stream.read(headerBytes);
		if (byteCount != ID3v2Header.HEADER_LEN) {
			throw new IOException(
					"Unable to read first header bytes, byte count "
							+ byteCount);
		}
		log.info(methodName, "Read header from start of file: "
				+ new String(headerBytes, "UTF-8"));

		ID3Header header = new ID3v2Header(headerBytes);
		header.parse();

		return header;
	}

	/**
	 * Attempts to read an ID3v2 tag footer.
	 * 
	 * @TODO this routine is not implemented correctly. It will need some
	 *       refactoring to operate correctly
	 * 
	 * @return
	 * @throws IOException
	 * @throws InfoUnavailableException
	 */
	public ID3Header readID3v2Footer(boolean attemptedHeaderRead)
			throws IOException, InfoUnavailableException {
		final String methodName = "4";
		log.info(methodName, "Attempting to read ID3v2 tag footer..");

		headerBytes = new byte[ID3v2Header.HEADER_LEN];
		long len = stream.getStreamLength();
		long footerStart = len - ID3v2Header.HEADER_LEN;
		if (attemptedHeaderRead) {
			footerStart = -ID3v2Header.HEADER_LEN;
		}

		long skipped = stream.skip(footerStart);
		if (skipped != footerStart) {
			throw new IOException("InputStream skipped " + skipped
					+ ", required to skip " + footerStart);
		}
		log.info(methodName, "Skipped stream to footer start");

		int byteCount = stream.read(headerBytes);
		if (byteCount != 10) {
			throw new IOException("Unable to read footer bytes, byte count "
					+ byteCount);
		}
		log.info(methodName, "Read footer from end of file: "
				+ new String(headerBytes, "UTF-8"));

		ID3Header header = new ID3v2Header(headerBytes);
		header.parse();
		log.info(methodName, "Constructed ID3Header object");

		// now need to reset stream and move to beginning of frames
		stream.reset();
		long framesStart = len - header.bodyLength();
		skipped = stream.skip(framesStart);
		if (skipped != framesStart) {
			throw new IOException("InputStream skipped " + skipped
					+ ", required to skip " + framesStart);
		}
		log.info(methodName, "Set stream to start of frames");

		return header;
	}
}
