/**
 * @date 30 Sep 2008
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

/**
 * Used to throw errors in the mobscrob.id3 package
 * 
 * @author Neill
 * 
 */
public class ID3Exception extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3322982191930220242L;
	
	public static final ID3Exception UNEXPECTED_END_OF_STREAM = new ID3Exception(
			"Reached the end of the input stream reading frame...");
	public static final ID3Exception UNEXPECTED_BYTE_READ_COUNT = new ID3Exception(
			"Couldn't read expected bytes for frame body");
	public static final ID3Exception INCORRECT_FRAME_HEADER_LENGTH = new ID3Exception(
			"Incorrect frame header length");
	public static final ID3Exception INVALID_FRAME_HEADER_FLAGS = new ID3Exception(
			"Invalid flags in frame header");
	public static final ID3Exception UNSUPPORTED_FORMAT = new ID3Exception(
			"Extended frame header not currently supported");

	public ID3Exception(String msg) {
		super(msg);
	}
}
