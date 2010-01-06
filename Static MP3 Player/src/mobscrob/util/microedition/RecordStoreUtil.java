/**
 * RecordStoreUtil.java
 * @author NJ Pearman
 * @date 4 Oct 2008
 */
package mobscrob.util.microedition;

import java.io.IOException;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 * @author Neill
 *
 */
public class RecordStoreUtil {

	/**
	 * Utility class, so private constructor
	 */
	private RecordStoreUtil () {}
	
	public static void closeRecordStore(RecordStore rs) {
		if(rs != null) {
			try { rs.closeRecordStore(); }
			catch(Exception e) {}
		}
	}
	
	/**
	 * Iterates all records in the specified RecordStore and processes each record with the specified
	 * implementation of RecordProcessor. 
	 * @param rs
	 * @param processor
	 * @throws RecordStoreException
	 * @throws IOException
	 */
	public static void processAllRecords(RecordStore rs, RecordProcessor processor) throws RecordStoreException, IOException {
		int num = rs.getNumRecords();
		int next = rs.getNextRecordID();
		byte[] fileBytes;
		for(int i=(next-num); i<next; i++) {
			fileBytes = rs.getRecord(i);
			processor.process(i, fileBytes);
		}
		
	}
	
	/**
	 * Interface for allowing classes that call processAllRecords() to
	 * perform custom functionality within the method call.  Typically
	 * would be used to create an anonymous class at invocation. 
	 * @author Neill
	 *
	 */
	public static interface RecordProcessor {
		/**
		 * Interface for processing an individual RecordStore record,
		 * represented by a byte array.
		 * @throws IOException
		 * @throws RecordStoreException 
		 */
		void process(int recordIndex, byte[] bytes) throws IOException, RecordStoreException;
	}
}
