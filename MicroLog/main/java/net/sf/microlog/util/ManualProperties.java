/*
 * ManualProperties.java
 *
 * Created on May 21, 2008.
 */
package net.sf.microlog.util;

import java.util.Hashtable;

/**
 * Properties for manually configuration.
 * 
 * @author Karsten Ohme
 * 
 */
public class ManualProperties implements PropertiesGetter {

	/**
	 * hashtable for holding properties.
	 */
	private final Hashtable hashTable;

	/**
	 * Creates new manual properties which can be filled.
	 */
	public ManualProperties() {
		hashTable = new Hashtable();
	}

	/**
	 * Puts a key value pair in the properties.
	 * <p>
	 * Old values of the same key are overwritten.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the vale
	 */
	public void put(String key, Object value) {
		hashTable.put(key, value);
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
	public Object get(String key) {
		return hashTable.get(key);
	}

	/**
	 * Manual values have no default values.
	 * 
	 * @return always <code>null</code> is returned.
	 */
	public Object getDefaultValue(String key) {
		return null;
	}

	/**
	 * Returns the String representation to which the specified key is mapped.
	 * 
	 * @param key
	 *            the key associated to the stored Object
	 * 
	 * @return the Object to which the key is mapped; null if the key is not
	 *         mapped to any Object in this hashtable.
	 */
	public String getString(String key) {
		Object value = hashTable.get(key);
		return value == null ? null : value.toString();
	}

}
