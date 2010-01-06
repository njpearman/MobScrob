/**
 * ScrobbleProcessorTest.java
 * @author NJ Pearman
 * @date 27 Oct 2008
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
import mobscrob.scrobbler.MobScrobbler;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @author Neill
 *
 */
public class ScrobbleProcessorTest extends MockObjectTestCase {

	private ScrobbleProcessor testProcessor;
	private Mock mockScrobbler;
	
	/**
	 * Test method for {@link mobscrob.player.ScrobbleProcessor#shutdown()}.
	 */
	public void testShutdown() {
		// null holding track
		mockScrobbler = mock(MobScrobbler.class);
		mockScrobbler.expects(once()).method("getHoldingTrack").withNoArguments().will(returnValue(null));
		mockScrobbler.expects(once()).method("shutdown").withNoArguments();
		testProcessor = new ScrobbleProcessor((MobScrobbler)mockScrobbler.proxy());
		testProcessor.shutdown();
		
		// non null holding track
		TrackMetadata testTrack = new TrackMetadata();
		mockScrobbler = mock(MobScrobbler.class);
		mockScrobbler.expects(once()).method("getHoldingTrack").withNoArguments().will(returnValue(testTrack));
		mockScrobbler.expects(once()).method("shutdown").withNoArguments();
		testProcessor = new ScrobbleProcessor((MobScrobbler)mockScrobbler.proxy());
		testProcessor.shutdown();
	}

}
