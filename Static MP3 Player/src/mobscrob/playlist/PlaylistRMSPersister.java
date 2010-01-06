/**
 * PlaylistRMSPersister.java
 * @author NJ Pearman
 * @date 4 Oct 2008
 */
package mobscrob.playlist;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;
import mobscrob.playlist.Playlist.PlaylistFile;
import mobscrob.util.StreamUtil;
import mobscrob.util.microedition.RecordStoreUtil;

/**
 * @author Neill
 *
 */
public class PlaylistRMSPersister {
	
	private static final Log log = LogFactory.getLogger(PlaylistRMSPersister.class);

	private static final String RECORD_STORE_NAME = "mobscrob.playlist";
		
	/* (non-Javadoc)
	 * @see mobscrob.properties.PropertyPersistor#load(mobscrob.properties.MobScrobProperties)
	 */
	public boolean load(final Playlist playlist) {
		final String methodName = "1";
		
		boolean success = true;
		
		try {
			final RecordStore rs = RecordStore.openRecordStore(RECORD_STORE_NAME, true);

			try {
				RecordStoreUtil.processAllRecords(rs, 
					new RecordStoreUtil.RecordProcessor() {
						public void process(int recordIndex, byte[] bytes) throws IOException, RecordStoreException {
							try {
								playlist.addToPlaylist(getFileFromBytes(bytes));
								rs.deleteRecord(recordIndex);
							} catch (PlaylistFullException e) {
								throw new IOException(e.getMessage());
							}
						}
					});
				
				log.info(methodName, "Loaded playlist, "+rs.getNumRecords()+" records from RMS");
			} catch (RecordStoreFullException e) {
				log.error(methodName, "Playlist full: "+e.getMessage(), e);
				success = false;
			} catch (RecordStoreNotFoundException e) {
				log.error(methodName, "Playlist store not found: "+e.getMessage(), e);
				success = false;
			} catch (RecordStoreException e) {
				log.error(methodName, "Unable to load playlist: "+e.getMessage(), e);
				success = false;
			} catch (IOException e) {
				log.error(methodName, "IO Error trying to load playlist: "+e.getMessage(), e);
				success = false;
			} catch(Exception e) {
				log.error(methodName, "Unexpected error: "+e.getMessage(), e);
			} finally {
				RecordStoreUtil.closeRecordStore(rs);
			}
		} catch (RecordStoreException e) {
			log.error(methodName, "Unable to get playlist from RMS: "+e.getMessage(), e);
		}
		
		return success;
	}

	/* (non-Javadoc)
	 * @see mobscrob.properties.PropertyPersistor#save(mobscrob.properties.MobScrobProperties)
	 */
	public void save(Playlist playlist) {
		final String methodName = "2";
		
		RecordStore rs = null;
		
		try {
			// open the record store
			rs = RecordStore.openRecordStore(RECORD_STORE_NAME, true);
			
			Vector entries = playlist.getPlaylist();
			byte[] fileBytes;
			PlaylistFile file;
			
			// add any new entries required
			for(Enumeration e=entries.elements(); e.hasMoreElements(); ) {
				file = (PlaylistFile)e.nextElement();
				fileBytes = writePlaylistFileToByteArray(file);
				rs.addRecord(fileBytes, 0, fileBytes.length);
			}
			
			log.info(methodName, "Saved playlist, num entries: "+rs.getNumRecords());
		} catch (RecordStoreFullException e) {
			log.error(methodName, "Playlist full: "+e.getMessage(), e);
		} catch (RecordStoreNotFoundException e) {
			log.error(methodName, "Playlist store not found: "+e.getMessage(), e);
		} catch (RecordStoreException e) {
			log.error(methodName, "Unable to save playlist: "+e.getMessage(), e);
		} catch (IOException e) {
			log.error(methodName, "IO error while saving playlist: "+e.getMessage(), e);
		} catch(Exception e) {
			log.error(methodName, "Unexpected error: "+e.getMessage(), e);
		} finally {
			RecordStoreUtil.closeRecordStore(rs);
		}
	}

	/**
	 * Converts the specified File object to a byte array.
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private byte[] writePlaylistFileToByteArray(PlaylistFile file) throws IOException {
		final String methodName = "3";
		
		ByteArrayOutputStream baos;
		DataOutputStream out = null;
		
		try {
			log.info(methodName, "Writing "+file.getName());
			baos = new ByteArrayOutputStream();
			out = new DataOutputStream(baos);
			
			out.writeUTF(file.getName());
			out.writeUTF(file.getLocation());
			
			return baos.toByteArray();
		} finally {
			StreamUtil.closeOutputStream(out);
		}
	}
	
	/**
	 * Converts the specified byte array to a File object.
	 * @param fileBytes
	 * @return
	 * @throws IOException
	 */
	private PlaylistFile getFileFromBytes(byte[] fileBytes) throws IOException {
		final String methodName = "4";
		
		ByteArrayInputStream bais;
		DataInputStream in = null;
		
		try {
			bais = new ByteArrayInputStream(fileBytes);
			in = new DataInputStream(bais);
			
			// need to read position, but don't need to store it
			String name = in.readUTF();
			String loc = in.readUTF();
			
			log.info(methodName, "Read "+name);
			return new PlaylistFile(name, loc);
		} finally {
			StreamUtil.closeInputStream(in);
		}
	}
}
