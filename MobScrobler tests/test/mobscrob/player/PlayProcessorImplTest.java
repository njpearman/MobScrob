/**
 * PlayProcessorImplTest.java
 * @author NJ Pearman
 * @date 24 Oct 2008
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

import org.jmock.MockObjectTestCase;

/**
 * @author Neill
 *
 */
public class PlayProcessorImplTest extends MockObjectTestCase {

	private static final String TRACK_NAME_1 = "TEST1";
	private static final String TRACK_NAME_2 = "TEST2";
	private static final String TRACK_NAME_3 = "TEST3";
	
	private PlayProcessorImpl testProcessor;
	
	/**
	 * Test method for {@link mobscrob.player.PlayProcessorImpl#queueAtStart(mobscrob.id3.TrackMetadata)}.
	 */
	public void testQueueAtStart() {
		TrackMetadata track;
		
		testProcessor = new PlayProcessorImpl(100){
			public void process(TrackMetadata next) {}
			public void shutdown() {}
		};
		
		// check adding to an empty queue
		track = new TrackMetadata();
		track.setArtist(TRACK_NAME_1);
		testProcessor.queueAtStart(track);
		assertEquals(1, testProcessor.getQueueSnapshot().length);
		assertEquals(TRACK_NAME_1, testProcessor.getQueueSnapshot()[0].getArtist());
		
		// check adding to a queue containing one element
		track = new TrackMetadata();
		track.setArtist(TRACK_NAME_2);
		testProcessor.queueAtStart(track);
		assertEquals(2, testProcessor.getQueueSnapshot().length);
		assertEquals(TRACK_NAME_2, testProcessor.getQueueSnapshot()[0].getArtist());
		assertEquals(TRACK_NAME_1, testProcessor.getQueueSnapshot()[1].getArtist());
				
		// check adding to a queue containing two elements
		track = new TrackMetadata();
		track.setArtist(TRACK_NAME_3);
		testProcessor.queueAtStart(track);
		assertEquals(3, testProcessor.getQueueSnapshot().length);
		assertEquals(TRACK_NAME_3, testProcessor.getQueueSnapshot()[0].getArtist());
		assertEquals(TRACK_NAME_2, testProcessor.getQueueSnapshot()[1].getArtist());
		assertEquals(TRACK_NAME_1, testProcessor.getQueueSnapshot()[2].getArtist());
	}

}
