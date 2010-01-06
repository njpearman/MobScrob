/**
 * 
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
public class ID3v1Body extends AbstractID3Body {
	private static final Log log = LogFactory.getLogger(ID3v1Body.class);

	protected static final int TRACK_TITLE_LEN = 30;
	protected static final int ARTIST_LEN = 30;
	protected static final int ALBUM_TITLE_LEN = 30;
	protected static final int YEAR_LEN = 4;
	protected static final int COMMENT_LEN = 29;
	protected static final int TRACK_NUMBER_LEN = 1;
	protected static final int GENRE_LEN = 1;

	private int currFrame;

	public ID3v1Body(ID3Header header, MP3Stream is) {
		super(header, is);

		// set the current frame to the first frame
		currFrame = ID3v1Frame.TRACK_TITLE;
	}

	public boolean readComplete() {
		return currFrame > ID3v1Frame.GENRE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mobscrob.id3.AbstractID3Body#readNextFrame()
	 */
	public Frame readNextFrame() throws IOException {
		final String methodName = "1";

		if (currFrame > ID3v1Frame.GENRE) {
			log.info(methodName, "Finished reading tag");
			return null;
		}

		Frame frame = null;

		// read the current frame
		switch (currFrame) {
		case ID3v1Frame.TRACK_TITLE:
			frame = readFrame(ID3v1Frame.ID_TRACK_TITLE, TRACK_TITLE_LEN);
			break;
		case ID3v1Frame.ARTIST:
			frame = readFrame(ID3v1Frame.ID_ARTIST, ARTIST_LEN);
			break;
		case ID3v1Frame.ALBUM:
			frame = readFrame(ID3v1Frame.ID_ALBUM_TITLE, ALBUM_TITLE_LEN);
			break;
		case ID3v1Frame.YEAR:
			frame = readFrame(ID3v1Frame.ID_YEAR, YEAR_LEN);
			break;
		case ID3v1Frame.COMMENT:
			frame = readFrame(ID3v1Frame.ID_COMMENT, COMMENT_LEN);
			break;
		case ID3v1Frame.TRACK_NUMBER:
			frame = readSingleByteAsIntFrame(ID3v1Frame.ID_TRACK_NUMBER);
			break;
		case ID3v1Frame.GENRE:
			frame = readSingleByteAsIntFrame(ID3v1Frame.ID_GENRE);
			break;
		default:
			log.error(methodName, "Unexpected current frame: " + currFrame);
		}

		// shift bit for next read
		currFrame = currFrame << 1;
		return frame;
	}

	private Frame readFrame(String frameID, int frameLen) throws IOException {
		final String methodName = "2";
		byte[] bytes = new byte[frameLen];
		int byteCount = is.read(bytes);
		if (byteCount != frameLen) {
			throw new IOException("Unable to read tag frame " + frameID
					+ ", num bytes read: " + byteCount);
		}
		log.info(methodName, "Read ID3v1 frame: " + new String(bytes));
		return new ID3v1Frame(frameID, frameLen, bytes);
	}

	private Frame readSingleByteAsIntFrame(String frameID) throws IOException {
		final String methodName = "3";
		byte[] bytes = new byte[1];
		int next = is.read();
		if (next < 0) {
			throw new IOException("Unable to read single byte as integer "
					+ frameID + ", end of stream");
		}
		log.info(methodName, "Read ID3v1 frame: " + new String(bytes));

		String str = String.valueOf(next);
		return new ID3v1Frame(frameID, 1, str.getBytes());
	}

	public class ID3v1Frame extends Frame {

		public static final String ID_ARTIST = "ART";
		public static final String ID_TRACK_TITLE = "TRA";
		public static final String ID_ALBUM_TITLE = "ALB";
		public static final String ID_YEAR = "YEA";
		public static final String ID_COMMENT = "COM";
		public static final String ID_TRACK_NUMBER = "NUM";
		public static final String ID_GENRE = "GEN";

		static final int TRACK_TITLE = 2;
		static final int ARTIST = 4;
		static final int ALBUM = 8;
		static final int YEAR = 16;
		static final int COMMENT = 32;
		static final int TRACK_NUMBER = 64;
		static final int GENRE = 128;

		public ID3v1Frame(String id, int length, byte[] raw) {
			super(id, length, raw);
		}

		public String getContentsAsString() {
			String s = new String(rawBytes);
			int i = s.indexOf(0x00);
			// ignore any empty content
			if (i > -1) {
				s = s.substring(0, i);
			}
			return s;
		}

	}
}
