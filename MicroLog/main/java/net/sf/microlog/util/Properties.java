package net.sf.microlog.util;

import java.util.Hashtable;
import javax.microedition.midlet.MIDlet;
import net.sf.microlog.util.properties.AppProperty;

import net.sf.microlog.util.properties.DefaultValues;
import net.sf.microlog.util.properties.NumberedAppProperty;
import net.sf.microlog.util.properties.PropertyFile;
import net.sf.microlog.util.properties.PropertySource;

/**
 * A class that makes the properties available to the system. The properties are
 * based on key/value, i.e: <code>some.key=some_value</code>
 * 
 * The properties are taken from the following sources in this order. (That is,
 * the values have precedence in this order.)
 * 
 * Sources: -------- - default values - property text-file included in the
 * JAR-file - values from application properties (MIDlet.getAppProperty) -
 * values saved in rms
 * 
 * (Example: keys/values saved in the rms overwrites keys/values from all other
 * sources.)
 * <p>
 * 
 * @author Darius Katz
 */
public class Properties implements PropertiesGetter {

	public static final String DEFAULT_PROPERTY_FILE = "/microlog.properties";

	private final Hashtable props;

	private MIDlet midlet = null;

	private DefaultValues defaultValues;

	/**
	 * Creates a new properties object. Since no midlet is passed as a parameter
	 * properties will not come from the application properties.
	 */
	public Properties() {
		props = new Hashtable();
		initProperties(props);
	}

	/**
	 * Creates a new properties object.
	 * 
	 * @param m
	 *            the MIDlet from which application properties will be fetched
	 */
	public Properties(MIDlet m) {
		midlet = m;
		props = new Hashtable();
		initProperties(props);
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
		return props.get(key);
	}

	/**
	 * Returns the String to which the specified key is mapped.
	 * 
	 * @param key
	 *            the key associated to the stored Object
	 * 
	 * @return the Object to which the key is mapped; null if the key is not
	 *         mapped to any Object in this hashtable.
	 */
	public String getString(String key) {
		return (String) props.get(key);
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
		return defaultValues.get(key);
	}

	/**
	 * Initializes the Properties by reading values from different
	 * PropertySources in a determined order. This order determines the way in
	 * which values are overridden.
	 * 
	 * @param properties
	 *            the Hashtable in which the properties are stored
	 */
	private void initProperties(Hashtable properties) {
		// Insert default values
		defaultValues = new DefaultValues();
		defaultValues.insertProperties(properties);

		// Insert/overwrite values from the property file
		PropertySource fileProperties = new PropertyFile(DEFAULT_PROPERTY_FILE);
		fileProperties.insertProperties(properties);

		// Insert/overwrite values from the application properties
		if (midlet != null) {
			PropertySource appProperties = new AppProperty(midlet);
			appProperties.insertProperties(properties);

			PropertySource numAppProperty = new NumberedAppProperty(midlet);
			numAppProperty.insertProperties(properties);
		}

	}

}
