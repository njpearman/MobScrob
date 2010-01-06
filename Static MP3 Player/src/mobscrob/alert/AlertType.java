/**
 * AlertType.java
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
