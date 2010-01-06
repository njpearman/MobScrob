package net.sf.microlog.util.properties;

import java.util.Hashtable;

import javax.microedition.midlet.MIDlet;

/**
 * This class reads application properties from the JAD file with a number
 * prefix.
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 * @author Darius Katz
 */
public class NumberedAppProperty implements PropertySource {

	private static final String NUMBERED_PROPERTY_PREFIX = "AppProperty_";
	private static final String NUMBERED_PROPERTY_DELIMITER = "=";

	private MIDlet midlet;

	/**
	 * Creates a new instance of NumberedAppProperty
	 * 
	 * @param midlet
	 *            the MIDlet from which the application properties are read.
	 */
	public NumberedAppProperty(MIDlet midlet) {
		this.midlet = midlet;
	}

	/**
	 * Insert the values taken from a property source into the Hashtable. Any
	 * previous values with the same key should be overridden/overwritten.
	 * 
	 * @param properties
	 *            the Hashtable in which the properties are stored
	 */
	public void insertProperties(Hashtable properties) {
		int counter = 1;
		String numKey = NUMBERED_PROPERTY_PREFIX + counter;
		String propertyValue = midlet.getAppProperty(numKey);
		while (propertyValue != null) {
			int delimiterPosition = propertyValue
					.indexOf(NUMBERED_PROPERTY_DELIMITER);
			if (delimiterPosition != -1) {
				String key = propertyValue.substring(0, delimiterPosition);
				String value = propertyValue.substring(delimiterPosition + 1,
						propertyValue.length());
				properties.put(key, value);
			}
			counter++;
			numKey = NUMBERED_PROPERTY_PREFIX + counter;
			propertyValue = midlet.getAppProperty(numKey);
		}
	}

}
