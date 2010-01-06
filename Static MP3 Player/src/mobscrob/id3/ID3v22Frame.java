/**
 * 
 */
package mobscrob.id3;

import mobscrob.id3.AbstractID3Body.Frame;

/**
 * @author Neill
 * 
 */
public class ID3v22Frame extends Frame {

	public static final String ID_ALBUM_TITLE = "TAL";
	public static final String ID_TRACK_TITLE = "TT2";
	public static final String ID_ARTIST = "TP1";
	public static final String ID_TRACK_NUMBER = "TRK";
	public static final String ID_CONTENT_TYPE = "TCO";

	public ID3v22Frame(AbstractID3Body body, String id, int length, byte[] raw) {
		body.super(id, length, raw);
	}
}
