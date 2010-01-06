/**
 * ScrobbleProcessor.java
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
package mobscrob.player;

import mobscrob.id3.TrackMetadata;
import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;
import mobscrob.scrobbler.MobScrobbler;
import mobscrob.scrobbler.RequeueException;
import mobscrob.scrobbler.ScrobbleOfflineException;

/**
 * @author Neill
 * 
 */

public class ScrobbleProcessor extends PlayProcessorImpl {

	private static final Log log = LogFactory.getLogger(ScrobbleProcessor.class);

	private static final long MINIMUM_PLAY_TIME = 240000;
	private static final long MINIMUM_TRACK_LENGTH = 30000;

	public static final String DATA_FILE = "file:///e:/mobscrob.data";

	/**
	 * Scrobbling should wait 5 seconds before next submission
	 */
	private static final int WAIT_TIME = 5000;

	private MobScrobbler scrobbler;

	public ScrobbleProcessor(MobScrobbler scrobbler) {
		super(WAIT_TIME);
		this.scrobbler = scrobbler;
	}

	public void queueTrack(TrackMetadata track) {
		final String methodName = "2";
		long halfTrackLen = (long) (track.getTrackLength() / 2);
		long minPlayTime = halfTrackLen >= MINIMUM_PLAY_TIME ? MINIMUM_PLAY_TIME : halfTrackLen;
		if (track.getTrackLength() < MINIMUM_TRACK_LENGTH) {
			log.warn(methodName, "Not queuing, track less than 30secs long");
		} else if (minPlayTime > track.getCurrentPosition()) {
			log.warn(methodName, "Not queuing, track not played for at least 4mins or half of track length");
			track.setPostProcessed(false);
		} else if (track.isInvalidID3Tag()) {
			log.info(methodName, "Not queuing track with invalid ID3 tag");
		} else {
			log.info(methodName, "Queuing track: "+track);
			super.queueTrack(track);
		}
	}

	public void process(TrackMetadata next) {
		final String methodName = "3";
		// use tag info to call last.fm, but only if tag info is valid
		if (scrobbler != null) {
			try {
				scrobbler.scrobble(next);
				log.info(methodName, "Scrobbled track " + next);
			} catch (RequeueException e) {
				log.error(methodName, "Requeuing track: " + e.getMessage(), e);
				log.error(methodName, "Scrobble attempts: " + next.getSubmissionAttempts());
				queueAtStart(next);
			} catch(ScrobbleOfflineException e) {
				log.info(methodName, "Stopping real time scrobbling");
				queueAtStart(next);
				stopProcessing();
			}
		} else {
			log.error(methodName, "Unable to scrobble - MobScrobblerImpl is null!!");
		}
	}

	public void shutdown() {
		final String methodName = "4";
		TrackMetadata track = scrobbler.getHoldingTrack();
		if(track != null) {
			log.info(methodName, "Got holding track: " + track.getTrackTitle());
			queueAtStart(track);
		} else {
			log.info(methodName, "No holding track");
		}
		
		// tell MobScrobblerImpl to stop scrobbling
		scrobbler.shutdown();
	}
}
