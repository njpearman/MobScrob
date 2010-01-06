/**
 * MockInputStream.java 
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
package mobscrob.util.test;

import java.io.IOException;
import java.io.InputStream;

/**
 * Mock wrapper for an InputStream to allow usage of JMock
 * @author Neill
 *
 */
public class MockInputStream extends InputStream {

	/**
	 * 
	 */
	private IInputStream isProxy;
	
	public MockInputStream(IInputStream isProxy) {
		this.isProxy = isProxy;
	}
	
	public int read() throws IOException {
		return isProxy.read();
	}
}