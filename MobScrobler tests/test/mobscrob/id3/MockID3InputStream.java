/**
 * MockID3InputStream.java
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

import mobscrob.mp3.InfoUnavailableException;
import mobscrob.mp3.MP3Stream;

/**
 * A mocked out InputStream implementation to test the ID3Body class 
 * @author Neill
 *
 */
public class MockID3InputStream extends MP3Stream {

	private static final int READ_NOT_SET = -1979;
	
	private byte[] stream;
	private int[] readReturnValues;
	private int currIndex;
	private int offset;
	
	/**
	 * 
	 */
	public MockID3InputStream(byte[] stream) {
		super();
		this.currIndex = READ_NOT_SET;
		this.offset = 0;
		this.stream = stream;
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		if(currIndex == READ_NOT_SET) {
			throw ID3MockInputStreamException.READ_NOT_SET; 
		}
		if(offset >= stream.length) {
			throw ID3MockInputStreamException.END_OF_STREAM;
		}
		int next = stream[offset++];
		return next;
	}
	
	public int read(byte[] into) throws IOException {
		if(currIndex == READ_NOT_SET) {
			throw ID3MockInputStreamException.READ_NOT_SET; 
		}
		if(into.length > stream.length) {
			throw ID3MockInputStreamException.END_OF_STREAM;
		}
		
		for(int i=0; i<into.length; i++) {
			into[i] = stream[i+offset];
		}
		int val = readReturnValues[currIndex++];
		if(currIndex > readReturnValues.length) {
			throw ID3MockInputStreamException.UNEXPECTED_METHOD_CALL;
		}
		offset +=val;
		return val;
		
	}

	public MockID3InputStream setReturnInt(int[] values) {
		currIndex = 0;
		
		readReturnValues = new int[values.length];
		for(int i=0; i<values.length; i++) {
			readReturnValues[i] = values[i];
		}
		return this;
	}
	
	public static class ID3MockInputStreamException extends IOException {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3838173017359770095L;
		
		public static final ID3MockInputStreamException READ_NOT_SET = 
			new ID3MockInputStreamException("Read values have not been set");
		public static final ID3MockInputStreamException END_OF_STREAM =
			new ID3MockInputStreamException("Unxpected end of stream");
		public static final ID3MockInputStreamException UNEXPECTED_METHOD_CALL = 
			new ID3MockInputStreamException("Unexpected methd call");
		
		private ID3MockInputStreamException(String msg) {
			super(msg);
		}
		
		public boolean equals(Object obj) {
			if(obj == null ||
			   !(obj instanceof ID3MockInputStreamException)) {
				return false;
			}
			ID3MockInputStreamException e = (ID3MockInputStreamException)obj;
			
			return this.getMessage() == null && e.getMessage() == null ? true :
				   this.getMessage() == null && e.getMessage() != null ? false :
				   this.getMessage() != null && e.getMessage() == null ? false :
				   this.getMessage().equals(e.getMessage());
		}
	}

	public long getStreamLength() throws InfoUnavailableException {
		throw new InfoUnavailableException("Method not implemented");
	}
}
