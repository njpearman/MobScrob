/**
 * LogTest
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
package mobscrob.logging;

import junit.framework.TestCase;

/**
 * @author Neill
 *
 */
public class LogTest extends TestCase {

	private Log testLog;
	
	/**
	 * Test method for {@link mobscrob.logging.MicroLog#getInstance(java.lang.Class)}.
	 */
	public void testMicroLog() {
		testLog = new MicroLog().getInstance(String.class);
		assertEquals("String", testLog.getClassname());
	}

}
