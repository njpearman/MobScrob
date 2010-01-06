/**
 * PropertyRMSReader.java
 * @author NJ Pearman
 * @date 3 Oct 2008
 */
package mobscrob.properties;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;
import mobscrob.util.StreamUtil;
import mobscrob.util.microedition.RecordStoreUtil;

/**
 * @author Neill
 *
 */
public class PropertyRMSPersistor implements PropertyPersistor {

	private static final Log log = LogFactory.getLogger(PropertyRMSPersistor.class);
	
	private static final String RECORD_STORE_NAME = "mobscrob.properties";
	
	private static final int RS_INDEX_USERNAME = 1;
	private static final int RS_INDEX_PASSWORD = 2;
	private static final int RS_INDEX_SCROBBLE_OFFLINE = 3;
	
	/* (non-Javadoc)
	 * @see mobscrob.properties.PropertyReader#load(mobscrob.properties.MobScrobProperties)
	 */
	public boolean load(MobScrobProperties properties) {
		final String methodName = "1";
		
		boolean success = true;
		RecordStore rs = null;
		
		try {
			rs = RecordStore.openRecordStore(RECORD_STORE_NAME, true);
			
			byte[] username = rs.getRecord(RS_INDEX_USERNAME);
			properties.setUsername(getPropertyValue(username, KEY_USERNAME));
			byte[] password = rs.getRecord(RS_INDEX_PASSWORD);
			properties.setHashedPassword(getPropertyValue(password, KEY_HASHED_PASSWORD));
			byte[] exportQueue = rs.getRecord(RS_INDEX_SCROBBLE_OFFLINE);
			properties.setScrobbleOffline("true".equalsIgnoreCase(getPropertyValue(exportQueue, KEY_SCROBBLE_OFFLINE)));
			
			log.info(methodName, "Loaded RMS properties: "+properties.getUsername()+", "+properties.getHashedPassword());
		} catch (RecordStoreFullException e) {
			log.error(methodName, "No room in record store: "+e.getMessage(), e);
			success = false;
		} catch (RecordStoreNotFoundException e) {
			log.error(methodName, "Record store doesn't exist: "+e.getMessage(), e);
			success = false;
		} catch (RecordStoreException e) {
			log.error(methodName, "Problem with record store: "+e.getMessage(), e);
			success = false;
		} catch (IOException e) {
			log.error(methodName, "Error reading from stream: "+e.getMessage(), e);
			success = false;
		} finally {
			RecordStoreUtil.closeRecordStore(rs);
		}
		
		return success;
	}

	/* (non-Javadoc)
	 * @see mobscrob.properties.PropertyReader#save(mobscrob.properties.MobScrobProperties)
	 */
	public void save(MobScrobProperties properties) {
		final String methodName = "2";

		RecordStore rs = null;
		
		try {
			// open record store
			rs = RecordStore.openRecordStore(RECORD_STORE_NAME, true);

			// write properties to record store
			byte[] username = getPropertyAsByteArray(KEY_USERNAME, properties.getUsername());
			setProperty(rs, RS_INDEX_USERNAME, username);

			byte[] pwd = getPropertyAsByteArray(KEY_HASHED_PASSWORD, properties.getHashedPassword());
			setProperty(rs, RS_INDEX_PASSWORD, pwd);

			byte[] exportQueue = getPropertyAsByteArray(KEY_SCROBBLE_OFFLINE, String.valueOf(properties.scrobbleOffline()));
			setProperty(rs, RS_INDEX_SCROBBLE_OFFLINE, exportQueue);
			
		} catch(IOException e) {
			log.error(methodName, "Error writing to stream: "+e.getMessage(), e);
		} catch (RecordStoreFullException e) {
			log.error(methodName, "No room in record store: "+e.getMessage(), e);
		} catch (RecordStoreNotFoundException e) {
			log.error(methodName, "Record store doesn't exist: "+e.getMessage(), e);
		} catch (RecordStoreException e) {
			log.error(methodName, "Problem with record store: "+e.getMessage(), e);
		} finally {
			RecordStoreUtil.closeRecordStore(rs);
		}
	}
	
	private void setProperty(RecordStore rs, int recordID, byte[] bytes) throws RecordStoreNotOpenException, RecordStoreFullException, InvalidRecordIDException, RecordStoreException {
		final String methodName = "3";
		if(rs.getNextRecordID() <= recordID) {
			// add new record
			rs.addRecord(bytes, 0, bytes.length);
			log.info(methodName, "Added new property at "+recordID);
		} else {
			// update record
			rs.setRecord(recordID, bytes, 0, bytes.length);
			log.info(methodName, "Set existing property at "+recordID);
		}
	}

	private byte[] getPropertyAsByteArray(String key, String value) throws IOException {
		ByteArrayOutputStream baos;
		DataOutputStream out = null;
		try {
			baos = new ByteArrayOutputStream();
			out = new DataOutputStream(baos);
			
			out.writeUTF(key);
			out.writeUTF(value);
			out.flush();
			
			return baos.toByteArray();
		} finally {
			StreamUtil.closeOutputStream(out);
		}
	}
	
	private String getPropertyValue(byte[] bytes, String keyName) throws IOException {
		ByteArrayInputStream bais;
		DataInputStream in = null;
		String key, value;
		try {
			bais = new ByteArrayInputStream(bytes);
			in = new DataInputStream(bais);
			key = in.readUTF();
			if(!keyName.equals(key)) {
				throw new IOException("Property key not as expected. Wanted "+keyName+", got"+key);
			}
			value = in.readUTF();
			return value;
		} finally {
			StreamUtil.closeInputStream(in);
		}
	}
}
