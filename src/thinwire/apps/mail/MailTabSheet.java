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

import static thinwire.ui.ActionEventComponent.ACTION_CLICK;

import java.util.Properties;

import thinwire.ui.Component;
import thinwire.ui.Hyperlink;
import thinwire.ui.MessageBox;
import thinwire.ui.Panel;
import thinwire.ui.TabSheet;
import thinwire.ui.event.ActionEvent;
import thinwire.ui.event.ActionListener;
import thinwire.ui.event.PropertyChangeEvent;
import thinwire.ui.event.PropertyChangeListener;
import thinwire.ui.layout.SplitLayout;
import thinwire.ui.layout.SplitLayout.SplitType;
import thinwire.util.Grid;

public class MailTabSheet extends TabSheet {
	private ToolBar toolBar;
	private Panel contentPanel;
	private MessageViewer mv;
	private MessageList ml;
	private MailClient mc;
	
	private PropertyChangeListener sizeListener = new PropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent ev) {
			int size = ((Integer) ev.getNewValue()) - (MailClient.BORDER_SIZE * 2);
			if (ev.getPropertyName().equals(Panel.PROPERTY_WIDTH)) {
				contentPanel.setWidth(size);
				toolBar.setWidth(size);
			} else if (size > 30) {
				contentPanel.setHeight(size - 30);
			} else {
				contentPanel.setHeight(0);
			}
		}
		
	};
    
    public ActionListener printAction = new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
            Hyperlink h = new Hyperlink();
            h.setText("Click Here to Open the Message in a New Window");
            h.setWidth(225);
            h.setHeight(20);
            h.setLocation(mv.getMessageLocation());
            MessageBox.confirm(null, "Print the Email", h, "Done");
        }
    };
    
    public ActionListener acctSettingsAction = new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
            Properties properties = mc.getProperties();
            PropertiesDialog propDlg = new PropertiesDialog(properties);
            propDlg.getDialog().setVisible(true);
            try {
                mc.checkMail();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
	
	MailTabSheet(MailClient mcl) {
		mv = new MessageViewer();
		ml = new MessageList(mv);
		mc = mcl;
		toolBar = new ToolBar();
		toolBar.setX(0);
		toolBar.setY(0);
		toolBar.setHeight(30);
		toolBar.addButton("Print", MailClient.IMG_PATH + "PrintHS.gif");
		toolBar.getButton("Print").addActionListener(ACTION_CLICK, printAction);
		toolBar.addButton("Delete", MailClient.IMG_PATH + "DeleteHS.gif");
		toolBar.getButton("Delete").addActionListener(ACTION_CLICK, new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				ml.deleteMessage();
			}
		});
		toolBar.addButton("Account Settings");
		toolBar.getButton("Account Settings").addActionListener(ACTION_CLICK, acctSettingsAction);
		toolBar.setRightComponent(new SearchField(ml.getMessageGrid(), ml.getAltGrid()));

		getChildren().add(toolBar);
		contentPanel = new Panel();
		contentPanel.setX(0);
		contentPanel.setY(30);
		if (getWidth() > 0) this.contentPanel.setWidth(getWidth());
		if (getHeight() > 30) this.contentPanel.setHeight(getHeight() - 30);
		
		contentPanel.getChildren().add(ml);
		contentPanel.getChildren().add(mv);
		new SplitLayout(contentPanel, SplitType.HORIZONTAL, .50);
		getChildren().add(contentPanel);
		addPropertyChangeListener(new String[]{Component.PROPERTY_WIDTH, Component.PROPERTY_HEIGHT}, sizeListener);
	}
	
	public void populateMessageList(Grid folderGrid) throws Exception {
		ml.populate(folderGrid);
	}
}
