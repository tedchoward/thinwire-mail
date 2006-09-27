/*
                         ThinWire(TM) Mail Demo
              Copyright (C) 2003-2006 Custom Credit Systems
   
 This program is free software; you can redistribute it and/or modify it under
 the terms of the GNU General Public License as published by the Free Software
 Foundation; either version 2 of the License, or (at your option) any later
 version.
   
 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
   
 You should have received a copy of the GNU General Public License along with
 this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 Place, Suite 330, Boston, MA 02111-1307 USA
  
 Users wishing to use this library in proprietary products which are not 
 themselves to be released under the GNU Public License should contact Custom
 Credit Systems for a license to do so.
   
               Custom Credit Systems, Richardson, TX 75081, USA.
                          http://www.thinwire.com
 */
package thinwire.apps.mail;

import java.util.HashMap;

import thinwire.ui.Button;
import thinwire.ui.Component;
import thinwire.ui.Panel;
import thinwire.ui.event.PropertyChangeEvent;
import thinwire.ui.event.PropertyChangeListener;

public class ToolBar extends Panel {
	private HashMap<String, Button> buttonMap;
	private int nextX;
	private Component rightComponent;
	
	private PropertyChangeListener sizeListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent ev) {
			int size = ((Integer) ev.getNewValue()) - (MailClient.BORDER_SIZE * 2);
			
			if (ev.getPropertyName().equals(Panel.PROPERTY_WIDTH)) {
				if (getChildren().contains(rightComponent)) rightComponent.setX(size - rightComponent.getWidth());
			}
		}
	};

	ToolBar() {
		buttonMap = new HashMap<String, Button>();
		nextX = 5;
		setHeight(30);
		addPropertyChangeListener(new String[] { Component.PROPERTY_WIDTH, Component.PROPERTY_HEIGHT }, sizeListener);
	}
	
	public void addButton(String text) {
		addButton(text, null);
	}
	
	public void addButton(String text, String image) {
		Button newButton = new Button();
		newButton.setText(text);
		if (image != null) newButton.setImage(image);
		newButton.setBounds(nextX, 3, text.length() * 7 + 20, 25);
		nextX = newButton.getX() + newButton.getWidth() + 5;
		getChildren().add(newButton);
		buttonMap.put(text, newButton);
	}
	
	public Button getButton(String text) {
		return buttonMap.get(text);
	}
	
	public void setRightComponent(Component rightComponent) {
		if (getChildren().contains(this.rightComponent)) getChildren().remove(this.rightComponent);
		this.rightComponent = rightComponent;
		getChildren().add(this.rightComponent);
	}
}
