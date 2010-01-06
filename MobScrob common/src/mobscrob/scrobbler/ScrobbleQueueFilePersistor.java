/**
 * ScrobbleQueueFilePersistor.java
 * @author NJ Pearman
 * @date 19 Oct 2008
 */
package mobscrob.scrobbler;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import mobscrob.id3.TrackMetadata;
import mobscrob.id3.TrackMetadata.TrackMetadataSerializer;
import mobscrob.util.StreamUtil;

/**
 * @author Neill
 *
 */
public abstract class ScrobbleQueueFilePersistor {

	protected static final char NEW_LINE = '\n';
	
	private TrackMetadataSerializer serializer;
	
	public ScrobbleQueueFilePersistor(TrackMetadataSerializer serializer) {
		this.serializer = serializer;
	}
	
	protected void writeFileContents(OutputStream os, TrackQueuer queuer) {
		// write header
		
		// write each track to stream
		TrackMetadata[] tracks = queuer.getQueueSnapshot();
		
		// if queue not empty, serialized the queue elements
		for (int i=0; i<tracks.length; i++) {
			try {
				// add EOL to stream
				os.write(NEW_LINE);
					
				// now serialize track
				os.write(serializer.serialize(tracks[i]));

			} catch(IOException e) {
				//log.error(methodName, "Unable to serialize track "+tracks[i]);
			}
		}
	}
	
	/**
	 * Reads the InputStream for serialized TrackMetadata objects and queues them
	 * in the TrackQueuer.
	 * @param contentStream
	 * @param queuer
	 * @return
	 * @throws IOException
	 */
	protected void readFileContents(InputStream contentStream, 
			TrackQueuer queuer) throws IOException {
		// read the header first, to get application, device(?) and file version
		
		// deserialize and queue TrackMetadata objects
		try {
			// read the EOL
			while(contentStream.read() == '\n') {
				queuer.queueTrack(serializer.deserialize(contentStream));
			}
		} catch(EOFException e) {
			// finished reading unexpectedly
		} finally {
			StreamUtil.closeInputStream(contentStream);
		}
	}
}
