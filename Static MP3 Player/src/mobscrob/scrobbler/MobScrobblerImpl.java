/**
 * 
 */
package mobscrob.scrobbler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import mobscrob.alert.AlertType;
import mobscrob.alert.Alertable;
import mobscrob.alert.Alerter;
import mobscrob.id3.TrackMetadata;
import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;
import mobscrob.properties.MobScrobProperties;
import mobscrob.util.ByteUtil;
import mobscrob.util.microedition.HTTPUtil;

/**
 * @author Neill
 * 
 */
public class MobScrobblerImpl implements Alerter, MobScrobbler {

	private static final Log log = LogFactory.getLogger(MobScrobblerImpl.class);

	private static final String AUDIOSCROBBLER_URL = "http://post.audioscrobbler.com/";
	private static final String TEST_CLIENT_ID = "tst";
	private static final String TEST_CLIENT_VERSION = "1.0";
	private static final String DEFAULT_HOST = "post.audioscrobbler.com";
	private static final String DEFAULT_USER_AGENT = "MobScrobblerImpl (MIDP2.0)";
	private static final String DEFAULT_POST_CONTENT_TYPE = "application/x-www-form-urlencoded";

	private static final String SCROBBLE_OK = "OK";
	private static final String SCROBBLE_BAD_SESSION = "BADSESSION";
	private static final String SCROBBLE_FAILED = "FAILED";

	private static final String HEADER_ACCEPT = "Accept";
	private static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";
	private static final String HEADER_CONTENT_LENGTH = "Content-Length";
	private static final String HEADER_CONTENT_TYPE = "Content-Type";
	private static final String HEADER_USER_AGENT = "User-Agent";

	// these param names are pre-encoded for submission.
	private static final String ENC_OPEN_BRACKET = "%5b";
	private static final String ENC_CLOSE_BRACKET = "%5d";
	private static final String ENC_EQUALS = "=";
	private static final String PARAM_SUBMIT_ALBUM = "b";
	private static final String PARAM_SUBMIT_ARTIST = "a";
	private static final String PARAM_SUBMIT_LENGTH = "l";
	private static final String PARAM_SUBMIT_MUSICBRAINZ = "m";
	private static final String PARAM_SUBMIT_NUMBER = "n";
	private static final String PARAM_SUBMIT_RATING = "r";
	private static final String PARAM_SUBMIT_SESSION = "s";
	private static final String PARAM_SUBMIT_SOURCE = "o";
	private static final String PARAM_SUBMIT_TIME = "i";
	private static final String PARAM_SUBMIT_TRACK = "t";

	private int failureCount;
	private MobScrobProperties props;
	private Alertable alertable;
	private Session session;
	private boolean processing, shutdown;
	private TrackMetadata holdingTrack;
	
	public MobScrobblerImpl(MobScrobProperties props) {
		this.props = props;
		this.failureCount = 0;
	}

	/* (non-Javadoc)
	 * @see mobscrob.scrobbler.MobScrobbler#getHoldingTrack()
	 */
	public TrackMetadata getHoldingTrack() {
		return this.holdingTrack;
	}
	
