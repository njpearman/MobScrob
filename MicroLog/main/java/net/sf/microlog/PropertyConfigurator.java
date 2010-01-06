package net.sf.microlog;

import net.sf.microlog.util.properties.PropertyFile;

/**
 * 
 * A class to configure MicroLog from a property file. The file must be included
 * in the MIDlet jar.
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 * @since 0.5
 */
public class PropertyConfigurator {

	/**
	 * Configure MicroLog from a property file.
	 */
	public static void configure(String fileName) {
		PropertyFile propertyFile = null;
		if (fileName != null) {
			propertyFile = new PropertyFile(fileName);
		}

		Logger logger = Logger.getLogger();

	}
}
