/**
 * PropertyPersistor.java
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
package mobscrob.properties;

/**
 * Interface to be implemented by any class that needs to access 
 * MobScrob properties from an underlying storage system. 
 * @author Neill
 *
 */
public interface PropertyPersistor {

	static final String KEY_USERNAME = "last.fm.username";
	static final String KEY_HASHED_PASSWORD = "last.fm.hashed.password";
	static final String KEY_SCROBBLE_OFFLINE = "scrobble.offline";

	/**
	 * Called to load the MobScrobProperties values from the underlying
	 * persistance method used by a concrete implementation of
	 * PropertyPersistor.
	 * @param properties
	 * @return
	 */
	boolean load(MobScrobProperties properties);

	/**
	 * Called to save the MobScrobProperties in the concrete 
	 * PropertyPersistor.
	 * @param properties
	 */
	void save(MobScrobProperties properties);

}