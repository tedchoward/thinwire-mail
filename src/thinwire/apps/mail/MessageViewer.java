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

import java.io.File;
import java.io.FileOutputStream;

import thinwire.ui.Component;
import thinwire.ui.Panel;
import thinwire.ui.WebBrowser;
import thinwire.ui.event.PropertyChangeEvent;
import thinwire.ui.event.PropertyChangeListener;
import thinwire.ui.style.Color;

public class MessageViewer extends Panel {
	private WebBrowser messageBrowser;
	private PropertyChangeListener sizeListener = new PropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent ev) {
			int size = ((Integer) ev.getNewValue()) - (MailClient.BORDER_SIZE * 2);
			
			if (ev.getPropertyName().equals(WebBrowser.PROPERTY_WIDTH)) {
				messageBrowser.setWidth(size);
			} else {
				messageBrowser.setHeight(size);
			}
		}
	};
	
	MessageViewer() {
		messageBrowser = new WebBrowser();
		messageBrowser.getStyle().getBackground().setColor(Color.WHITE);
		getChildren().add(messageBrowser);
		addPropertyChangeListener(new String[]{Component.PROPERTY_WIDTH, Component.PROPERTY_HEIGHT}, sizeListener);
	}
	
	public void setMessage(String msg) throws Exception {
        String currentFile = messageBrowser.getLocation();
        if (currentFile.length() > 0) new File(currentFile).delete();
        File tmpFile = File.createTempFile("mailMsg" + System.currentTimeMillis(), ".html");
        tmpFile.deleteOnExit();
		FileOutputStream fos = new FileOutputStream(tmpFile);
		fos.write(msg.getBytes());
		fos.flush();
		fos.close();
		messageBrowser.setLocation(tmpFile.getAbsolutePath());
	}
	
	public String getMessageLocation() {
		return messageBrowser.getLocation();
	}
	
	public void clear() {
		messageBrowser.setLocation("about:blank");
	}
}
