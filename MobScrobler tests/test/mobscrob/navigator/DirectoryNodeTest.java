/**
 * 
 */
package mobscrob.navigator;

import java.util.Enumeration;
import java.util.Vector;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @author Neill
 *
 */
public class DirectoryNodeTest extends MockObjectTestCase {

	private DirectoryNode testNode;
	private Mock mockNodeManager;
	
	/**
	 * Test method for {@link mobscrob.navigator.DirectoryNode#addSorted(java.util.Enumeration, java.util.Vector, java.util.Vector)}.
	 */
	public void testAddSorted() throws Exception {
		Vector testItems;
		Enumeration e;
		
		//
		//
		//
		testNode = new DirectoryNode("test", null, null);
		testNode.children = new Vector();
		testNode.filenames = new Vector();

		testItems = new Vector();
		testItems.add("aaa.mp3");
		testItems.add("bbb.mp3");
		testItems.add("ccc.mp3");
		testItems.add("AAA.mp3");
		testItems.add("111.mp3");
		e = testItems.elements();
		
		testNode.addSorted(e);
		assertEquals(0, testNode.children.size());
		assertEquals(5, testNode.filenames.size());
		assertEquals("111.mp3", testNode.filenames.elementAt(0));
		assertEquals("aaa.mp3", testNode.filenames.elementAt(1));
		assertEquals("AAA.mp3", testNode.filenames.elementAt(2));
		assertEquals("bbb.mp3", testNode.filenames.elementAt(3));
		assertEquals("ccc.mp3", testNode.filenames.elementAt(4));
		
		//
		//
		//
		testNode = new DirectoryNode("test", null, null);
		testNode.children = new Vector();
		testNode.filenames = new Vector();
		
		testItems = new Vector();
		testItems.add("aaa.mp3");
		testItems.add("eee.mp3");
		testItems.add("ccc.mp3");
		testItems.add("aBC.mp3");
		testItems.add("ddd.mp3");
		e = testItems.elements();
		
		testNode.addSorted(e);
		assertEquals(0, testNode.children.size());
		assertEquals(5, testNode.filenames.size());
		assertEquals("aaa.mp3", testNode.filenames.elementAt(0));
		assertEquals("aBC.mp3", testNode.filenames.elementAt(1));
		assertEquals("ccc.mp3", testNode.filenames.elementAt(2));
		assertEquals("ddd.mp3", testNode.filenames.elementAt(3));
		assertEquals("eee.mp3", testNode.filenames.elementAt(4));
		
		//
		//
		//
		mockNodeManager = mock(DirectoryNodeManager.class);
		mockNodeManager.expects(once()).method("instance").with(
				isA(String.class), isA(DirectoryNode.class))
				.will(returnValue(new DirectoryNode("ddd/", null, null)));
		mockNodeManager.expects(once()).method("instance").with(
				isA(String.class), isA(DirectoryNode.class))
				.will(returnValue(new DirectoryNode("aBC/", null, null)));
		mockNodeManager.expects(once()).method("instance").with(
				isA(String.class), isA(DirectoryNode.class))
				.will(returnValue(new DirectoryNode("ccc/", null, null)));
		mockNodeManager.expects(once()).method("instance").with(
				isA(String.class), isA(DirectoryNode.class))
				.will(returnValue(new DirectoryNode("eee/", null, null)));
		mockNodeManager.expects(once()).method("instance").with(
				isA(String.class), isA(DirectoryNode.class))
				.will(returnValue(new DirectoryNode("aaa/", null, null)));
		
		testNode = new DirectoryNode("test", null, (DirectoryNodeManager)mockNodeManager.proxy());
		testNode.children = new Vector();
		testNode.filenames = new Vector();
		
		testItems = new Vector();
		testItems.add("aaa/");
		testItems.add("eee/");
		testItems.add("ccc/");
		testItems.add("aBC/");
		testItems.add("ddd/");
		e = testItems.elements();
		
		testNode.addSorted(e);
		assertEquals(5, testNode.children.size());
		assertEquals(0, testNode.filenames.size());
		assertEquals("aaa/", ((DirectoryNode)testNode.children.elementAt(0)).name);
		assertEquals("aBC/", ((DirectoryNode)testNode.children.elementAt(1)).name);
		assertEquals("ccc/", ((DirectoryNode)testNode.children.elementAt(2)).name);
		assertEquals("ddd/", ((DirectoryNode)testNode.children.elementAt(3)).name);
		assertEquals("eee/", ((DirectoryNode)testNode.children.elementAt(4)).name);
	}
}
