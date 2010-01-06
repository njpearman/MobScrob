/**
 * PlayProcessor.java
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
package mobscrob.player;

import mobscrob.scrobbler.TrackQueuer;

/**
 * Interface for a play processor. Play processors are implemented in order to
 * do some secondary processing for an MP3 file either before or after it is
 * played by the MP3Player class.
 * 
 * @author Neill
 * 
 */
public interface PlayProcessor extends TrackQueuer {

	/**
	 * Starts the PlayProcessor instance
	 */
	public void start();

	/**
	 * Tells the processor to start shutdown operations.
	 */
	public void shutdown();

}
