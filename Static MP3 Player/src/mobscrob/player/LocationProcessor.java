/**
 * LocationProcessor.java
 * @author NJ Pearman
 * @date 7 Oct 2008
 */
package mobscrob.player;

import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

import mobscrob.alert.AlertType;
import mobscrob.alert.Alertable;
import mobscrob.id3.TrackMetadata;
import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;

/**
 * LocationProcessor retrieves the GPS coordinates when processing
 * a TrackMetadata object.
 * @author Neill
 *
 */
public class LocationProcessor extends PlayProcessorImpl {

	private static final Log log = LogFactory.getLogger(LocationProcessor.class);
	
	private static final int WAIT_TIME = 5000;
	private static final int LOCATION_WAIT_TIME = 60;
	
	private final Criteria criteria;
	private final LocationProvider locProvider;
	private final Alertable alert;
	
	public LocationProcessor(Alertable alert) throws PlayProcessorException {
		super(WAIT_TIME);
		
		final String methodName = "1";

		this.alert = alert;
		
		try {
			// set up the Criteria
			criteria = new Criteria();
//			criteria.setPreferredPowerConsumption(Criteria.POWER_USAGE_LOW);
//			criteria.setPreferredResponseTime(60000);
			
			// create a location provider
			locProvider = LocationProvider.getInstance(criteria);
			log.info(methodName, "Created loc provider: "+locProvider.getClass().getName());
		} catch (LocationException e) {
			String msg = "Unable to get provider instance: "+e.getMessage();
			log.info(methodName, msg, e);
			throw new PlayProcessorException(msg);
		} catch(Exception e) {
			String msg = 
				"Unable to create Processor: " + e.getMessage() + 
				", " + e.getClass().getName();
			log.info(methodName, msg, e);
			throw new PlayProcessorException(msg);
		}
	}

	/* (non-Javadoc)
	 * @see mobscrob.player.PlayProcessorImpl#process(mobscrob.id3.TrackMetadata)
	 */
	public void process(TrackMetadata next) {
		final String methodName = "2";

		String msg = "not set";
		try {
			log.info(methodName, "Running loc processor");
			
			// get current location
			if(locProvider.getState() == LocationProvider.OUT_OF_SERVICE) {
				msg = "Loc provider is out of service";
				throw new PlayProcessorException(msg);
			} else if (locProvider.getState() == LocationProvider.TEMPORARILY_UNAVAILABLE) {
				msg = "Loc provider temporarily unavailable";
				throw new PlayProcessorException(msg);
			}
			
			Location loc = locProvider.getLocation(LOCATION_WAIT_TIME);
			
			if(loc.isValid()) {
				log.info(methodName, "Location is valid");
			} else {
				log.info(methodName, "Location is not valid");
			}
			
			QualifiedCoordinates coords = loc.getQualifiedCoordinates();
			
			double lat = coords.getLatitude();
			double lng = coords.getLongitude();
			
			msg = "Processed "+next.getTrackTitle()+" at "+lat+":lat "+lng+":long";
			log.info(methodName, msg);
			
		} catch (LocationException e) {
			msg = "Timed out on location: "+e.getMessage();
			log.error(methodName, msg, e);
		} catch (InterruptedException e) {
			msg = "Interrupted getting location: "+e.getMessage();
			log.error(methodName, msg, e);
		} catch(Throwable t) {
			msg = "Unchecked throwable:"+t.getMessage();
			log.error(methodName, msg, t);
		} finally {
			alert.alert(AlertType.INFO, msg);

			// reset the LocationProvider?
			locProvider.reset();
		}
		
	}

	/* (non-Javadoc)
	 * @see mobscrob.player.PlayProcessor#shutdown()
	 */
	public void shutdown() {
		// nothing to do here yet
	}
}
