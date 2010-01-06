/**
 * MobScrobbler.java
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