/**
 * TrackMetadataRMSPersistor.java
 * @author NJ Pearman
 * @date 4 Oct 2008
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
package mobscrob.player;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import mobscrob.id3.TrackMetadata;
import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;
import mobscrob.util.StreamUtil;
import mobscrob.util.microedition.RecordStoreUtil;

/**
 * @author Neill
 *
 */
public class TrackMetadataRMSPersistor implements TrackMetadataPersistor {

	private static final Log log = LogFactory.getLogger(TrackMetadataRMSPersistor.class);
	
	public static final String SCROBBLE_QUEUE_RECORD_STORE_NAME = "mobscrob.scrobble.queue";
	
	private final String recordStoreName;
	
	public TrackMetadataRMSPersistor(String recordStoreName) {
		this.recordStoreName = recordStoreName;
	}
	
	public void load(final PlayProcessor processor) {
		final String methodName = "1";
		
		try {
			final RecordStore rs = RecordStore.openRecordStore(recordStoreName, true);

			try {
				
				RecordStoreUtil.processAllRecords(rs, 
					new RecordStoreUtil.RecordProcessor() {
						public void process(int recordIndex, byte[] bytes) throws IOException, RecordStoreException {
							processor.queueTrack(getTrackFromByteArray(bytes));
							rs.deleteRecord(recordIndex);
						}
					});
				
				log.info(methodName, "Loaded tracks: "+processor.getQueueSnapshot().length);
			} catch (RecordStoreFullException e) {
				log.error(methodName, "No room in record store: "+e.getMessage(), e);
			} catch (RecordStoreNotFoundException e) {
				log.error(methodName, "Record store doesn't exist: "+e.getMessage(), e);
			} catch (RecordStoreException e) {
				log.error(methodName, "Problem with record store: "+e.getMessage(), e);
			} catch (IOException e) {
				log.error(methodName, "Error reading from stream: "+e.getMessage(), e);
			} finally {
				RecordStoreUtil.closeRecordStore(rs);
			}
		} catch (RecordStoreException e) {
			log.error(methodName, "Unable to get scrobble queue from RMS: "+e.getMessage(), e);
		}
		

	}

	public boolean save(PlayProcessor processor) {
		final String methodName = "2";
		boolean success = true;
		
		RecordStore rs = null;
		
		try {
			rs = RecordStore.openRecordStore(this.recordStoreName, true);
			
			TrackMetadata[] tracks = processor.getQueueSnapshot();
			byte[] trackBytes;
			for(int i=0; i<tracks.length; i++) {
				trackBytes = getTrackAsByteArray(tracks[i]);
				rs.addRecord(trackBytes, 0, trackBytes.length);
			}
			
			log.info(methodName, "Saved tracks, "+rs.getNumRecords());
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
	
	private byte[] getTrackAsByteArray(TrackMetadata track) throws IOException {
		final String methodName = "3";
		ByteArrayOutputStream baos;
		DataOutputStream out = null;
		
		try {
			baos = new ByteArrayOutputStream();
			out = new DataOutputStream(baos);
			
			out.writeUTF(track.getAlbumTitle());
			out.writeUTF(track.getArtist());
			out.writeLong(track.getCurrentPosition());
			out.writeUTF(track.getFileLocation());
			out.writeUTF(track.getMusicBrainzID());
			out.writeLong(track.getStartTimestamp());
			out.writeInt(track.getSubmissionAttempts());
			out.writeLong(track.getTrackLength());
			out.writeUTF(track.getTrackNumber());
			out.writeUTF(track.getTrackTitle());
			
			log.info(methodName, "Serialized track "+track.getTrackTitle());
			return baos.toByteArray();
		} finally {
			StreamUtil.closeOutputStream(out);
		}
	}
	
	private TrackMetadata getTrackFromByteArray(byte[] bytes) throws IOException {
		ByteArrayInputStream bais;
		DataInputStream in = null;
		
		try {
			bais = new ByteArrayInputStream(bytes);
			in = new DataInputStream(bais);
			
			TrackMetadata track = new TrackMetadata();
			
			track.setAlbumTitle(in.readUTF());
			track.setArtist(in.readUTF());
			track.setCurrentPosition(in.readLong());
			track.setFileLocation(in.readUTF());
			track.setMusicBrainzID(in.readUTF());
			track.setStartTimestamp(in.readLong());
			track.setSubmissionAttempts(in.readInt());
			track.setTrackLength(in.readLong());
			track.setTrackNumber(in.readUTF());
			track.setTrackTitle(in.readUTF());
			
			return track;
		} finally {
			StreamUtil.closeInputStream(in);
		}
	}
}
