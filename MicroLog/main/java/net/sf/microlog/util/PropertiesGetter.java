package net.sf.microlog.util;

/**
 * The PropertiesGetter has all the ways to get at the properties. Any
 * Property-like object should implement this.
 * 
 * @author Darius Katz
 */
public interface PropertiesGetter {

	/**
	 * Returns the Object to which the specified key is mapped.
	 * 
	 * @param key
	 *            the key associated to the stored Object
	 * 
	 * @return the Object to which the key is mapped; null if the key is not
	 *         mapped to any Object.
	 */
	public Object get(String key);

	/**
	 * Returns the String to which the specified key is mapped.
	 * 
	 * @param key
	 *            the key associated to the stored Object
	 * 
	 * @return the String to which the key is mapped; null if the key is not
	 *         mapped to any String.
	 */
	public String getString(String key);

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
	public Object getDefaultValue(String key);

}
