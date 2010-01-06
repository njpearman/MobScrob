/**
 * ID3v234Frame 
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
public class ID3v234Frame extends Frame {

	public static final String ID_ALBUM_TITLE = "TALB";
	public static final String ID_TRACK_TITLE = "TIT2";
	public static final String ID_ARTIST = "TPE1";
	public static final String ID_TRACK_NUMBER = "TRCK";
	public static final String ID_RECORDING_TIME = "TDRC";
	public static final String ID_CONTENT_TYPE = "TCON";

	public ID3v234Frame(AbstractID3Body body, String id, int length, byte[] raw) {
		body.super(id, length, raw);
	}
}
