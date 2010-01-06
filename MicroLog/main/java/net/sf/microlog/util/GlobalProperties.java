package net.sf.microlog.util;

import javax.microedition.midlet.MIDlet;

/**
 * A class that makes the Properties available globally to the system as a
 * Singleton.
 * 
 * @author Darius Katz
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 */
public class GlobalProperties implements PropertiesGetter {

	private static GlobalProperties instance = null;

	private static Properties properties = null;

	/**
	 * Creates a new global properties object. (as a Singleton)
	 */
	private GlobalProperties() {
	}

	/**
	 * Returns an instance of GlobalProperties. (as a Singleton)
	 * 
	 * <p>
	 * The default global properties are empty.
	 * 
	 * @return an instance of GlobalProperties
	 * 
	 */
	public static synchronized GlobalProperties getInstance() {
		if (instance == null) {
			instance = new GlobalProperties();
		}

		return instance;
	}

	/**
	 * Initiates the global properties. Must be called at least once before
	 * getting an instance of GlobalProperties and any calls after are ignored.
	 */
	public static synchronized void init(MIDlet midlet) {
		if (instance != null) {
			// It's not possible to initiate when an instance is created.
			return;
		}

		properties = new Properties(midlet);
	}

	/**
	 * Returns the Properties that this Singleton has instantiated.
	 * 
	 * @return the Properties instance
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Returns the Object to which the specified key is mapped.
	 * 
	 * @param key
	 *            the key associated to the stored Object
	 * 
	 * @return the Object to which the key is mapped; null if the key is not
	 *         mapped to any Object in this hashtable.
	 */
	public Object get(String key) {
		return properties.get(key);
	}

	/**
	 * Returns the String to which the specified key is mapped.
	 * 
	 * @param key
	 *            the key associated to the stored Object
	 * 
	 * @return the Object to which the key is mapped; null if the key is not
	 *         mapped to any Object.
	 */
	public String getString(String key) {
		return properties.getString(key);
	}

	/**
	 * Returns the Object to which the specified key is mapped directly from the
	 * default values. Any overridden settings are ignored. Useful if an
	 * overridden value is erroneous and a proper value is needed. (The default
	 * values are considered to be checked and therefore proper.)
	 * 
	 * @param key
	 *            the key associated to the stored Object
	 * 
	 * @return the Object to which the key is mapped; null if the key is not
	 *         mapped to any Object in this hashtable.
	 */
	public Object getDefaultValue(String key) {
		return properties.getDefaultValue(key);
	}

}
