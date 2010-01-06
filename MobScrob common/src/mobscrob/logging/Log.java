/**
 * Log.java
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
package mobscrob.logging;

/**
 * Interface for logging
 * @author Neill
 *
 */
public interface Log {

	/**
	 * Returns a new instance of the Log implementation.
	 * @param clazz
	 * @return
	 */
	Log getInstance(Class clazz);
	
	/**
	 * Returns the class name that the Log instance is logging information
	 * about.
	 * @return
	 */
	String getClassname();
	
	/**
	 * @param methodName
	 * @param msg
	 */
	void info(String methodName, String msg);

	/**
	 * @param methodName
	 * @param msg
	 * @param t
	 */
	void info(String methodName, String msg, Throwable t);

	/**
	 * @param methodName
	 * @param msg
	 */
	void trace(String methodName, String msg);

	/**
	 * @param methodName
	 * @param msg
	 * @param t
	 */
	void trace(String methodName, String msg, Throwable t);

	/**
	 * @param methodName
	 * @param msg
	 */
	void debug(String methodName, String msg);

	/**
	 * @param methodName
	 * @param msg
	 * @param t
	 */
	void debug(String methodName, String msg, Throwable t);

	/**
	 * @param methodName
	 * @param msg
	 */
	void error(String methodName, String msg);

	/**
	 * @param methodName
	 * @param msg
	 * @param t
	 */
	void error(String methodName, String msg, Throwable t);

	/**
	 * @param methodName
	 * @param msg
	 * @param t
	 */
	void fatal(String methodName, String msg, Throwable t);

	/**
	 * @param methodName
	 * @param msg
	 */
	void warn(String methodName, String msg);

	/**
	 * @param methodName
	 * @param msg
	 * @param t
	 */
	void warn(String methodName, String msg, Throwable t);

}