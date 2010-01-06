/**
 * ID32TagReaderIntegrationTest
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

import mobscrob.mp3.MP3ResourceStream;

import org.jmock.MockObjectTestCase;

public class ID32TagReaderIntegrationTest extends MockObjectTestCase {
	private static final String DEMO_MP3 = "resource://audio/Spiritualized.mp3";
	private ID32TagReader testReader;
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testRead() throws Exception {
		TrackMetadata testData = new TrackMetadata();
		
		// create a resource stream
		MP3ResourceStream stream = new MP3ResourceStream(DEMO_MP3);
		testReader = new ID32TagReader(stream);
		testReader.readInto(testData);
	}

}
