/**
 * ID3v2HeaderTest.java
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

import java.io.IOException;

import junit.framework.TestCase;

/**
 * @author Neill
 *
 */
public class ID3HeaderTest extends TestCase {

	private ID3v2Header testHeader;
	
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testVerifyIsID3() throws Exception {
		// starts ID3
		testHeader = new ID3v2Header(
				new byte[]
				         { 0x49, 0x44, 0x33, 0x00, 0x00,
						   0x00, 0x00, 0x00, 0x00, 0x00 });
		testHeader.verifyIsID3();
		
		// starts NOT
		testHeader = new ID3v2Header(
				new byte[]
				         { 0x4E, 0x4F, 0x54, 0x00, 0x00,
						   0x00, 0x00, 0x00, 0x00, 0x00 });
		
		try {
			testHeader.verifyIsID3();
			fail("IOException should be thrown for identifier != ID3");
		} catch(IOException e) {}
	}

	public void testReadVersion() throws Exception {
		// major version 2
		testHeader = new ID3v2Header(
				new byte[] 
				         { 0x00, 0x00, 0x00, 0x02, 0x00,
						   0x00, 0x00, 0x00, 0x00, 0x00 });
		testHeader.readVersion();

		// major version 3
		testHeader = new ID3v2Header(
				new byte[] 
				         { 0x00, 0x00, 0x00, 0x03, 0x00,
						   0x00, 0x00, 0x00, 0x00, 0x00 });
		testHeader.readVersion();
		
		// major version != 2 or 3
		testHeader = new ID3v2Header(
				new byte[] 
				         { 0x00, 0x00, 0x00, 0x04, 0x00,
						   0x00, 0x00, 0x00, 0x00, 0x00 });
		try {
			testHeader.readVersion();
			fail("IOException should be thrown for major version != 2 or 3");
		} catch(IOException e) {}

		// revision version != 0
		testHeader = new ID3v2Header(
				new byte[] 
				         { 0x00, 0x00, 0x00, 0x03, 0x05,
						   0x00, 0x00, 0x00, 0x00, 0x00 });
		try {
			testHeader.readVersion();
			fail("IOException should be thrown for revision != 0");
		} catch(IOException e) {}

	}
	
	/**
	 * Test method for {@link mobscrob.id3.ID3Header#parseFlags()}.
	 */
	public void testParseFlags() throws Exception {
		testFlags(new byte[] {
				0x0, 0x0, 0x0, 0x0, 0x0,
				0x0, 0x0, 0x0, 0x0, 0x0 }, 0);
		
		try {
			testFlags(new byte[] {
					0x0, 0x0, 0x0, 0x0, 0x0,
					1, 0x0, 0x0, 0x0, 0x0 }, 0);
			fail("IOException should be thrown for invalid flag");
		} catch(IOException e) {}
		
		int i = (1 << 4) | (1 << 6) | (1 << 7);
		
		try {
			testFlags(new byte[] {
					0x0, 0x0, 0x0, 0x0, 0x0,
					(byte)i, 0x0, 0x0, 0x0, 0x0 }, 0);
			fail("IOException should be thrown for flag in bit 4");
		} catch(IOException ex) {}

		i ^= (1 << 4);
		
		testFlags(new byte[] {
				0x0, 0x0, 0x0, 0x0, 0x0,
				(byte)i, 0x0, 0x0, 0x0, 0x0 }, 4);
		assertTrue(testHeader.hasExtendedHeader());
		assertTrue(testHeader.hasUnchronization());
	}
	
	public void testDetermineTagLength() throws Exception {
		// test value from ID3.org
		testHeader = new ID3v2Header(new byte[] { 
				0x0, 0x0, 0x0, 0x0, 0x0, 
				0x0, 0x00, 0x00, 0x02, 0x01});
		assertEquals((1<<8)+1, testHeader.determineTagLength());

		// max value
		testHeader = new ID3v2Header(new byte[] { 
				0x0, 0x0, 0x0, 0x0, 0x0, 
				0x0, 0x7F, 0x7F, 0x7F, 0x7F});
		assertEquals((1 << 28)-1, testHeader.determineTagLength());

		// exceeds max value for a particular byte
		try {
			testHeader = new ID3v2Header(new byte[]{
					0x0, 0x0, 0x0, 0x0, 0x0, 
					0x0, 0x7F, 0x7F, 0x7F, (byte)0x80});
			testHeader.determineTagLength();
			fail("IOException should be thrown when maximum byte size exceeded");
		} catch(IOException e) {}
	}
	
	private void testFlags(byte[] flags, int version) throws Exception {
		testHeader = new ID3v2Header(flags);
		testHeader.setMajorVersion(version);
		testHeader.parseFlags();
	}
}
