/**
 * ID3v1BodyTest.java
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
import junit.framework.TestCase;

/**
 * @author Neill
 *
 */
public class ID3v1BodyTest extends TestCase {

	private static final byte[] TAG_HEADER = new byte[]{'T', 'A', 'G'};
	
	ID3v1Body testBody;
	MockID3InputStream mockStream;
	
	/**
	 * Test method for {@link mobscrob.id3.ID3v1Body#readNextFrame()}.
	 */
	public void testReadNextFrame() throws Exception {
		byte[] streamBytes = new byte[] {
				0x41, 0x72, 0x6F, 0x75, 0x6E, 0x64, 0x20, 0x54,
				0x68, 0x65, 0x20, 0x57, 0x6F, 0x72, 0x6C, 0x64,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00,	0x00, 0x00, 
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x44, 0x61, 
				0x66, 0x74, 0x20, 0x50, 0x75, 0x6E, 0x6B, 0x00, 
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
				0x00, 0x00, 0x00, 0x00, 0x48, 0x6F, 0x6D, 0x65, 
				0x77, 0x6F, 0x72, 0x6B, 0x00, 0x00, 0x00, 0x00, 
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
				0x00, 0x00, 0x31, 0x39, 0x39, 0x37, 0x00, 0x00, 
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
				0x00, 0x00, 0x00, 0x07, 0x23
		};
		
		mockStream = new MockID3InputStream(streamBytes);
		mockStream.setReturnInt(new int[] {
				ID3v1Body.TRACK_TITLE_LEN,
				ID3v1Body.ARTIST_LEN,
				ID3v1Body.ALBUM_TITLE_LEN,
				ID3v1Body.YEAR_LEN,
				ID3v1Body.COMMENT_LEN,
				ID3v1Body.TRACK_NUMBER_LEN,
				ID3v1Body.GENRE_LEN
		});
		testBody = new ID3v1Body(new ID3v1Header(TAG_HEADER), mockStream);
		
		// read each 'frame' and check contents
		assertNextFrame(ID3v1Frame.ID_TRACK_TITLE, "Around The World");
		assertNextFrame(ID3v1Frame.ID_ARTIST, "Daft Punk");
		assertNextFrame(ID3v1Frame.ID_ALBUM_TITLE, "Homework");
		assertNextFrame(ID3v1Frame.ID_YEAR, "1997");
		assertNextFrame(ID3v1Frame.ID_COMMENT, "");
		assertNextFrame(ID3v1Frame.ID_TRACK_NUMBER, "7");
		assertNextFrame(ID3v1Frame.ID_GENRE, "35");
	}
	
	private void assertNextFrame(String expectedID, String expectedContents) throws Exception {
		Frame next = testBody.readNextFrame();
		assertNotNull(next);
		assertEquals(expectedID, next.getId());
		assertEquals(expectedContents, next.getContentsAsString());
	}

}
