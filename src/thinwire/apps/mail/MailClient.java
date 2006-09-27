/*
 *                         ThinWire(TM) Mail Demo
 *                 Copyright (C) 2006 Custom Credit Systems
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Users wishing to use this demo in proprietary products which are not 
 * themselves to be released under the GNU Public License should contact Custom
 * Credit Systems for a license to do so.
 * 
 *               Custom Credit Systems, Richardson, TX 75081, USA.
 *                          http://www.thinwire.com
 */
package thinwire.apps.mail;

import static thinwire.ui.ActionEventComponent.ACTION_CLICK;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

import thinwire.render.web.WebApplication;
import thinwire.ui.Application;
import thinwire.ui.Frame;
import thinwire.ui.Menu;
import thinwire.ui.MessageBox;
import thinwire.ui.ProgressBar;
import thinwire.ui.TabFolder;
import thinwire.ui.event.ActionEvent;
import thinwire.ui.event.ActionListener;
import thinwire.ui.event.PropertyChangeEvent;
import thinwire.ui.event.PropertyChangeListener;
import thinwire.ui.layout.SplitLayout;
import thinwire.ui.layout.SplitLayout.SplitType;
import thinwire.ui.style.Color;

import com.sun.mail.pop3.POP3Store;

/**
 * ThinWire Mail is a POP3 email client written using the ThinWire framework and
 * the JavaMail API. It is designed to look like a standard desktop email
 * client, with a hierarchy of mailbox folders on the left, and a list of
 * messages on the right with a preview pane at the bottom. ThinWire Mail can be
 * configured to work with any POP3 email account including Gmail, just type in
 * your account settings in the properties dialog that pops up when the app
 * loads.
 * 
 * @author Ted C. Howard
 */
public class MailClient {

    static final String IMG_PATH = "class:///thinwire.apps.mail.MailClient/resources/";
    static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    static final int BORDER_SIZE = 3;
    static final int SCROLL_BAR_WIDTH = 18;

    private Session session;
    private Properties properties;
    private WebApplication app;
    private Frame frame;
    private MailTabSheet folderView;
    private MailBoxViewer mbv;
    private Store store;
    private Folder folder;

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

    MailClient() throws Exception {
        store = null;
        folder = null;
        app = (WebApplication) Application.current();
        frame = app.getFrame();
        frame.getStyle().getBackground().setColor(Color.DIMGRAY);
        frame.setTitle("Thinwire Mail Demo");

        folderView = new MailTabSheet(this);
        mbv = new MailBoxViewer(this, folderView);
        frame.getChildren().add(mbv);

        TabFolder right = new TabFolder();
        right.getStyle().getBackground().setColor(Color.SILVER);
        right.getStyle().getBorder().setColor(right.getStyle().getBackground().getColor());
        right.getChildren().add(folderView);
        frame.getChildren().add(right);
        new SplitLayout(frame, SplitType.VERTICAL, .25);

        buildMenu();
        
        frame.addPropertyChangeListener(Frame.PROPERTY_VISIBLE, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent ev) {
                if (!((Boolean) ev.getNewValue())) {
                    try {
                        closeConnection();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
            }
            }
        });

