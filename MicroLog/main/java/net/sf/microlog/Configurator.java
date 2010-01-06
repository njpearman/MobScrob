package net.sf.microlog;

/**
 * The interface that can configure a Logger.
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 * 
 */
public interface Configurator {

	/**
	 * Configure the specified Logger.
	 * 
	 * @param logger
	 *            the logger the <code>Logger</code> to configure.
	 */
	void configure(Logger logger);

}
