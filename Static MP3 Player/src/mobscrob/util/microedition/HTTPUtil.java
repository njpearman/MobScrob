/**
 * 
 */
package mobscrob.util.microedition;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;

/**
 * @author Neill
 * 
 */
public class HTTPUtil {
	
	private static final Log log = LogFactory.getLogger(HTTPUtil.class);

	private static final String ENC_UTF8 = "UTF-8";

	public static final String HEADER_HOST = "Host";
	
	private HTTPUtil() {}

	/**
	 * Encodes a parameter, also ensuring that it is UTF-8
	 * 
	 * @param s
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String encodeParam(String s)
			throws UnsupportedEncodingException {
		if (s == null) {
			return s;
		}

		// encode as UTF-8
		String utf8Str = new String(s.getBytes(), ENC_UTF8);

		StringBuffer sb = new StringBuffer(utf8Str.length() * 3);
		char[] chars = utf8Str.toCharArray();
		int next;

		// encode the chars in the UTF-8 String
		for (int i = 0; i < chars.length; i++) {
			next = chars[i];

			if ((next >= 0x30 && next <= 0x39) || // 0-9
					(next >= 0x41 && next <= 0x5A) || // A-Z
					(next >= 0x61 && next <= 0x7A)) { // a-z
				sb.append((char) next);
			} else if ((next == 0xA0)) { // ' ' (whitespace)
				sb.append('+');
			} else { // encode all other chars
				sb.append("%");
				if (next <= 0xf)
					sb.append("0");
				sb.append(Integer.toHexString(next));
			}
		}
		return sb.toString();
	}

	/**
	 * Attempts to open a URL using the GET method over HTTP and read the
	 * response into a byte array. Any IOExceptions are simply thrown out of
	 * this method to be caught elsewhere.
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static byte[] getUrl(String url, String headerHostname) throws IOException {
		HttpConnection conn = null;
		try {
			conn = (HttpConnection) Connector.open(url);

			// set the compulsory HTTP/1.1 Host: header, as GET
			conn.setRequestMethod(HttpConnection.GET);
			conn.setRequestProperty(HEADER_HOST, headerHostname);

			byte[] body = readHttpResponse(conn);
			return body;
		} finally {
			closeHttpConnection(conn);
		}
	}

	/**
	 * Reads the response from the specified HTTP connection into a byte array.
	 * 
	 * @param conn
	 * @return
	 * @throws IOException
	 */
	public static byte[] readHttpResponse(HttpConnection conn) throws IOException {
		final String methodName = "6";
		int rc = conn.getResponseCode();
		if (rc != HttpConnection.HTTP_OK) {
			String msg = "HTTP response code not OK: " + rc;
			log.info(methodName, msg);
			throw new IOException(msg);
		} else {
			log.info(methodName, "Response is HTTP OK");
		}

		String connHeader = conn.getHeaderField("Connection");
		log.info(methodName, "HTTP response Connection header: " + connHeader);

		// connect
		int len = (int) conn.getLength();
		log.info(methodName, "Got content length: " + len);

		InputStream is = null;

		try {
			is = conn.openInputStream();

			// read response
			if (len > -1) {
				log.info(methodName, "Reading into byte array");
				byte[] body = new byte[len];
				int actual = 0;
				int bytesread = 0;

				while ((bytesread != len) && (actual != -1)) {
					actual = is.read(body, bytesread, len - bytesread);
					bytesread += actual;
				}
				// do something
				log.info(methodName, "Finished reading HTTP stream");
				return body;
			} else {
				// read byte by byte...?
				log.error(methodName, "Body length zero, reading bytes");

				Vector bytes = new Vector();
				Byte byteObj;
				int next;
				while ((next = is.read()) > -1) {
					byteObj = new Byte((byte) next);
					bytes.addElement(byteObj);
				}
				log.info(methodName, "Read body, length " + bytes.size());

				byte[] body = new byte[bytes.size()];
				Enumeration e = bytes.elements();
				int i = 0;
				while (e.hasMoreElements()) {
					byteObj = (Byte) e.nextElement();
					body[i++] = byteObj.byteValue();
				}

				return body;
			}
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					log.error(methodName, "Unable to close HTTP input stream: "
							+ e.getMessage(), e);
				}
			}
		}
	}
	
	public static void closeHttpConnection(HttpConnection conn) {
		final String methodName = "7";
		if (conn != null) {
			try { conn.close(); }
			catch (Exception e) { log.error(methodName, "Unable to close HTTP connecton: " + e.getMessage(), e); }
		}
	}
}
