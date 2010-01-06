/**
 * ScrobbleFilePersister.java
 * @author NJ Pearman
 * @date 4 Oct 2008
 */
package mobscrob.player;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import mobscrob.id3.TrackMetadata;
import mobscrob.id3.TrackMetadata.TrackMetadataSerializer;
import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;
import mobscrob.util.StreamUtil;

/**
 * @author Neill
 *
 */
public class TrackMetadataFilePersistor implements TrackMetadataPersistor {
	
	private static final Log log = LogFactory.getLogger(TrackMetadata.class);

	public static final String SCROBBLE_QUEUE_FILENAME = "file:///c:/music/mobscrob.data";

	private TrackMetadataSerializer serializer;
	private String filename;
	
	public TrackMetadataFilePersistor(String filename, TrackMetadataSerializer serializer) {
		this.filename = filename;
		this.serializer = serializer;
	}
	
	public boolean save(PlayProcessor processor) {
		final String methodName = "1";
		
		boolean success = true;
		StringBuffer contents = new StringBuffer();
		TrackMetadata[] tracks = processor.getQueueSnapshot();
		
		log.info(methodName, "Have " + tracks.length + " queued track(s) to save to file");

		// if queue not empty, serialized the queue elements
		if (tracks.length > 0) {
			String serializedTrack;
			for (int i=0; i<tracks.length; i++) {
				try {
					serializedTrack = new String(
							serializer.serialize(tracks[i]));
					contents.append(serializedTrack).append('\n');
				} catch(IOException e) {
					log.error(methodName, "Unable to serialize track "+tracks[i]);
				}
			}
		}

		// persist queue to file
		// need to obfuscate or compress contents
		FileConnection fc = null;
		DataOutputStream dos = null;
		try {
			fc = (FileConnection) Connector.open(filename);
			if (!fc.exists()) {
				log.info(methodName, "Creating data file");
				fc.create();
			} else {
				log.info(methodName, "Already have file");
			}
			fc.setHidden(true);

			byte[] bytes = contents.toString().getBytes();
			dos = fc.openDataOutputStream();
			dos.write(bytes);
			fc.truncate(bytes.length);
			log.info(methodName, "Saved queue to file");
		} catch (IOException e) {
			log.error(methodName, "Error saving queue to file: "
					+ e.getMessage(), e);
		} finally {
			if (dos != null) {
				try {
					dos.flush();
					dos.close();
				} catch (Exception e) {
				}
			}
			if (fc != null) {
				try { fc.close(); } 
				catch (Exception e) {}
			}
		}
		
		return success;
	}
	
	public void load(PlayProcessor processor) {
		final String methodName = "2";

		// override so that we can first read the queue from file
		FileConnection fc = null;
		InputStream is = null;
		try {
			// open file
			fc = (FileConnection) Connector.open(filename);
			if (fc.exists()) {
				log.info(methodName, "Loading queue from file");

				is = fc.openInputStream();

				// deserialize and queue TrackMetadata objects
				TrackMetadata track;
				do {
					track = serializer.deserialize(is);
					log.info(methodName, "Deserialized track from file: "
							+ track);
					processor.queueTrack(track);
				} while (!track.isInvalidID3Tag());

			} else {
				log.info(methodName, "No queue to load from file");
			}
		} catch (Exception e) {
			log.error(methodName, "Unable to load queue from file: "
					+ e.getMessage(), e);
		} finally {
			StreamUtil.closeInputStream(is);
			if (fc != null) {
				try { fc.close(); } 
				catch (Exception e) {}
			}
		}
	}
}
