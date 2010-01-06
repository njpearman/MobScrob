/**
 * MockOutputStream.java
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
import java.io.OutputStream;

/**
 * Mock wrapper for an OutputStream to allow usage of JMock
 * @author Neill
 *
 */
public class MockOutputStream extends OutputStream {
	
	private IOutputStream osProxy;
	
	public MockOutputStream(IOutputStream osProxy) {
		this.osProxy = osProxy;
	}
	
	public void write(int b) throws IOException {
		osProxy.write(b);
	}
	
	public void write(byte[] b) throws IOException {
		osProxy.write(b, 0, b.length);
	}
}