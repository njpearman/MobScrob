package mobscrob.util;

import java.util.Vector;

/**
 * 
 */

/**
 * @author Neill
 * 
 */
public class ByteUtil {

	/**
	 * Utility class so private constructor
	 */
	private ByteUtil() {
	}

	/**
	 * Returns a Vector of Strings representing the lines present in the byte
	 * array, separated by '\n' return feed.
	 * 
	 * @param bytes
	 * @return
	 */
	public static Vector readLines(byte[] bytes) {
		//final String methodName = "1";
		Vector lines = new Vector();
		if (bytes == null || bytes.length == 0) {
			return lines;
		}
		StringBuffer line = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] == '\n') {
				// new line
				String nextLine = line.toString();
				//log.info(methodName, "Got line: " + nextLine);
				lines.addElement(nextLine);
				line = new StringBuffer();
			} else {
				// add to current line
				line.append((char) bytes[i]);
			}
		}

		return lines;
	}

	public static void replaceSpaceWithPlus(byte[] bytes) {
		if (bytes != null) {
			for (int i = 0; i < bytes.length; i++) {
				if (bytes[i] == ' ') {
					bytes[i] = '+';
				}
			}
		}
	}
}
