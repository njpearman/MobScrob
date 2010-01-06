package net.sf.microlog;

/**
 * This class represent the logging level.
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 */
public final class Level {

	public static final int FATAL_INT = 16;

	public static final int ERROR_INT = 8;

	public static final int WARN_INT = 4;

	public static final int INFO_INT = 2;

	public static final int DEBUG_INT = 1;

	public static final int TRACE_INT = 0;

	public static final String FATAL_STRING = "FATAL";

	public static final String ERROR_STRING = "ERROR";

	public static final String WARN_STRING = "WARN";

	public static final String INFO_STRING = "INFO";

	public static final String DEBUG_STRING = "DEBUG";

	public static final String TRACE_STRING = "TRACE";

	public static final Level FATAL = new Level(ERROR_INT, ERROR_STRING);

	public static final Level ERROR = new Level(ERROR_INT, ERROR_STRING);

	public static final Level WARN = new Level(WARN_INT, WARN_STRING);

	public static final Level INFO = new Level(INFO_INT, INFO_STRING);

	public static final Level DEBUG = new Level(DEBUG_INT, DEBUG_STRING);

	public static final Level TRACE = new Level(DEBUG_INT, DEBUG_STRING);

	private int level = 0;

	private String levelString = "";

	/**
	 * Construct a Level object. The levelString param
	 * 
	 * @param level
	 *            the level to create. This should be set using one of the
	 *            constants defined in the class.
	 * @param levelString
	 *            the <code>String</code> that shall represent the level. This
	 *            should be set using one of the defined constants defined in
	 *            the class.
	 */
	private Level(int level, String levelString) {
		this.level = level;
		this.levelString = levelString;
	}

	/**
	 * Return the integer level for this Level.
	 * 
	 * @return the integer level.
	 */
	public int toInt() {
		return level;
	}

	/**
	 * Return a String representation for this Level.
	 * 
	 * @return a <code>String</code> representation for the Level.
	 */
	public String toString() {
		return levelString;
	}
}
