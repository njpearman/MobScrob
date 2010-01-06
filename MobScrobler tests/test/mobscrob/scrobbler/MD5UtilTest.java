/**
 * MD5UtilTest.java
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

import junit.framework.TestCase;

/**
 * @author Neill
 *
 */
public class MD5UtilTest extends TestCase {

	/** 01/01/2000 **/
	private static final long TEST_TIMESTAMP = 946702800;
	
	private String result;
	
	/**
	 * Test method for {@link mobscrob.scrobbler.MD5Util#getAuthenticationToken(java.lang.String, long)}.
	 */
	public void testGetAuthenticationToken() throws Exception {
		result = MD5Util.getAuthenticationToken("test", TEST_TIMESTAMP);
		assertNotNull(result);
		assertEquals("5178829806db2d04d7509700460d2392", result);
	}

	/**
	 * Test method for {@link mobscrob.scrobbler.MD5Util#md5Hash(java.lang.String)}.
	 */
	public void testMd5Hash() throws Exception {
		result = MD5Util.md5Hash("TEST");
		assertNotNull(result);
		assertEquals("033bd94b1168d7e4f0d644c3c95e35bf", result);

		result = MD5Util.md5Hash(null);
		assertEquals(MD5Util.md5Hash(""), result);
	}
	
	public void testMd5HashCustom() throws Exception {
		String ref, result;

		// test 1
		ref = MD5Util.md5Hash("TEST");
		result = MD5Util.md5Hash("TEST");
		
		assertEquals(ref, result);
		
		// test 2
		ref = MD5Util.md5Hash("mobscrobbler is an awesome example of using J2ME to create a fully featured MP3 player enabled for scrobbling tracks to audioscrobbler and last.fm");
		result = MD5Util.md5Hash("mobscrobbler is an awesome example of using J2ME to create a fully featured MP3 player enabled for scrobbling tracks to audioscrobbler and last.fm");
		
		assertEquals(ref, result);
		
		// test 3
		ref = MD5Util.md5Hash("");
		result = MD5Util.md5Hash("");
		
		assertEquals(ref, result);
		
		// test time
		long startTime, total;
		startTime = System.currentTimeMillis();
		for(int i=0; i<2000; i++) {
			ref = MD5Util.md5Hash("njpearman1");
		}
		total = System.currentTimeMillis()-startTime;
		System.out.println("Total time for Java implementation: "+total);
		
		startTime = System.currentTimeMillis();
		for(int i=0; i<2000; i++) {
			result = MD5Util.md5Hash("njpearman2");
		}
		total = System.currentTimeMillis()-startTime;
		System.out.println("Total time for custom implementation: "+total);
	}
}
