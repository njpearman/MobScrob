/**
 * MD5Util.java
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
package mobscrob.scrobbler;

import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;

/**
 * @author Neill
 * 
 */
public class MD5Util {
	private static final Log log = LogFactory.getLogger(MD5Util.class);

	/**
	 * Util class so private constructor
	 */
	private MD5Util() {
	}

	public static String getAuthenticationToken(String pwd, long timestamp) {
		final String methodName = "1";
		String hash;

		// md5 the password
		hash = md5Hash(pwd);
		log.debug(methodName, "Pwd hash: " + hash);

		// append timestamp
		hash += String.valueOf(timestamp);
		log.debug(methodName, "Hash + timestamp: " + hash);

		// md5 the whole lot again
		hash = md5Hash(hash);
		log.debug(methodName, "Final hash: " + hash);
		return hash;
	}

	/**
	 * Produce MD5 hash of the specified String, using the satsa libraries
	 * 
	 * @param str
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws DigestException
	 */
//	public static String md5JavaHash(String str) throws NoSuchAlgorithmException,
//			DigestException {
//		// check if String is null
//		if (str == null) {
//			str = "";
//		}
//
//		String hash;
//		byte[] buf = new byte[128];
//		MessageDigest md5 = MessageDigest.getInstance("MD5");
//		md5.update(str.getBytes(), 0, str.length());
//		md5.digest(buf, 0, buf.length);
//
//		char[] hexArray = new char[buf.length * 2];
//		for (int i = 0, x = 0; i < buf.length; i++) {
//			hexArray[x++] = HEX_CHARS[(buf[i] >>> 4) & 0xf];
//			hexArray[x++] = HEX_CHARS[buf[i] & 0xf];
//		}
//		hash = new String(hexArray).substring(0, 32);
//		return hash;
//	}
	
	/**
	 * Produces the MD5 hash of the specified String.  If an error is encountered
	 * while calculating the hash then the original String is returned.
	 * @param str
	 * @return
	 */
	public static String md5Hash(String str) {
		// check if String is null
		if (str == null) {
			str = "";
		}

		return MD5.getHashString(str);
	}
}
