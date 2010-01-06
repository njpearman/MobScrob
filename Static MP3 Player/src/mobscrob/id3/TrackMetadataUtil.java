/**
 * TrackMetadataUtil.java
 * @author NJ Pearman
 * @date 19 Oct 2008
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
