/**
 * ClientConnector.java
 * @author NJ Pearman
 * @date 18 Oct 2008
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
