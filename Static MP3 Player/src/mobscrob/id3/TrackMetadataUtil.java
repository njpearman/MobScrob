/**
 * TrackMetadataUtil.java
 * @author NJ Pearman
 * @date 19 Oct 2008
 */
package mobscrob.id3;

import mobscrob.id3.AbstractID3Body.Frame;
import mobscrob.id3.ID3v1Body.ID3v1Frame;
import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;

/**
 * @author Neill
 *
 */
public class TrackMetadataUtil {
	
	private static final Log log = LogFactory.getLogger(TrackMetadataUtil.class);
	
	private TrackMetadataUtil() {}

	public static void addMetadata(TrackMetadata trackMetadata, Frame frame) {
		final String methodName = "1";
		String id = frame.getId();
		if (ID3v234Frame.ID_ALBUM_TITLE.equals(id)
				|| ID3v22Frame.ID_ALBUM_TITLE.equals(id)
				|| ID3v1Frame.ID_ALBUM_TITLE.equals(id)) {
			trackMetadata.setAlbumTitle(frame.getContentsAsString());
		} else if (ID3v234Frame.ID_ARTIST.equals(id)
				|| ID3v22Frame.ID_ARTIST.equals(id)
				|| ID3v1Frame.ID_ARTIST.equals(id)) {
			trackMetadata.setArtist(frame.getContentsAsString());
		} else if (ID3v234Frame.ID_TRACK_TITLE.equals(id)
				|| ID3v22Frame.ID_TRACK_TITLE.equals(id)
				|| ID3v1Frame.ID_TRACK_TITLE.equals(id)) {
			trackMetadata.setTrackTitle(frame.getContentsAsString());
		} else if (ID3v234Frame.ID_TRACK_NUMBER.equals(id)
				|| ID3v22Frame.ID_TRACK_NUMBER.equals(id)
				|| ID3v1Frame.ID_TRACK_NUMBER.equals(id)) {
			trackMetadata.setTrackNumber(frame.getContentsAsString());
		} else if (ID3v234Frame.ID_RECORDING_TIME.equals(id)) {
			log.warn(methodName, "Not yet implemented - using Player.getDuration()");
		}
	}
}
