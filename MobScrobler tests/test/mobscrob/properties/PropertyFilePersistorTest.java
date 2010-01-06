/**
 * PropertyFilePersistorTest.java 
 * 
 * This program is distributed under the terms of the GNU General Public 
 * License
 * Copyright 2008 NJ Pearman
 *
 * This file is part of MobScrob.
 *
 * MobScrob is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MobScrob is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MobScrob.  If not, see <http://www.gnu.org/licenses/>.
 */
package mobscrob.properties;

import java.util.Vector;

import mobscrob.properties.PropertyFilePersistor.KeyValuePair;
import mobscrob.util.test.IInputStream;
import mobscrob.util.test.IOutputStream;
import mobscrob.util.test.MockInputStream;
import mobscrob.util.test.MockOutputStream;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @author Neill
 *
 */
public class PropertyFilePersistorTest extends MockObjectTestCase {

	private PropertyFilePersistor testProps;
	private Mock mockIS;
	private Mock mockOS;
	private Vector result;
	
	public void testReadKeyValues8Bit() throws Exception {
		MockInputStream isWrapper; 
		KeyValuePair kv ;
		testProps = new PropertyFilePersistor();

		// single line
		mockIS = mock(IInputStream.class);
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue(-1));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'b'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'='));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'a'));
		isWrapper = new MockInputStream((IInputStream)mockIS.proxy());
		
		result = testProps.readKeyValues(isWrapper);
		assertNotNull(result);
		assertEquals(1, result.size());
		kv = (KeyValuePair)result.elementAt(0);
		assertEquals("a", kv.key);
		assertEquals("b", kv.value);
		
		
		// comments and multiple lines
		mockIS = mock(IInputStream.class);
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue(-1));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'d'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'='));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'c'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'\n'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'b'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'='));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'a'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'\n'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'e'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'r'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'o'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'n'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'g'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'i'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'#'));
		isWrapper = new MockInputStream((IInputStream)mockIS.proxy());
		
		result = testProps.readKeyValues(isWrapper);
		assertNotNull(result);
		assertEquals(2, result.size());
		kv = (KeyValuePair)result.elementAt(0);
		assertEquals("a", kv.key);
		assertEquals("b", kv.value);
		kv = (KeyValuePair)result.elementAt(1);
		assertEquals("c", kv.key);
		assertEquals("d", kv.value);
		
	}

	public void testReadKeyValues16BitBigEndian() throws Exception {
		MockInputStream isWrapper; 
		KeyValuePair kv ;
		testProps = new PropertyFilePersistor();

		// single line
		mockIS = mock(IInputStream.class);
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue(-1));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'b'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'='));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'a'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0xFF));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0xFE));
		isWrapper = new MockInputStream((IInputStream)mockIS.proxy());
		
		result = testProps.readKeyValues(isWrapper);
		assertNotNull(result);
		assertEquals(1, result.size());
		kv = (KeyValuePair)result.elementAt(0);
		assertEquals("a", kv.key);
		assertEquals("b", kv.value);
		
		
		// comments and multiple lines
		mockIS = mock(IInputStream.class);
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue(-1));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'d'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'='));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'c'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'\n'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'b'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'='));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'a'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'\n'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'e'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'r'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'o'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'n'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'g'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'i'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'#'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0xFF));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0xFE));
		isWrapper = new MockInputStream((IInputStream)mockIS.proxy());
		
		result = testProps.readKeyValues(isWrapper);
		assertNotNull(result);
		assertEquals(2, result.size());
		kv = (KeyValuePair)result.elementAt(0);
		assertEquals("a", kv.key);
		assertEquals("b", kv.value);
		kv = (KeyValuePair)result.elementAt(1);
		assertEquals("c", kv.key);
		assertEquals("d", kv.value);
		
	}

	public void testReadKeyValues16BitLittleEndian() throws Exception {
		MockInputStream isWrapper; 
		KeyValuePair kv ;
		testProps = new PropertyFilePersistor();

		// single line
		mockIS = mock(IInputStream.class);
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue(-1));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'b'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'='));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'a'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0xFE));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0xFF));
		isWrapper = new MockInputStream((IInputStream)mockIS.proxy());
		
		result = testProps.readKeyValues(isWrapper);
		assertNotNull(result);
		assertEquals(1, result.size());
		kv = (KeyValuePair)result.elementAt(0);
		assertEquals("a", kv.key);
		assertEquals("b", kv.value);
		
		
		// comments and multiple lines
		mockIS = mock(IInputStream.class);
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue(-1));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'d'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'='));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'c'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'\n'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'b'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'='));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'a'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'\n'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'e'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'r'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'o'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'n'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'g'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'i'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0x00));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)'#'));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0xFE));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)0xFF));
		isWrapper = new MockInputStream((IInputStream)mockIS.proxy());
		
		result = testProps.readKeyValues(isWrapper);
		assertNotNull(result);
		assertEquals(2, result.size());
		kv = (KeyValuePair)result.elementAt(0);
		assertEquals("a", kv.key);
		assertEquals("b", kv.value);
		kv = (KeyValuePair)result.elementAt(1);
		assertEquals("c", kv.key);
		assertEquals("d", kv.value);
		
	}
	
	public void testWritePropertyToStream() throws Exception {
		MockOutputStream osWrapper;
		byte[] testKey, testValue;
		
		testProps = new PropertyFilePersistor();
		
		testKey = "KEY".getBytes();
		testValue = "VALUE".getBytes();
		mockOS = mock(IOutputStream.class);
		mockOS.expects(once()).method("write").with(eq((int)PropertyFilePersistor.CHAR_CARRIAGE_RETURN));
		mockOS.expects(once()).method("write").with(isA(byte[].class), eq(0), eq(5));
		mockOS.expects(once()).method("write").with(eq((int)PropertyFilePersistor.CHAR_EQUALS));
		mockOS.expects(once()).method("write").with(isA(byte[].class), eq(0), eq(3));
		
		osWrapper = new MockOutputStream((IOutputStream)mockOS.proxy());
		testProps.writePropertyToStream(osWrapper, testKey, testValue);
	}
}
