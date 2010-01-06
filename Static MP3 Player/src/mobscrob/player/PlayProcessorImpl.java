/**
 * PlayProcessorImpl.java
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

import java.util.Enumeration;
import java.util.Vector;

import mobscrob.id3.TrackMetadata;
import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;

/**
 * @author Neill
 * 
 */
public abstract class PlayProcessorImpl implements PlayProcessor, Runnable {
	private static final Log log = LogFactory.getLogger(PlayProcessorImpl.class);

	private final int waitTime;

	private Vector queue;
	private boolean isRunning;
	protected Thread runThread;

	public PlayProcessorImpl(int waitTime) {
		this.waitTime = waitTime;
		isRunning = false;
		queue = new Vector();
	}

	/**
	 * Starts a new thread for this PlayProcessorImpl instance, provided
	 * that the instance is not already running.
	 */
	public void start() {
		if(!isRunning) {
			runThread = new Thread(this);
			runThread.start();
		}
	}

	public void queueTrack(TrackMetadata track) {
		final String methodName = "1";
		if (track != null) {
			synchronized (queue) {
				queue.addElement(track);
			}
		} else {
			log.warn(methodName, "Can't add a null track to queue");
		}
	}

	public void queueAtStart(TrackMetadata track) {
		final String methodName = "2";
		if (track != null) {
			if (queue.size() > 0) {
				synchronized (queue) {
					queue.insertElementAt(track, 0);
				}
			} else {
				synchronized (queue) {
					queue.addElement(track);
				}
			}
		} else {
			log.warn(methodName, "Can't add a null track to start of queue");
		}
	}

	/**
	 * Gets a snapshot of the queue being used by this processor, as an array
	 * of TrackMetadata objects.
	 * 
	 * @return
	 */
	public TrackMetadata[] getQueueSnapshot() {
		synchronized (queue) {
			TrackMetadata[] tracks = new TrackMetadata[queue.size()];

			Enumeration e = queue.elements();
			int curr = 0;
			while (e.hasMoreElements()) {
				tracks[curr++] = (TrackMetadata)e.nextElement();
			}
			return tracks;
		}
	}

	public abstract void process(TrackMetadata next);

	public void run() {
		final String methodName = "3";
		isRunning = true;

		log.info(methodName, "Starting " + this.getClass().getName());

		while (isRunning) {
			try {
				// wait for item in queue
				while (queue.isEmpty() && isRunning) {
					Thread.sleep(waitTime);
				}
				if (!isRunning) {
					// break if the processor has been stopped
					break;
				}

				TrackMetadata next;
				synchronized (queue) {
					// take item off top of queue
					next = (TrackMetadata) queue.elementAt(0);
					queue.removeElementAt(0);
					log.info(methodName, "Removed "+next.getTrackTitle()+" from queue");
				}

				process(next);

				log.info(methodName, "Track length " + next.getTrackLength()
						+ ", played for " + next.getCurrentPosition());

			} catch (InterruptedException ex) {
				log.error(methodName, "PostPlayTagReader thread was interrupted"
								+ ex.getMessage(), ex);
			}
		}
	}
	
	protected void stopProcessing() {
		this.isRunning = false;
	}
}
