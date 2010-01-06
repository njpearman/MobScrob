/**
 * StreamUtil.java
 * @author NJ Pearman
 * @date 4 Oct 2008
 */
package mobscrob.util;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Neill
 *
 */
public class StreamUtil {

	private StreamUtil() {}
	
	public static void closeInputStream(InputStream in) {
		if(in != null) {
			try { in.close(); }
			catch(Exception e) {}
		}
	}

	public static void closeOutputStream(OutputStream out) {
		if(out != null) {
			try { out.close(); }
			catch(Exception e) {}
		}
	}
}
