/**
 * ClientConnector.java
 * @author NJ Pearman
 * @date 18 Oct 2008
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
package mobscrob.midlet;

import java.io.IOException;

import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;
import mobscrob.util.microedition.HTTPUtil;

/**
 * @author Neill
 *
 */
public class ClientConnector {
	
	private static final Log log = LogFactory.getLogger(ClientConnector.class);
	
	private static final String DEFAULT_CLIENT_URL = "http://169.254.1.1:80/mobscrob/client";
	
	public void pingHTTP() {
		final String methodName = "pingHTTP";
		
		try {
			HTTPUtil.getUrl(DEFAULT_CLIENT_URL, "localhost");
		} catch (IOException e) {
			log.error(methodName, "Unable to send ping request to client: "+e.getMessage(), e);
		}
	}
}
