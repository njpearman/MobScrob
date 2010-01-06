/**
 * 
 */
package mobscrob.navigator;

/**
 * Interface for creating DirectoryNodes
 * 
 * @author Neill
 * 
 */
public interface DirectoryNodeManager {
	DirectoryNode instance(String name, DirectoryNode parent);
}