        properties = new Properties();
        PropertiesDialog propDlg = new PropertiesDialog(properties);
        //propDlg.getDialog().setVisible(true);
        if (propDlg.confirm() == 1) {
            checkMail();
        } else {
            mbv.getCheckBtn().setEnabled(false);
            frame.getMenu().getRootItem().getChildren().get(0).getChildren().get(0).setEnabled(false);
        }
    }

    /**
     * This method makes a connection to the POP3 server and downloads all the
     * messages in the inbox. Depending on the properties specified, it will
     * make a standard connection, or it will connect with SSL (ex: Gmail).
     * 
     * @throws Exception
     */
    public void checkMail() throws Exception {
        mbv.getCheckBtn().setEnabled(true);
        frame.getMenu().getRootItem().getChildren().get(0).getChildren().get(0).setEnabled(true);
        MessageBox pleaseWait = new MessageBox();
        pleaseWait.setTitle("Please Wait");
        pleaseWait.setText("Waiting to Connect to Mail Server.");
        pleaseWait.show();
        Properties props = System.getProperties();
        closeConnection();

        try {
            if (properties.getProperty("connectionType").indexOf("pop3") >= 0) {
                if (properties.getProperty("connectionType").equals("pop3+ssl")) {
                    props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
                    props.setProperty("mail.pop3.socketFactory.fallback", "false");
                    props.setProperty("mail.pop3.port", "995");
                    props.setProperty("mail.pop3.socketFactory.port", "995");
                    URLName url = new URLName("pop3://"
                        + properties.getProperty("username") + ":"
                        + properties.getProperty("password") + "@"
                        + properties.getProperty("server") + ":995");
                    session = Session.getInstance(props, null);
                    store = new POP3Store(session, url);
                    store.connect(properties.getProperty("server"), 
                        properties.getProperty("emailAddress"), 
                        properties.getProperty("password"));
                } else {
                    session = Session.getInstance(props, null);
                    store = session.getStore("pop3");
                    store.connect(properties.getProperty("server"), 
                        properties.getProperty("username"), 
                        properties.getProperty("password"));
                }
                pleaseWait.close();
                folder = store.getFolder("inbox");
                folder.open(Folder.READ_ONLY);
                pleaseWait = new MessageBox();
                pleaseWait.setTitle("Retrieving Mail from Server");
                pleaseWait.setComponent(new ProgressBar());
                pleaseWait.getComponent().setHeight(20);
                pleaseWait.show();
                mbv.populateFolder(folder.getMessages(), "Inbox", (ProgressBar) pleaseWait.getComponent());
            } else {
                throw new Exception("connectionType property missing or invalid");
            }
        } catch (MessagingException e) {
            MessageBox.confirm("Error Retrieving Mail From Server");
            mbv.populateFolder(new Message[] {}, "Inbox", (ProgressBar) pleaseWait.getComponent());
        }

        mbv.openFolder("Inbox");
        pleaseWait.close();
    }

    public Properties getProperties() {
        return properties;
    }
    
    public void closeConnection() throws Exception {
        if (folder != null && folder.isOpen()) folder.close(false);
        if (store != null && store.isConnected()) store.close();
    }
    
    private void buildMenu() {
        frame.setMenu(new Menu());
        frame.getMenu().getStyle().getBackground().setColor(Color.SILVER);
        frame.getMenu().getStyle().getBorder().setColor(Color.SILVER);
        Menu.Item mainMenu = frame.getMenu().getRootItem();
        Menu.Item mnuFile = new Menu.Item("File");

        Menu.Item mnuFileNew = new Menu.Item("New Message");
        mnuFileNew.setUserObject(mbv.composeListener);
        mnuFileNew.setImage(IMG_PATH + "NewMessageHS.gif");
        mnuFile.getChildren().add(mnuFileNew);

        Menu.Item mnuFilePrint = new Menu.Item("Print");
        mnuFilePrint.setUserObject(folderView.printAction);
        mnuFilePrint.setImage(IMG_PATH + "PrintHS.gif");
        mnuFile.getChildren().add(mnuFilePrint);

        Menu.Item mnuFileQuit = new Menu.Item("Exit");
        mnuFileQuit.setUserObject(quitListener);
        mnuFile.getChildren().add(mnuFileQuit);

        Menu.Item mnuTools = new Menu.Item("Tools");

        Menu.Item mnuToolsCheck = new Menu.Item("Check Mail");
        mnuToolsCheck.setUserObject(mbv.checkMailListener);
        mnuToolsCheck.setImage(IMG_PATH + "SychronizeListHS.gif");
        mnuTools.getChildren().add(mnuToolsCheck);

        Menu.Item mnuToolsAcct = new Menu.Item("Account Settings");
        mnuToolsAcct.setUserObject(folderView.acctSettingsAction);
        mnuToolsAcct.setImage(IMG_PATH + "list.png");
        mnuTools.getChildren().add(mnuToolsAcct);

        mainMenu.getChildren().add(mnuFile);
        mainMenu.getChildren().add(mnuTools);
        frame.getMenu().addActionListener(ACTION_CLICK, menuListener);
    }

}
