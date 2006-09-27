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

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

import thinwire.render.web.WebApplication;
import thinwire.ui.Application;
import thinwire.ui.Frame;
import thinwire.ui.Menu;
import thinwire.ui.MessageBox;
import thinwire.ui.TabFolder;
import thinwire.ui.event.ActionEvent;
import thinwire.ui.event.ActionListener;
import thinwire.ui.layout.SplitLayout;
import thinwire.ui.layout.SplitLayout.SplitType;
import thinwire.ui.style.Color;

import com.sun.mail.pop3.POP3Store;

public class MailClient {
	
	static final String IMG_PATH = "class:///thinwire.apps.mail.MailClient/resources/";
	static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	
	private Session session;
	private Properties properties;
	
	static final int BORDER_SIZE = 3;
	static final int SCROLL_BAR_WIDTH = 18;
	private WebApplication app;
	private Frame frame;
	private MailTabSheet folderView;
	private MailBoxViewer mbv;
    
    private ActionListener menuListener = new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
            Menu.Item source = (Menu.Item) ev.getSource();
            ((ActionListener) source.getUserObject()).actionPerformed(new ActionEvent(source, null));
        }
    };
    
    private ActionListener quitListener = new ActionListener() {

        public void actionPerformed(ActionEvent ev) {
            frame.setVisible(false);
        }
        
    };
	
	public static void main(String[] args) throws Exception {
		new MailClient();
	}
	
	MailClient() throws Exception {
		app = (WebApplication) Application.current();
		frame = app.getFrame();
		frame.getStyle().getBackground().setColor(Color.THREEDFACE);
		frame.setTitle("Thinwire Mail Client");
        
		folderView = new MailTabSheet(this);
		mbv = new MailBoxViewer(this, folderView);
		frame.getChildren().add(mbv);
		
		TabFolder right = new TabFolder();
		right.getChildren().add(folderView);
		frame.getChildren().add(right);
		new SplitLayout(frame, SplitType.VERTICAL, .25).setDividerSize(4);
        
        frame.setMenu(new Menu());
        Menu.Item mainMenu = frame.getMenu().getRootItem();
        Menu.Item mnuFile = new Menu.Item("File");
        
        Menu.Item mnuFileNew = new Menu.Item("New Message");
        mnuFileNew.setUserObject(mbv.composeListener);
        //mnuFileNew.setImage(IMG_PATH + "NewMessageHS.gif");
        mnuFile.getChildren().add(mnuFileNew);
        
        Menu.Item mnuFilePrint = new Menu.Item("Print");
        mnuFilePrint.setUserObject(folderView.printAction);
        //mnuFilePrint.setImage(IMG_PATH + "PrintHS.gif");
        mnuFile.getChildren().add(mnuFilePrint);
        
        Menu.Item mnuFileQuit = new Menu.Item("Exit");
        mnuFileQuit.setUserObject(quitListener);
        mnuFile.getChildren().add(mnuFileQuit);
        
        Menu.Item mnuTools = new Menu.Item("Tools");
        
        Menu.Item mnuToolsCheck = new Menu.Item("Check Mail");
        mnuToolsCheck.setUserObject(mbv.checkMailListener);
        //mnuToolsCheck.setImage(IMG_PATH + "SynchronizeListHS.gif");
        mnuTools.getChildren().add(mnuToolsCheck);
        
        Menu.Item mnuToolsAcct = new Menu.Item("Account Settings");
        mnuToolsAcct.setUserObject(folderView.acctSettingsAction);
        mnuTools.getChildren().add(mnuToolsAcct);
        
        mainMenu.getChildren().add(mnuFile);
        mainMenu.getChildren().add(mnuTools);
        frame.getMenu().addActionListener(ACTION_CLICK, menuListener);
		
		properties = new Properties();
        File file = app.getRelativeFile("thinwire_mail.properties");
        if (!file.exists()) file.createNewFile();
		properties.load(new FileInputStream(file));
		PropertiesDialog propDlg = new PropertiesDialog(properties);
		propDlg.getDialog().setVisible(true);
		checkMail();
	}
	
	public void checkMail() throws Exception {
		MessageBox pleaseWait = new MessageBox();
		pleaseWait.setText("Retrieving Mail from Server");
		pleaseWait.setTitle("Please Wait");
		pleaseWait.show();
		Properties props = System.getProperties();
		Store store;
		
		if (properties.getProperty("connectionType").indexOf("pop3") >= 0) {
			if (properties.getProperty("connectionType").equals("pop3+ssl")) {
				props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
				props.setProperty("mail.pop3.socketFactory.fallback", "false");
				props.setProperty("mail.pop3.port", "995");
				props.setProperty("mail.pop3.socketFactory.port", "995");
				URLName url = new URLName("pop3://" + properties.getProperty("username") + ":" + properties.getProperty("password") + 
						"@" + properties.getProperty("server") + ":995");
				session = Session.getInstance(props, null);
				store = new POP3Store(session, url);
				store.connect(properties.getProperty("server"), properties.getProperty("emailAddress"), properties.getProperty("password"));
			} else {
				session = Session.getInstance(props, null);
				store = session.getStore("pop3");
				store.connect(properties.getProperty("server"), properties.getProperty("username"), properties.getProperty("password"));
			}
			
			Folder folder = store.getFolder("inbox");
			folder.open(Folder.READ_ONLY);
			mbv.populateFolder(folder.getMessages(), "Inbox");
			folder.close(false);
			store.close();
		} else {
			throw new Exception("connectionType property missing or invalid");
		}
        
        mbv.openFolder("Inbox");
		pleaseWait.close();
	}
	
	public Properties getProperties() {
		return properties;
	}

}
