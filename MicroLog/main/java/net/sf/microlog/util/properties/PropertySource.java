package net.sf.microlog.util.properties;

import java.util.Hashtable;

/**
 * An interface that every property source must implement.
 * 
 * @author Darius Katz
 */
public interface PropertySource {

	/**
	 * Insert the values taken from a property source into the Hashtable. Any
	 * previous values with the same key should be overridden/overwritten.
	 * 
	 * @param properties
	 *            the Hashtable in which the properties are stored
	 * 
	 */
	void insertProperties(Hashtable properties);

}
