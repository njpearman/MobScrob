/**
 * 
 */
package mobscrob.id3;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import mobscrob.event.Listener;
import mobscrob.util.StreamUtil;

/**
 * Contains simple track metadata required for scrobbling audio tracks
 * 
 * @author Neill
 * 
 */
public class TrackMetadata {
	
	private static final TrackMetadataSerializer serializer = 
		new TrackMetadataSerializerImpl();

	public static final long POSITION_END_OF_TRACK = -32;

	private Listener listener;
	private String artist;
	private String albumTitle;
	private long currentPosition;
	private String fileLocation;
	private String musicBrainzID;
	private String trackTitle;
	private long trackLength;
	private String trackNumber;
	private long startTimestamp;
	private boolean postProcessed;
	private boolean invalidID3Tag;
	private int submissionAttempts;

	public TrackMetadata() {
	}

	public TrackMetadata(String fileLocation, long trackLength) {
		this.fileLocation = fileLocation;
		this.trackLength = trackLength;
		this.startTimestamp = new Date().getTime();
		this.postProcessed = false;

		this.albumTitle = "";
		this.artist = "";
		this.currentPosition = 0;
		this.musicBrainzID = "";
		this.trackNumber = "";
		this.trackTitle = "";
		this.submissionAttempts = 0;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist.trim();
	}

	public String getAlbumTitle() {
		return albumTitle;
	}

	public void setAlbumTitle(String albumTitle) {
		this.albumTitle = albumTitle.trim();
	}

	public String getMusicBrainzID() {
		return musicBrainzID;
	}

	public void setMusicBrainzID(String musicBrainsID) {
		this.musicBrainzID = musicBrainsID;
	}

	public long getTrackLength() {
		return trackLength;
	}

	public void setTrackLength(long value) {
		this.trackLength = value;
	}

	public long getTrackLengthInSeconds() {
		return trackLength / 1000l;
	}

	public String getTrackTitle() {
		return trackTitle;
	}

	public void setTrackTitle(String trackTitle) {
		this.trackTitle = trackTitle.trim();
	}

	public String getTrackNumber() {
		return trackNumber;
	}

	public void setTrackNumber(String trackNumber) {
		this.trackNumber = trackNumber.trim();
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String filename) {
		this.fileLocation = filename.trim();
	}

	public long getStartTimestampInSeconds() {
		return (long) (startTimestamp / 1000);
	}

	public long getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public long getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(long currentPosition) {
		this.currentPosition = currentPosition;
	}

	public void setPostProcessed(boolean value) {
		this.postProcessed = value;
	}

	public boolean isPostProcessed() {
		return postProcessed;
	}

	public void setInvalidID3Tag(boolean value) {
		this.invalidID3Tag = value;
	}

	public boolean isInvalidID3Tag() {
		return invalidID3Tag;
	}

	public int getSubmissionAttempts() {
		return this.submissionAttempts;
	}

	public void setSubmissionAttempts(int value) {
		this.submissionAttempts = value;
	}

	public void attemptedSubmit() {
		this.submissionAttempts++;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}
	
	/**
	 * Notifies the listener that an event has occurred. 
	 */
	public void updated() {
		if(listener != null) {
			listener.event();
		} else {
			//log.error(methodName, "No listener waiting for updates on this track");
		}
	}

	public String toString() {
		StringBuffer buf = new StringBuffer("[TrackMetadata [Artist: ");
		buf.append(artist).append("] [Album: ").append(albumTitle).append(
				"] [Track name: ").append(trackTitle).append("] [Track#: ")
				.append(trackNumber).append("] [Location: ").append(
						fileLocation).append("] [Length ").append(trackLength)
				.append("] [Current pos: ").append(currentPosition).append(
						"] [Start timestamp: ").append(startTimestamp).append(
						"] [Submisson attempts: ").append(submissionAttempts)
				.append("]]");

		return buf.toString();
	}

	public static TrackMetadataSerializer getSerializer() {
		return serializer;
	}
	
	public static interface TrackMetadataSerializer {
		public byte[] serialize(TrackMetadata track) throws IOException;
		public TrackMetadata deserialize(InputStream trackStream) throws IOException;
	}
	
	/**
	 * Basic serialization processor for instances of TrackMetadata
	 * 
	 * @author Neill
	 * 
	 */
	private static class TrackMetadataSerializerImpl implements TrackMetadataSerializer {

		public byte[] serialize(TrackMetadata track) throws IOException {
			ByteArrayOutputStream baos;
			DataOutputStream out = null;
			
			try {
				baos = new ByteArrayOutputStream();
				out = new DataOutputStream(baos);
				
				out.writeUTF(track.albumTitle == null ? "" : track.albumTitle);
				out.writeUTF(track.artist == null ? "" : track.artist);
				out.writeLong(track.currentPosition);
				out.writeUTF(track.fileLocation == null ? "" : track.fileLocation);
				out.writeBoolean(track.invalidID3Tag);
				out.writeUTF(track.musicBrainzID == null ? "" : track.musicBrainzID);
				out.writeBoolean(track.postProcessed);
				out.writeLong(track.startTimestamp);
				out.writeInt(track.submissionAttempts);
				out.writeLong(track.trackLength);
				out.writeUTF(track.trackNumber == null ? "" : track.trackNumber);
				out.writeUTF(track.trackTitle == null ? "" : track.trackTitle);
				
				return baos.toByteArray();
			} finally {
				StreamUtil.closeOutputStream(out);
			}
		}

		public TrackMetadata deserialize(InputStream trackStream)
				throws IOException {

			TrackMetadata track = new TrackMetadata();
			track.setInvalidID3Tag(true);

			DataInputStream in = null;
			
			try {
				in = new DataInputStream(trackStream);
				
				track.albumTitle = in.readUTF();
				track.artist = in.readUTF();
				track.currentPosition = in.readLong();
				track.fileLocation = in.readUTF();
				track.invalidID3Tag = in.readBoolean();
				track.musicBrainzID = in.readUTF();
				track.postProcessed = in.readBoolean();
				track.startTimestamp = in.readLong();
				track.submissionAttempts = in.readInt();
				track.trackLength = in.readLong();
				track.trackNumber = in.readUTF();
				track.trackTitle = in.readUTF();
			} finally {
				StreamUtil.closeInputStream(in);
			}
			
			return track;
		}
	}
}
