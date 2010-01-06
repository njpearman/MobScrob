/**
 * ID3v22Frame.java
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

/**
 * @author Neill
 * 
 */
public class ID3v22Frame extends Frame {

	public static final String ID_ALBUM_TITLE = "TAL";
	public static final String ID_TRACK_TITLE = "TT2";
	public static final String ID_ARTIST = "TP1";
	public static final String ID_TRACK_NUMBER = "TRK";
	public static final String ID_CONTENT_TYPE = "TCO";

	public ID3v22Frame(AbstractID3Body body, String id, int length, byte[] raw) {
		body.super(id, length, raw);
	}
}
