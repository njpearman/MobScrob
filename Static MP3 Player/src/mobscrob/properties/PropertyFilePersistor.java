/**
 * PropertyFileReader.java
 * @author NJ Pearman
 * @date 3 Oct 2008
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;
import mobscrob.util.StreamUtil;

/**
 * @author Neill
 *
 */
public class PropertyFilePersistor implements PropertyPersistor {
	
	private static final Log log = LogFactory.getLogger(PropertyFilePersistor.class);

	private static final String PROPERTIES_FILENAME = "file:///e:/mobscrob.properties";

	protected static final char CHAR_EQUALS = '=';
	protected static final char CHAR_CARRIAGE_RETURN = '\r';

	private boolean readKey;
	private StringBuffer key, value;
	private Vector keyValues;
	private boolean readingComment;
	private boolean deleteFileOnLoad = true;

	public PropertyFilePersistor() {
		key = new StringBuffer();
		value = new StringBuffer();
		keyValues = new Vector();
		readingComment = false;
		readKey = false;
	}

	private FileConnection openPropertiesFile() throws IOException {
		FileConnection fc = (FileConnection) Connector.open(PROPERTIES_FILENAME);

		if (!fc.exists()) {
			// create Properties
			fc.create();
		}
		return fc;
	}

	/* (non-Javadoc)
	 * @see mobscrob.properties.PropertyReader#load(mobscrob.properties.MobScrobProperties)
	 */
	public boolean load(MobScrobProperties properties) {
		final String methodName = "1";
		FileConnection fc = null;
		InputStream is = null;
		try {
			fc = openPropertiesFile();

			is = fc.openInputStream();
			Vector lines = readKeyValues(is);

			Enumeration e = lines.elements();
			while (e.hasMoreElements()) {
				KeyValuePair kv = (KeyValuePair) e.nextElement();

				if (KEY_USERNAME.equals(kv.key)) {
					properties.setUsername(kv.value);
					log.info(methodName, "Loaded username: " + kv.value);
				} else if (KEY_HASHED_PASSWORD.equals(kv.key)) {
					properties.setHashedPassword(kv.value);
					log.info(methodName, "Loaded hashed password: " + kv.value);
				} else if (KEY_SCROBBLE_OFFLINE.equals(kv.value)) {
					properties.setScrobbleOffline("true".equals(kv.value.toLowerCase()));
					log.info(methodName, "Loaded export queue: " + kv.value);
				} else {
					log.warn(methodName, "Unexpected property: " + kv.key
							+ ": " + kv.value);
				}
			}
			
			if(deleteFileOnLoad) {
				log.info(methodName, "Deleting properties file");
				fc.delete();
			}
			return true;
		} catch (IOException e) {
			log.error(methodName, "Unable to load properties: "
					+ e.getMessage(), e);
			return false;
		} finally {
			StreamUtil.closeInputStream(is);
			if (fc != null) {
				try { fc.close(); } 
				catch (Exception e) { }
			}
		}
	}

	/* (non-Javadoc)
	 * @see mobscrob.properties.PropertyReader#save(mobscrob.properties.MobScrobProperties)
	 */
	public void save(MobScrobProperties properties) {
		final String methodName = "2";
		FileConnection fc = null;
		OutputStream os = null;

		try {
			log.info(methodName, "Saving properties...");

			fc = openPropertiesFile();
			int bytesWritten = 0;

			// write output to stream
			os = fc.openOutputStream();

			// write username
			bytesWritten += writePropertyToStream(os, KEY_USERNAME.getBytes(),
					properties.getUsername().getBytes());

			// write password
			bytesWritten += writePropertyToStream(os, KEY_HASHED_PASSWORD.getBytes(), 
					properties.getHashedPassword().getBytes());

			bytesWritten += writePropertyToStream(os, KEY_SCROBBLE_OFFLINE.getBytes(), 
					String.valueOf(properties.scrobbleOffline()).getBytes());

			// truncate to length of written bytes
			fc.truncate(bytesWritten);

			log.info(methodName, "Saved properties " + this.toString());
		} catch (IOException e) {
			log.error(methodName, "Unable to write properties file: " + 
					e.getMessage(), e);
		} finally {
			if (os != null) {
				try {
					os.flush();
					os.close();
				} catch (Exception e) {
					log.error(methodName, "Unable to close output stream: "
							+ e.getMessage(), e);
				}
			}

			if (fc != null) {
				try { fc.close(); } 
				catch (Exception e) {}
			}
		}
	}

	/**
	 * Writes the given property to the specified output stream. Returns the
	 * total number of bytes written to the output stream by this call
	 * 
	 * @param os
	 * @param key
	 * @param value
	 * @return
	 * @throws IOException
	 */
	protected int writePropertyToStream(OutputStream os, byte[] key,
			byte[] value) throws IOException {
		int totalBytes = key.length + value.length + 2;
		os.write(key);
		os.write(CHAR_EQUALS);
		os.write(value);
		os.write(CHAR_CARRIAGE_RETURN);
		return totalBytes;
	}

	protected Vector readKeyValues(InputStream is) throws IOException {
		final String methodName = "3";
		int next, big, little;
		KeyValuePair kv;
		PropertyFilePersistor reader = new PropertyFilePersistor();

		// read first two bytes to see if 16bit
		int first = is.read();
		int second = is.read();

		if (first == 0xFE && second == 0xFF) {
			// big endian 16 bit
			log.info(methodName, "Reading 16 bit big endian");
			while (((big = is.read()) > -1) && ((little = is.read()) > -1)) {
				next = 0;
				next |= big << 8;
				next |= little;

				interpretByte(next, reader);
			}
		} else if (first == 0xFF && second == 0xFE) {
			// little endian 16 bit
			log.info(methodName, "Reading 16 bit little endian");
			while (((big = is.read()) > -1) && ((little = is.read()) > -1)) {
				next = 0;
				next |= big;
				next |= little << 8;

				interpretByte(next, reader);
			}
		} else {
			log.info(methodName, "Reading 8 bit");

			// first interpret the first two bytes
			interpretByte(first, reader);
			interpretByte(second, reader);

			while ((next = is.read()) > -1) {
				// read the rest of the stream
				interpretByte(next, reader);
			}
		}

		if (reader.key.length() > 0) {
			kv = new KeyValuePair();
			kv.key = reader.key.toString();
			kv.value = reader.value.toString();
			reader.keyValues.addElement(kv);
		}

		return reader.keyValues;
	}

	private void interpretByte(int nextByte, PropertyFilePersistor reader) {
		if (nextByte == '\r' || nextByte == '\n') {
			reader.readingComment = false;

			// save key-value pair if key not empty
			if (reader.key.length() > 0) {
				KeyValuePair kv = new KeyValuePair();
				kv.key = reader.key.toString();
				kv.value = reader.value.toString();
				reader.keyValues.addElement(kv);
			}
			reader.key = new StringBuffer();
			reader.value = new StringBuffer();
			reader.readKey = false;
		} else if ((reader.readingComment)) {
			// skip
		} else if (nextByte == '#') {
			// save and clear key value keys
			reader.readingComment = true;
		} else if (nextByte == '=') {
			// finished reading this key
			reader.readKey = true;
		} else {
			/**
			 * @TODO restrict characters to a-zA-Z0-9 plus some special chars as
			 *       defined by last.fm username restrictions, plus any others
			 *       required
			 */
			// append to key / value
			if (reader.readKey) {
				reader.value.append((char) nextByte);
			} else {
				reader.key.append((char) nextByte);
			}
		}
	}
	
	protected class KeyValuePair {
		protected String key;
		protected String value;
	}
}
