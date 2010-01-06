/**
 * 
 */
package mobscrob.alert;

/**
 * @author Neill
 * 
 */
public class AlertType {

	public static final AlertType ERROR = new AlertType("Error",
			javax.microedition.lcdui.AlertType.ERROR);
	public static final AlertType INFO = new AlertType("Info",
			javax.microedition.lcdui.AlertType.INFO);

	private final javax.microedition.lcdui.AlertType uiType;
	private final String type;

	private AlertType(String type, javax.microedition.lcdui.AlertType uiType) {
		this.type = type;
		this.uiType = uiType;
	}

	public String getType() {
		return type;
	}

	public javax.microedition.lcdui.AlertType getUIType() {
		return uiType;
	}
}
