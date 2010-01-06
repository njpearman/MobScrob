/**
 * 
 */
package mobscrob.navigator;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.file.FileSystemRegistry;

import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;

/**
 * @author Neill
 * 
 */
public class SystemRootNode extends DirectoryNode {
	private static final Log log = LogFactory.getLogger(SystemRootNode.class);

	private static final String SYSTEM_ROOT_NAME = "file:///";

	public SystemRootNode(DirectoryNodeManager manager) {
		super(SYSTEM_ROOT_NAME, null, manager);
	}

	public void loadChildrenAndFiles() throws IOException {
		final String methodName = "1";
		log.info(methodName, "Loading file system root...");
		// load roots from file registry
		Enumeration roots = FileSystemRegistry.listRoots();

		children = new Vector();
		filenames = new Vector();
		addSorted(roots);

		log.info(methodName, "Number of roots loaded: " + children.size());
	}
}
