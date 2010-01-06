/**
 * ByteUtiTest.java
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
package mobscrob.util;

import java.util.Vector;

import junit.framework.TestCase;

/**
 * @author Neill
 *
 */
public class ByteUtilTest extends TestCase {

	/**
	 * Test method for {@link mobscrob.util.ByteUtilTest#readLines(byte[])}.
	 */
	public void testReadLines() {
		byte[] testBytes = null;
		
		Vector result;
		
		result = ByteUtil.readLines(testBytes);
		assertEquals(0, result.size());
		
		
		// three zero-length lines
		testBytes = new byte[] { 
				0x0a, 0x0a, 0x0a
		};
		
		result = ByteUtil.readLines(testBytes);
		assertEquals(3, result.size());

		// a b c
		testBytes = new byte[] {
				0x61, 0x61, 0x61, 0x0a, 
				0x62, 0x62, 0x62, 0x0a, 
				0x63, 0x63, 0x63, 0x0a
		};
		
		result = ByteUtil.readLines(testBytes);
		assertEquals(3, result.size());
		assertEquals("aaa", result.elementAt(0));
		assertEquals("bbb", result.elementAt(1));
		assertEquals("ccc", result.elementAt(2));
		
	}

	public void testReplaceSpaceWithPlus() throws Exception {
		byte[] testBytes = null;
		
		ByteUtil.replaceSpaceWithPlus(testBytes);
		assertNull(testBytes);
		
		testBytes = new byte[] {
			0x20, 0x20, 0x20
		};
		System.out.println(new String(testBytes));
		ByteUtil.replaceSpaceWithPlus(testBytes);
		assertEquals("+++", new String(testBytes));
		
		testBytes = new byte[] {
			0x61, 0x62, 0x63
		};
		System.out.println(new String(testBytes));
		ByteUtil.replaceSpaceWithPlus(testBytes);
		assertEquals("abc", new String(testBytes));
		
		testBytes = new byte[] {
			0x61, 0x20, 0x62, 0x20, 0x63
		};
		System.out.println(new String(testBytes));
		ByteUtil.replaceSpaceWithPlus(testBytes);
		assertEquals("a+b+c", new String(testBytes));
	}
}
