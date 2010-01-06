/**
 * ID3Header.java
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
 * Interface for an ID3 header
 * 
 * @author Neill
 * 
 */
public interface ID3Header {

	/**
	 * Parses the raw header data
	 * 
	 * @throws IOException
	 */
	public abstract void parse() throws IOException;

	/**
	 * Returns the body length held in the header
	 * 
	 * @return
	 */
	public abstract long bodyLength();

	/**
	 * Returns the major version number held in the header
	 * 
	 * @return
	 */
	public abstract int majorVersion();

	public abstract boolean hasUnchronization();

	public abstract boolean hasExtendedHeader();

}