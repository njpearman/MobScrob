package mobscrob.properties;

/**
 * Interface to be implemented by any class that needs to access 
 * MobScrob properties from an underlying storage system. 
 * @author Neill
 *
 */
public interface PropertyPersistor {

	static final String KEY_USERNAME = "last.fm.username";
	static final String KEY_HASHED_PASSWORD = "last.fm.hashed.password";
	static final String KEY_SCROBBLE_OFFLINE = "scrobble.offline";

	/**
	 * Called to load the MobScrobProperties values from the underlying
	 * persistance method used by a concrete implementation of
	 * PropertyPersistor.
	 * @param properties
	 * @return
	 */
	boolean load(MobScrobProperties properties);

	/**
	 * Called to save the MobScrobProperties in the concrete 
	 * PropertyPersistor.
	 * @param properties
	 */
	void save(MobScrobProperties properties);

}