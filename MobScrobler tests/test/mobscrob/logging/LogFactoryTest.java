/**
 * LogFactoryTest.java
 * @author NJ Pearman
 * @date 28 Oct 2008
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

import org.jmock.MockObjectTestCase;

/**
 * @author Neill
 *
 */
public class LogFactoryTest extends MockObjectTestCase {

	/**
	 * Test method for {@link mobscrob.logging.LogFactory#setImplementation(java.lang.String)}.
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @throws LogConfigurationException 
	 */
	public void testSetImplementation() throws ClassNotFoundException, InstantiationException, IllegalAccessException, LogConfigurationException {
		// try null
		try {
			LogFactory.setImplementation(null);
			fail("LogConfigurationException should be thrown for null implementation String");
		} catch(LogConfigurationException e) {
			assertEquals(LogConfigurationException.NULL_STRING, e.getMessage());
		}
		
		// try non-existent class name
		try {
			LogFactory.setImplementation("some.imaginary.class.name");
			fail("ClassNotFoundException should be thrown for non-existent class name");
		} catch(ClassNotFoundException e) {}

		// try valid class name
		LogFactory.setImplementation("mobscrob.logging.MicroLog");
		
		// try setting log class again
		try {
			LogFactory.setImplementation("mobscrob.logging.MicroLog");
			fail("LogConfigurationException should be thrown for when Log implementation has already been configured");
		} catch(LogConfigurationException e) {
			assertEquals(LogConfigurationException.ALREADY_CONFIGURED, e.getMessage());
		}
	}

}
