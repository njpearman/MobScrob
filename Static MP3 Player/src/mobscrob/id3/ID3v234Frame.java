/**
 * 
 */
package mobscrob.id3;

import mobscrob.id3.AbstractID3Body.Frame;

/**
 * @author Neill
 * 
 */
public class ID3v234Frame extends Frame {

	public static final String ID_ALBUM_TITLE = "TALB";
	public static final String ID_TRACK_TITLE = "TIT2";
	public static final String ID_ARTIST = "TPE1";
	public static final String ID_TRACK_NUMBER = "TRCK";
	public static final String ID_RECORDING_TIME = "TDRC";
	public static final String ID_CONTENT_TYPE = "TCON";

	public ID3v234Frame(AbstractID3Body body, String id, int length, byte[] raw) {
		body.super(id, length, raw);
	}
}
