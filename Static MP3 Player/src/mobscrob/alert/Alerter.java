/**
 * 
 */
package mobscrob.alert;

/**
 * @author Neill
 * 
 */
public interface Alerter {
	void setAlertable(Alertable alertable);

	void alert(AlertType type, String msg);
}
