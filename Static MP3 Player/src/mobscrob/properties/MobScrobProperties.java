/**
 * 
 */
package mobscrob.properties;

import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;
import mobscrob.player.ScrobbleProcessor;

/**
 * MobScrob properties file persistor. Can read 8-bit and 16-bit files (big or
 * little endian), although saves file as 8-bit.
 * 
 * @author Neill
 * 
 */
public class MobScrobProperties {

	private static final Log log = LogFactory.getLogger(MobScrobProperties.class);

	private String username;
	private String hashedPassword;
	private boolean scrobbleOffline;
	private PropertyPersistor persistor;
	private ScrobbleProcessor processor;

	public MobScrobProperties(PropertyPersistor persistor) {
		this.persistor = persistor;
	}
	
	protected void setHashedPassword(String value) { this.hashedPassword = value; }
	protected void setUsername(String value) { this.username = value; }
	protected void setScrobbleOffline(boolean value) {
		this.scrobbleOffline = value;
		if(!this.scrobbleOffline && processor != null) {
			processor.start();
		}
	}
	
	public void setLastFmUserDetails(String user, String hashedPassword) {
		final String methodName = "4";
		this.username = user;
		this.hashedPassword = hashedPassword;
		log.info(methodName, "Set user credentials: " + this.username + ", " + this.hashedPassword);
	}

	public void setScrobbleProcessor(ScrobbleProcessor processor) {
		this.processor = processor;
	}
	
	public String getUsername() { return this.username; }
	public String getHashedPassword() { return this.hashedPassword; }
	public boolean scrobbleOffline() { return this.scrobbleOffline; }
	
	public void save() throws PropertiesException {
		if(persistor == null) {
			throw new PropertiesException("Null properties persistor set");
		}
		persistor.save(this);
	}
	
	public boolean load() throws PropertiesException {
		if(persistor == null) {
			throw new PropertiesException("Null properties persistor set");
		}
		return persistor.load(this);
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer("[MobScrobProperties [Username: ");
		buf.append(username).append("] [Hashed password: ")
		   .append(hashedPassword).append("] [Scrobble offline? ")
		   .append(scrobbleOffline).append("]]");

		return buf.toString();
	}
}