	/* (non-Javadoc)
	 * @see mobscrob.scrobbler.MobScrobbler#handshake()
	 */
	public boolean handshake() throws IOException, NoSuchAlgorithmException,
			DigestException {
		final String methodName = "3";
		long timestamp = (long) ((new Date().getTime()) / 1000);

		// create handshake URL
		StringBuffer urlBuf = new StringBuffer(AUDIOSCROBBLER_URL);
		urlBuf.append("?hs=true&").append("p=1.2.1&").append("c=").append(
				TEST_CLIENT_ID).append("&v=").append(TEST_CLIENT_VERSION)
				.append("&u=").append(props.getUsername()).append("&t=")
				.append(timestamp).append("&a=").append(
						MD5Util.md5Hash(props.getHashedPassword() + timestamp));

		String url = urlBuf.toString();
		log.info(methodName, "Attempting to connect to " + url);

		// create HTTPConnection to URL
		byte[] body = HTTPUtil.getUrl(url, DEFAULT_HOST);

		Vector lines = ByteUtil.readLines(body);

		// check is OK
		String ok = (String) lines.elementAt(0);
		if (ok == null) {
			log.error(methodName, "Handshake response should not be null");
		} else if (!"OK".equals(ok)) {
			log.error(methodName, "Handshake response not OK: " + ok);
		}

		// get lines
		String sessionID = (String) lines.elementAt(1);
		String nowPlayingUrl = (String) lines.elementAt(2);
		String submitUrl = (String) lines.elementAt(3);

		session = new Session(sessionID, nowPlayingUrl, submitUrl);
		log.info(methodName, "Got session: " + session);
		try {
			session.validateSessionInfo();
		} catch (ScrobbleException ex) {
			log.error(methodName, "Invalid scrobble session: " + ex.getMessage(), ex);
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see mobscrob.scrobbler.MobScrobbler#scrobble(mobscrob.id3.TrackMetadata)
	 */
	public void scrobble(TrackMetadata track) throws RequeueException, ScrobbleOfflineException {
		final String methodName = "4";
		holdingTrack = track;
		
		HttpConnection conn = null;
		OutputStream os = null;

		// first check if scrobbling offline
		if(props.scrobbleOffline()) {
			throw new ScrobbleOfflineException();
		}
		
		if (track == null) {
			log.error(methodName, "Can't scrobble null track");
			return;
		} else if (track.isInvalidID3Tag()) {
			log.error(methodName, "Can't scrobble track with invalid ID3 tag");
		}

		try {
			if (!validSession()) {
				throw new RequeueException("Unable to establish handshake");
			}

			processing = true;
			holdingTrack = null;
			byte[] paramBytes = createSubmissionParams(track);

			// get host from submit URL
			String submitUrl = session.submitUrl;

			String host = submitUrl.substring(7, submitUrl.lastIndexOf(':'));
			log.info(methodName, "Host: " + host);

			// set up HTTP connection
			conn = (HttpConnection) Connector.open(session.submitUrl);

			conn.setRequestMethod(HttpConnection.POST);
			conn.setRequestProperty(HTTPUtil.HEADER_HOST, host);
			conn.setRequestProperty(HEADER_USER_AGENT, DEFAULT_USER_AGENT);
			conn.setRequestProperty(HEADER_CONTENT_TYPE, DEFAULT_POST_CONTENT_TYPE);
			conn.setRequestProperty(HEADER_CONTENT_LENGTH, String.valueOf(paramBytes.length));
			conn.setRequestProperty(HEADER_ACCEPT, "*/*");
			conn.setRequestProperty(HEADER_ACCEPT_LANGUAGE, "en");
			log.info(methodName, "Set up connection details");

			// try opening output stream first
			os = conn.openOutputStream();

			// try writing byte by byte
			for (int i = 0; i < paramBytes.length; i++) {
				os.write((int) paramBytes[i]);
			}

			log.info(methodName, "Written to stream");

			byte[] response = HTTPUtil.readHttpResponse(conn);
			String responseStatus = new String(response);
			log.info(methodName, "Scrobble response: " + responseStatus);
			failureCount = 0;

			if (responseStatus.indexOf(SCROBBLE_OK) >= 0) {
				log.info(methodName, "Success submitting track - check last.fm!!");
			} else if (responseStatus.indexOf(SCROBBLE_BAD_SESSION) >= 0) {
				log.error(methodName, "Invalid session, requeueing track and invalidating session");
				session.invalidate();
				throw new RequeueException("BADSESSION returned from audioscrobbler");
			} else if (responseStatus.indexOf(SCROBBLE_FAILED) >= 0) {
				log.error(methodName, "Submission failed: " + responseStatus);
				throw new RequeueException("Failure submitting to audioscrobbler");
			} else {
				String msg = "Unexpected response from audioscrobbler: " + responseStatus;
				log.error(methodName, msg);
				throw new RequeueException(msg);
			}
		} catch (IOException ex) {
			failureCount++;
			String msg = "Error scrobbling track: " + ex.getMessage();
			log.error(methodName, msg, ex);
			if (failureCount > 2) {
				session.invalidate();
				failureCount = 0;
			}
			throw new RequeueException(msg);
		} finally {
			track.attemptedSubmit();
			if (os != null) {
				try { os.close(); } 
				catch (Exception e) { log.error(methodName, "Unable to close HTTP output stream: " + e.getMessage(), e); }
			}
			HTTPUtil.closeHttpConnection(conn);
			processing = false;
		}
	}

	private byte[] createSubmissionParams(TrackMetadata track)
			throws UnsupportedEncodingException {
		final String methodName = "5";
		StringBuffer paramsBuf = new StringBuffer();
		paramsBuf.append(PARAM_SUBMIT_SESSION).append('=').append(
				HTTPUtil.encodeParam(session.sessionID)).append('&');

		appendSubmissionParam(paramsBuf, PARAM_SUBMIT_ALBUM, 0).append(
				HTTPUtil.encodeParam(track.getAlbumTitle())).append('&');
		appendSubmissionParam(paramsBuf, PARAM_SUBMIT_ARTIST, 0).append(
				HTTPUtil.encodeParam(track.getArtist())).append('&');
		appendSubmissionParam(paramsBuf, PARAM_SUBMIT_LENGTH, 0).append(
				String.valueOf(track.getTrackLengthInSeconds())).append('&');
		appendSubmissionParam(paramsBuf, PARAM_SUBMIT_MUSICBRAINZ, 0)
				.append("").append('&');
		appendSubmissionParam(paramsBuf, PARAM_SUBMIT_NUMBER, 0).append(
				String.valueOf(track.getTrackNumber())).append('&');
		appendSubmissionParam(paramsBuf, PARAM_SUBMIT_RATING, 0).append("")
				.append('&');
		appendSubmissionParam(paramsBuf, PARAM_SUBMIT_SOURCE, 0).append("P")
				.append('&');
		appendSubmissionParam(paramsBuf, PARAM_SUBMIT_TIME, 0).append(
				String.valueOf(track.getStartTimestampInSeconds())).append('&');
		appendSubmissionParam(paramsBuf, PARAM_SUBMIT_TRACK, 0).append(
				HTTPUtil.encodeParam(track.getTrackTitle()));

		String params = paramsBuf.toString();
		byte[] paramBytes = params.getBytes();

		// need to encode URL
		log.info(methodName, "Generated request params: " + params);

		return paramBytes;
	}

	private StringBuffer appendSubmissionParam(StringBuffer buf, String paramName, int count) {
		buf.append(paramName).append(ENC_OPEN_BRACKET).append(count).append(ENC_CLOSE_BRACKET).append(ENC_EQUALS);
		return buf;
	}

	public void alert(AlertType type, String msg) {
		if (alertable != null) {
			alertable.alert(type, msg);
		}
	}

	public void setAlertable(Alertable alertable) {
		this.alertable = alertable;
	}

	private boolean validSession() {
		final String methodName = "7";
		if (session == null || session.invalid()) {

			log.error(methodName, "Session is invalid, attempting handshake");
			while ((session == null || session.invalid()) && !shutdown) {
				int retryTime = session == null ? 0 : session.getRetryWaitTime();
				log.info(methodName, "Waiting for next handshake attempt in " + retryTime + "ms");
				// wait five seconds if unsuccessful
				try { Thread.sleep(session.getRetryWaitTime()); } 
				catch (Exception e) {}

				try {
					if (handshake()) {
						session.resetWaitTime();
						break;
					}
				} catch (Exception ex) {
					log.error(methodName, "Handshake error: " + ex.getMessage(), ex);
					if (session == null) {
						log.info(methodName, "Session null, creating invalid session");
						session = new Session();
					}
					session.increaseWaitTime();
				}
			}

			if (session == null || session.invalid()) {
				String msg = "Handshake failed, unable to scrobble";
				log.info(methodName, msg);
				return false;
			}
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see mobscrob.scrobbler.MobScrobbler#shutdown()
	 */
	public void shutdown() {
		final String methodName = "8";
		shutdown = true;
		try {
			while (processing) {
				log.info(methodName, "Can't shutdown, still scrobbling");
				Thread.sleep(5000);
			}
		} catch (InterruptedException e) {
			log.error(methodName, "Interrupted while waiting to finish processing.", e);
		}
	}

	/**
	 * Represents an AudioScrobbler session
	 * 
	 * @author Neill
	 * 
	 */
	private static class Session {
		private static final int MAX_RETRY = 120 * 60 * 1000;
		private static final int ONE_MINUTE = 60 * 1000;

		private final String sessionID;
		private final String nowPlayingUrl;
		private final String submitUrl;

		private int retryWaitTime;
		private boolean invalid;

		/**
		 * Creates an invalid Session, with null values for
		 */
		public Session() {
			this(null, null, null);
			invalid = true;
		}

		public Session(String sessionID, String nowPlayingUrl, String submitUrl) {
			this.sessionID = sessionID;
			this.nowPlayingUrl = nowPlayingUrl;
			this.submitUrl = submitUrl;
			this.retryWaitTime = 0;
		}

		/**
		 * Validates the session information. Method call isn't compulsory but
		 * is recommended before attempts to scrobble are made.
		 */
		public void validateSessionInfo() throws ScrobbleException {
			if (invalid) {
				return;
			}
			if (sessionID == null) {
				// error
				invalid = true;
				throw new ScrobbleException("Session ID is null");
			} else if (nowPlayingUrl == null) {
				// error
				invalid = true;
				throw new ScrobbleException("Now playing URL is null");
			} else if (submitUrl == null) {
				// error
				invalid = true;
				throw new ScrobbleException("Submission URL is null");
			}

			invalid = false;
		}

		public boolean invalid() {
			return invalid;
		}

		public void invalidate() {
			invalid = true;
		}

		public int getRetryWaitTime() {
			return retryWaitTime;
		}

		public void increaseWaitTime() {
			if (retryWaitTime == 0) {
				retryWaitTime = ONE_MINUTE;
			} else if (retryWaitTime < MAX_RETRY) {
				retryWaitTime *= 2;
			}
		}

		public void resetWaitTime() {
			retryWaitTime = 0;
		}

		public String toString() {
			StringBuffer buf = new StringBuffer(
					"[MobScrobblerImpl.Session [Session ID: ");
			buf.append(sessionID).append("] [Now playing URL: ").append(
					nowPlayingUrl).append("] [Submit URL: ").append(submitUrl)
					.append("] [Valid? ").append(!invalid).append("]]");
			return buf.toString();
		}
	}

	private static class ScrobbleException extends Exception {
		ScrobbleException(String msg) {
			super(msg);
		}
	}
}