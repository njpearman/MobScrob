/**
 * 
 */
package mobscrob.properties;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import mobscrob.alert.AlertType;
import mobscrob.alert.Alertable;
import mobscrob.logging.Log;
import mobscrob.logging.LogFactory;
import mobscrob.midlet.Callback;
import mobscrob.midlet.MobScrobDisplay;
import mobscrob.scrobbler.MD5Util;

/**
 * @author Neill
 * 
 */
public class PropertiesScreen implements MobScrobDisplay, CommandListener {

	private static final Log log = LogFactory.getLogger(PropertiesScreen.class);

	private final MobScrobProperties props;
	private final Callback returnCB;
	private final Callback saveCB;
	private Form propsForm;
	private TextField usernameField;
	private TextField passwordField;
	private ChoiceGroup exportQueueField;
	private Command saveCmd;
	private Command cancelCmd;
	private Alertable alertable;

	public PropertiesScreen(MobScrobProperties props, Callback returnCB, 
			Callback saveCB, Alertable alertable) {
		this.props = props;
		this.returnCB = returnCB;
		this.saveCB = saveCB;
		this.alertable = alertable;
	}

	public void init() {
		propsForm = new Form("Properties");

		usernameField = new TextField("Username", "", 100, TextField.ANY);
		usernameField.setString(props.getUsername());
		passwordField = new TextField("Password", "", 100, TextField.PASSWORD);
		exportQueueField = new ChoiceGroup("Export queue on exit?", ChoiceGroup.MULTIPLE);
		exportQueueField.append("", null);
		exportQueueField.setSelectedIndex(0, props.scrobbleOffline());
		
		saveCmd = new Command("Save", Command.EXIT, 1);
		cancelCmd = new Command("Cancel", Command.CANCEL, 1);

		propsForm.append(usernameField);
		propsForm.append(passwordField);
		propsForm.append(exportQueueField);
		propsForm.addCommand(saveCmd);
		propsForm.addCommand(cancelCmd);
		propsForm.setCommandListener(this);
	}

	public void open(Display parent) {
		parent.setCurrent(propsForm);
	}

	public void commandAction(Command command, Displayable s) {
		final String methodName = "1";
		if (command == saveCmd) {
			try {
				// save properties
				log.info(methodName, "Got save command");
				String usr = usernameField.getString();
				String pwd = passwordField.getString();
				if ("".equals(usr) || "".equals(pwd)) {
					// alert that details are blank
					alertable.alert(AlertType.ERROR,
							"Username and password cannot be blank");
				} else {
					log.info(methodName, "Got username: " + usr);
					props.setLastFmUserDetails(usr, MD5Util.md5Hash(pwd));
					props.setScrobbleOffline(exportQueueField.isSelected(0));
					props.save();
					saveCB.callback();
				}
			} catch (PropertiesException e) {
				log.fatal(methodName, e.getMessage(), e);
			} catch(Exception e) {
				log.error(methodName, "Error processing command: "
						+ e.getMessage(), e);
			}
		} else if (command == cancelCmd) {
			// cancel and return to main
			log.info(methodName, "Got cancel command");
			usernameField.setString(props.getUsername());
			returnCB.callback();
		}
	}
}
