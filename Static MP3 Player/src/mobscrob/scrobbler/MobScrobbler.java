package mobscrob.scrobbler;

import java.io.IOException;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;

import mobscrob.id3.TrackMetadata;

public interface MobScrobbler {

	public TrackMetadata getHoldingTrack();

	/**
	 * Attempts a handshake with last.fm server to start a scrobble session
	 */
	public boolean handshake() throws IOException, NoSuchAlgorithmException,
			DigestException;

	/**
	 * Attempts to submit the TrackMetadata to last.fm in the current scrobble
	 * session.
	 * 
	 * @param track
	 * @throws IOException
	 */
	public void scrobble(TrackMetadata track) throws RequeueException,
			ScrobbleOfflineException;

	public void shutdown();

}