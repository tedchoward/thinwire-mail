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

import java.util.Date;
import java.util.HashMap;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;

import thinwire.ui.AlignX;
import thinwire.ui.Button;
import thinwire.ui.Component;
import thinwire.ui.Image;
import thinwire.ui.Label;
import thinwire.ui.Panel;
import thinwire.ui.Tree;
import thinwire.ui.event.ActionEvent;
import thinwire.ui.event.ActionListener;
import thinwire.ui.event.PropertyChangeEvent;
import thinwire.ui.event.PropertyChangeListener;
import thinwire.ui.style.Color;
import thinwire.util.ArrayGrid;

public class MailBoxViewer extends Panel {
	private MailClient mc;
	private MailTabSheet folderView;
	private Tree mailBoxTree;
	private Tree.Item root;
	private HashMap<String, Tree.Item> folderMap;
	private Panel tools;
	private Button checkBtn;
	private Button composeBtn;
    private Label banner;
	
	private PropertyChangeListener sizeListener = new PropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent ev) {
			int size = ((Integer) ev.getNewValue()) - (MailClient.BORDER_SIZE * 2);
			
			if (ev.getPropertyName().equals(Component.PROPERTY_WIDTH)) {
                banner.setWidth(size);
				mailBoxTree.setWidth(size);
				tools.setWidth(size);
				int x1 = (checkBtn.getWidth() + composeBtn.getWidth() + 10) / 2;
				int x2 = size / 2;
				checkBtn.setX(x2 - x1);
				composeBtn.setX(checkBtn.getX() + checkBtn.getWidth() + 10);
			} else {
				if (size > 60) {
					mailBoxTree.setHeight(size - 60);
				} else {
					mailBoxTree.setHeight(0);
				}
			}
		}
	};
	
	private ActionListener clickListener = new ActionListener() {

		public void actionPerformed(ActionEvent ev) {
			folderView.setText(((Tree.Item) ev.getSource()).getText());
			try {
				folderView.populateMessageList((ArrayGrid) ((Tree.Item) ev.getSource()).getUserObject());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
	};
	
	public ActionListener checkMailListener = new ActionListener() {

		public void actionPerformed(ActionEvent ev) {
			try {
				mc.checkMail();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
	};
	
	public ActionListener composeListener = new ActionListener() {

		public void actionPerformed(ActionEvent ev) {
			ComposeDialog composeDlg = new ComposeDialog();
			composeDlg.getDialog().setVisible(true);
		}
		
	};
	
	MailBoxViewer(MailClient mc, MailTabSheet folderView) {
		this.mc = mc;
		this.folderView = folderView;
		folderMap = new HashMap<String, Tree.Item>();
        banner = new Label("ThinWire Mail!");        
        banner.setBounds(0, 5, 30, 20);
        banner.getStyle().getFont().setSize(18);
        banner.getStyle().getFont().setColor(Color.CADETBLUE);
        banner.getStyle().getFont().setBold(true);
        banner.setAlignX(AlignX.CENTER);
        getChildren().add(banner);
		tools = new Panel();
        tools.setPosition(0, banner.getHeight() + banner.getY());
		tools.setHeight(40);                       
		checkBtn = new Button("Check Mail", MailClient.IMG_PATH + "SychronizeListHS.gif");
		checkBtn.setY(10);
		checkBtn.setWidth(90);
		checkBtn.setHeight(22);
		checkBtn.addActionListener(Button.ACTION_CLICK, checkMailListener);
		tools.getChildren().add(checkBtn);
		composeBtn = new Button("Compose", MailClient.IMG_PATH + "NewMessageHS.gif");
		composeBtn.setY(10);
		composeBtn.setWidth(70);
		composeBtn.setHeight(22);
		composeBtn.addActionListener(Button.ACTION_CLICK, composeListener);
		tools.getChildren().add(composeBtn);
		getChildren().add(tools);
		mailBoxTree = new Tree();
		mailBoxTree.setX(0);
		mailBoxTree.setY(60);
		mailBoxTree.addActionListener(Tree.ACTION_CLICK, clickListener);
		getChildren().add(mailBoxTree);
		addPropertyChangeListener(new String[]{Component.PROPERTY_WIDTH, Component.PROPERTY_HEIGHT}, sizeListener);
		root = mailBoxTree.getRootItem();
		addFolder("Inbox");
		addFolder("Sent");
		addFolder("Contacts");
	}
	
	private void addFolder(String name) {
		Tree.Item newFolder = new Tree.Item();
		newFolder.setText(name);
		newFolder.setImage(MailClient.IMG_PATH + "folderclose.gif");
		newFolder.setUserObject(new ArrayGrid());
		folderMap.put(name, newFolder);
		root.getChildren().add(newFolder);
	}
	
	public void populateFolder(Message[] messages, String folderName) throws Exception {
		ArrayGrid folderGrid = (ArrayGrid) folderMap.get(folderName).getUserObject();
        folderGrid.getRows().clear();
		folderGrid.getRows().add(getWelcomeMessage());
		for (Message m : messages) {
			ArrayGrid.Row newRow = new ArrayGrid.Row();
			newRow.add(m.getFrom()[0]);
			newRow.add(m.getSubject());
			newRow.add(m.getSentDate());
			StringBuilder sb = new StringBuilder();
			Address[] recipients = m.getRecipients(Message.RecipientType.TO);
			for (Address a : recipients) {
				sb.append(a.toString()).append("; ");
			}
			String header = "From: <span style=\"font-weight: bold;\">" + m.getFrom()[0] + "</span><br />To: <span style=\"" +
				"font-weight: bold;\">" + sb.toString() + "</span><br />Subject: <span style=\"font-weight: bold;\">" + 
				m.getSubject() + "</span><br /><br /><hr /><br />";
			if (m.getContent().getClass().equals(MimeMultipart.class)) {
				MimeMultipart mp = (MimeMultipart) m.getContent();
				String content = new String();
				if (mp.getContentType().indexOf("multipart/alternative") >= 0) {
					for (int i = 0, cnt = mp.getCount(); i < cnt; i++) {
						Part part = mp.getBodyPart(i);
						content = header + part.getContent().toString();
					}
				} else if (mp.getContentType().indexOf("multipart/mixed") >= 0) {
					for (int i = 0, cnt = mp.getCount(); i < cnt; i++) {
						Part part = mp.getBodyPart(i);
						if (part.getContentType().indexOf("text") >= 0) {
							content = header + part.getContent().toString();
						}
					}
				}
				newRow.add(content);
			} else {
				newRow.add(header + m.getContent());
			}
			folderGrid.getRows().add(newRow);
		}
		folderMap.get(folderName).setUserObject(folderGrid);
	}
	
	public ArrayGrid.Row getWelcomeMessage() {
		ArrayGrid.Row msg = new ArrayGrid.Row();
		msg.add("ThinWire Dev Team <info@thinwire.com>");
		msg.add("Welcome to ThinWire MailClient");
		msg.add(new Date());
		msg.add("From: <span style=\"font-weight: bold;\">ThinWire Dev Team <info@thinwire.com></span><br />To: <span style=\"" +
				"font-weight: bold;\">New ThinWire User</span><br />Subject: <span style=\"font-weight: bold;\">" + 
				"Welcome to ThinWire MailClient</span><br /><br /><hr /><br />" + 
		"<h1>Welcome to the ThinWire Mail Application</h1>" + 
        "<p>" + 
        "    This application is a working POP3 mail browser.  The email configuration you specified in the properties" +
        "    dialog has been saved to the thinwire_mail.properties file.  The next time you load this app, the " +
        "    parameters you specified will be loaded from there." + 
        "</p>");
		return msg;
	}
	
	public void openFolder(String folderName) {
		mailBoxTree.fireAction("click", folderMap.get(folderName));
	}
}
