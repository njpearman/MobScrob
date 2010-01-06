/**
 * LogFactory.java
 * @author NJ Pearman
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
 * @author Neill
 *
 */
public class LogFactory {

	private static Log logger;
	
	/**
	 * Call once to initialize the logger implementation class.
	 * @param implementation
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws LogConfigurationException 
	 */
	public static final void setImplementation(String implementation) throws ClassNotFoundException, InstantiationException, IllegalAccessException, LogConfigurationException {
		if(implementation == null) {
			System.out.println(LogConfigurationException.NULL_STRING);
			throw new LogConfigurationException(LogConfigurationException.NULL_STRING);
		} else if(LogFactory.logger != null) {
			System.out.println(LogConfigurationException.ALREADY_CONFIGURED);
			throw new LogConfigurationException(LogConfigurationException.ALREADY_CONFIGURED);
		} else {
			Class logClass = Class.forName(implementation);
			logger = (Log) logClass.newInstance();
			System.out.println("INFO: Set logging implementation");
		}
	}
	
	/**
	 * Returns an instance of the Log implementation associated
	 * with the provided Class.
	 * @param clazz
	 * @return
	 */
	public static final Log getLogger(Class clazz) {
		return logger.getInstance(clazz);
	}
}
