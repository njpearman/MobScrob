/**
 * 
 */
package mobscrob.event;

/**
 * Interface for classes wishing to perform commands on files
 * 
 * @author Neill
 * 
 */
public interface FileCommandListener {

	public static final int SELECT = 1;

	/**
	 * Method to perform an action on the specified file, based on the supplied
	 * command type
	 * 
	 * @param command
	 * @param file
	 */
	void perform(int command, String file);
}
