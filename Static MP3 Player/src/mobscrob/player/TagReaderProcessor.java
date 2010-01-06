/**
 * 
 */
package mobscrob.player;

import java.io.IOException;

import mobscrob.id3.ID32TagReader;
import mobscrob.id3.TrackMetadata;
import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;
import mobscrob.mp3.InfoUnavailableException;
import mobscrob.mp3.MP3Stream;
import mobscrob.util.StreamUtil;

/**
 * @author Neill
 * 
 */
public class TagReaderProcessor extends PlayProcessorImpl {
private static final Log log = LogFactory.getLogger(TagReaderProcessor.class);

	private static final int WAIT_TIME = 5000;

	public TagReaderProcessor() {
		super(WAIT_TIME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mobscrob.player.PlayProcessorImpl#process(mobscrob.id3.TrackMetadata)
	 */
	public void process(TrackMetadata next) {
		final String methodName = "1";
		MP3Stream stream = null;

		try {
			// reading the tag for the file
			stream = MP3Stream.instance(next.getFileLocation());
			ID32TagReader reader = new ID32TagReader(stream);
			reader.readInto(next);
			log.info(methodName, "Got tag info for " + next);
			next.updated();
		} catch (IOException e) {
			log.error(methodName,
					  "Error trying to read tag: " + e.getMessage(), e);
			next.setInvalidID3Tag(true);
		} catch (InfoUnavailableException e) {
			log.error(methodName,
					"Error trying to read tag: " + e.getMessage(), e);
			next.setInvalidID3Tag(true);
		} finally {
			StreamUtil.closeInputStream(stream);
		}
	}

	public void shutdown() {
		// nothing to do
	}
}
