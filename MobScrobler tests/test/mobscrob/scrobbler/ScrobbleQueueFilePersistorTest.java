/**
 * ScrobbleQueueFilePersistorTest.java
 * @author NJ Pearman
 * @date 19 Oct 2008
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
package mobscrob.scrobbler;

import java.io.InputStream;

import mobscrob.id3.TrackMetadata;
import mobscrob.id3.TrackMetadata.TrackMetadataSerializer;
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
public class ScrobbleQueueFilePersistorTest extends MockObjectTestCase {

	private ScrobbleQueueFilePersistor testPersistor;
	private Mock mockSerializer;
	private Mock mockQueuer;
	private Mock mockOS;
	private Mock mockIS;
	
	/**
	 * Test method for {@link mobscrob.scrobbler.ScrobbleQueueFilePersistor#writeFileContents(java.io.OutputStream, mobscrob.scrobbler.TrackQueuer)}.
	 */
	public void testWriteFileContents() throws Exception {
		TrackMetadata[] tracks;
		
		// no tracks to save
		tracks = new TrackMetadata[0];
		mockSerializer = mock(TrackMetadataSerializer.class);
		mockQueuer = mock(TrackQueuer.class);
		mockQueuer.expects(once()).method("getQueueSnapshot").withNoArguments().will(returnValue(tracks));
		mockOS = mock(IOutputStream.class);
		testPersistor = new ScrobbleQueueFilePersistor((TrackMetadataSerializer)mockSerializer.proxy()) {};
		testPersistor.writeFileContents(new MockOutputStream((IOutputStream)mockOS.proxy()), (TrackQueuer)mockQueuer.proxy());
		
		// one blank tracks to save
		tracks = new TrackMetadata[1];
		tracks[0] = new TrackMetadata();
		mockSerializer = mock(TrackMetadataSerializer.class);
		mockSerializer.expects(once()).method("serialize").with(isA(TrackMetadata.class)).will(returnValue(new byte[]{ 0 }));
		mockQueuer = mock(TrackQueuer.class);
		mockQueuer.expects(once()).method("getQueueSnapshot").withNoArguments().will(returnValue(tracks));
		mockOS = mock(IOutputStream.class);
		mockOS.expects(once()).method("write").with(eq((int)ScrobbleQueueFilePersistor.NEW_LINE));
		mockOS.expects(once()).method("write").with(
				isA(byte[].class), eq(0), eq(1));
		testPersistor = new ScrobbleQueueFilePersistor((TrackMetadataSerializer)mockSerializer.proxy()) {};
		testPersistor.writeFileContents(new MockOutputStream((IOutputStream)mockOS.proxy()), (TrackQueuer)mockQueuer.proxy());
		
		// three blank tracks to save
		tracks = new TrackMetadata[3];
		tracks[0] = new TrackMetadata();
		tracks[1] = new TrackMetadata();
		tracks[2] = new TrackMetadata();
		mockSerializer = mock(TrackMetadataSerializer.class);
		mockSerializer.expects(exactly(3)).method("serialize").with(isA(TrackMetadata.class)).will(returnValue(new byte[]{ 0 }));
		mockQueuer = mock(TrackQueuer.class);
		mockQueuer.expects(once()).method("getQueueSnapshot").withNoArguments().will(returnValue(tracks));
		mockOS = mock(IOutputStream.class);
		mockOS.expects(exactly(3)).method("write").with(eq((int)ScrobbleQueueFilePersistor.NEW_LINE));
		mockOS.expects(exactly(3)).method("write").with(
				isA(byte[].class), eq(0), eq(1));
		testPersistor = new ScrobbleQueueFilePersistor((TrackMetadataSerializer)mockSerializer.proxy()) {};
		testPersistor.writeFileContents(new MockOutputStream((IOutputStream)mockOS.proxy()), (TrackQueuer)mockQueuer.proxy());
		
	}

	/**
	 * Test method for {@link mobscrob.scrobbler.ScrobbleQueueFilePersistor#readFileContents(java.io.InputStream, mobscrob.scrobbler.TrackQueuer)}.
	 * @throws Exception 
	 */
	public void testReadFileContents() throws Exception {
		// empty stream
		mockSerializer = mock(TrackMetadataSerializer.class);
		mockQueuer = mock(TrackQueuer.class);
		mockIS = mock(IInputStream.class);
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue(-1));
		testPersistor = new ScrobbleQueueFilePersistor((TrackMetadataSerializer)mockSerializer.proxy()){};
		testPersistor.readFileContents(
				new MockInputStream((IInputStream)mockIS.proxy()), 
				(TrackQueuer)mockQueuer.proxy());

		// one track in stream
		mockSerializer = mock(TrackMetadataSerializer.class);
		mockSerializer.expects(once()).method("deserialize").with(isA(InputStream.class)).will(returnValue(new TrackMetadata()));
		mockQueuer = mock(TrackQueuer.class);
		mockQueuer.expects(once()).method("queueTrack").with(isA(TrackMetadata.class));
		mockIS = mock(IInputStream.class);
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue(-1));
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue((int)ScrobbleQueueFilePersistor.NEW_LINE));
		testPersistor = new ScrobbleQueueFilePersistor((TrackMetadataSerializer)mockSerializer.proxy()){};
		testPersistor.readFileContents(
				new MockInputStream((IInputStream)mockIS.proxy()), 
				(TrackQueuer)mockQueuer.proxy());
		
		// three tracks in stream
		mockSerializer = mock(TrackMetadataSerializer.class);
		mockSerializer.expects(exactly(3)).method("deserialize").with(isA(InputStream.class)).will(returnValue(new TrackMetadata()));
		mockQueuer = mock(TrackQueuer.class);
		mockQueuer.expects(exactly(3)).method("queueTrack").with(isA(TrackMetadata.class));
		mockIS = mock(IInputStream.class);
		mockIS.expects(once()).method("read").withNoArguments().will(returnValue(-1));
		mockIS.expects(exactly(3)).method("read").withNoArguments().will(returnValue((int)ScrobbleQueueFilePersistor.NEW_LINE));
		testPersistor = new ScrobbleQueueFilePersistor((TrackMetadataSerializer)mockSerializer.proxy()){};
		testPersistor.readFileContents(
				new MockInputStream((IInputStream)mockIS.proxy()), 
				(TrackQueuer)mockQueuer.proxy());
		
	}
}
