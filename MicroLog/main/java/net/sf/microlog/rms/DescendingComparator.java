/*
 * DescendingComparator.java
 *
 * Created on den 18 oktober 2005, 13:36
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package net.sf.microlog.rms;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import javax.microedition.rms.RecordComparator;

/**
 * An descending RecordComparator, based on the timestamp.
 * 
 * @author Darius Katz
 */
public class DescendingComparator implements RecordComparator {

	/** Creates a new instance of DescendingComparator */
	public DescendingComparator() {
	}

	/**
	 * The compare() implementation, descending based on the timestamp
	 */
	public int compare(byte[] entry1, byte[] entry2) {
		try {
			ByteArrayInputStream bais1 = new ByteArrayInputStream(entry1);
			DataInputStream is1 = new DataInputStream(bais1);

			ByteArrayInputStream bais2 = new ByteArrayInputStream(entry2);
			DataInputStream is2 = new DataInputStream(bais2);

			// Sort based on the timestamp which is the first long in the
			// data/stream
			long timestamp1 = is1.readLong();
			long timestamp2 = is2.readLong();

			is2.close();
			bais2.close();
			is1.close();
			bais1.close();

			if (timestamp1 > timestamp2) {
				return RecordComparator.PRECEDES;
			} else if (timestamp1 < timestamp2) {
				return RecordComparator.FOLLOWS;
			} else {
				return RecordComparator.EQUIVALENT;
			}

		} catch (IOException e) {
			e.printStackTrace();
			return RecordComparator.EQUIVALENT; // Just return something...
		}
	}

}
