/**
 * TagReaderProcessor.java
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

import java.io.IOException;

import mobscrob.id3.ID32TagReader;
import mobscrob.id3.TrackMetadata;
import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;
import mobscrob.mp3.InfoUnavailableException;
import mobscrob.mp3.MP3Stream;
import mobscrob.util.StreamUtil;

/**
 * @author Neill
 * 
 */
public class TagReaderProcessor extends PlayProcessorImpl {
private static final Log log = LogFactory.getLogger(TagReaderProcessor.class);

	private static final int WAIT_TIME = 5000;

	public TagReaderProcessor() {
		super(WAIT_TIME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mobscrob.player.PlayProcessorImpl#process(mobscrob.id3.TrackMetadata)
	 */
	public void process(TrackMetadata next) {
		final String methodName = "1";
		MP3Stream stream = null;

		try {
			// reading the tag for the file
			stream = MP3Stream.instance(next.getFileLocation());
			ID32TagReader reader = new ID32TagReader(stream);
			reader.readInto(next);
			log.info(methodName, "Got tag info for " + next);
			next.updated();
		} catch (IOException e) {
			log.error(methodName,
					  "Error trying to read tag: " + e.getMessage(), e);
			next.setInvalidID3Tag(true);
		} catch (InfoUnavailableException e) {
			log.error(methodName,
					"Error trying to read tag: " + e.getMessage(), e);
			next.setInvalidID3Tag(true);
		} finally {
			StreamUtil.closeInputStream(stream);
		}
	}

	public void shutdown() {
		// nothing to do
	}
}
