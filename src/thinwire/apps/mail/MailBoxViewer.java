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

import java.util.Date;
import java.util.HashMap;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;

import thinwire.ui.Button;
import thinwire.ui.Image;
import thinwire.ui.Panel;
import thinwire.ui.ProgressBar;
import thinwire.ui.Tree;
import thinwire.ui.event.ActionEvent;
import thinwire.ui.event.ActionListener;
import thinwire.ui.event.PropertyChangeEvent;
import thinwire.ui.event.PropertyChangeListener;
import thinwire.ui.style.Color;
import thinwire.ui.style.FX;
import thinwire.util.ArrayGrid;

/**
 * The MailBoxViewer is a Panel that contains a Tree with folders representing
 * the folders in a MailBox. It also contains Buttons for refreshing the
 * contents from the mail server, and composing a new outgoing message.
 * 
 * @author Ted C. Howard
 */
public class MailBoxViewer extends Panel {
    private MailClient mc;
    private MailTabSheet folderView;
    private Tree mailBoxTree;
    private Tree.Item root;
    private HashMap<String, Tree.Item> folderMap;
    private Panel tools;
    private Button checkBtn;
    private Button composeBtn;
    private Image banner;

    private PropertyChangeListener sizeListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent ev) {
            if (ev.getPropertyName().equals(PROPERTY_WIDTH)) {
                int size = ((Panel) ev.getSource()).getInnerWidth();
                banner.setWidth(size);
                mailBoxTree.setWidth(size);
                tools.setWidth(size);
                int x1 = (checkBtn.getWidth() + composeBtn.getWidth() + 10) / 2;
                int x2 = size / 2;
                checkBtn.setX(x2 - x1);
                composeBtn.setX(checkBtn.getX() + checkBtn.getWidth() + 10);
            } else {
                int size = ((Panel) ev.getSource()).getInnerHeight() - (banner.getHeight() + tools.getHeight());
                if (size > 0) {
                    mailBoxTree.setHeight(size);
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
            composeDlg.getDialog().getStyle().getFX().setVisibleChange(FX.Type.SMOOTH);
            composeDlg.getDialog().setVisible(true);
        }
    };

    MailBoxViewer(MailClient mc, MailTabSheet folderView) {
        getStyle().getBackground().setColor(Color.SILVER);
        this.mc = mc;
        this.folderView = folderView;
        folderMap = new HashMap<String, Tree.Item>();
        banner = new Image(MailClient.IMG_PATH + "MailDemoLogo.png");
        banner.setBounds(0, 0, 30, 46);
        getChildren().add(banner);
        tools = new Panel();
        tools.setPosition(0, banner.getHeight() + banner.getY());
        tools.setHeight(30);
        tools.getStyle().getBackground().setColor(getStyle().getBackground().getColor());
        checkBtn = new Button("Check Mail", MailClient.IMG_PATH + "SychronizeListHS.gif");
        checkBtn.setY(4);
        checkBtn.setWidth(90);
        checkBtn.setHeight(22);
        checkBtn.addActionListener(Button.ACTION_CLICK, checkMailListener);
        tools.getChildren().add(checkBtn);
        composeBtn = new Button("Compose", MailClient.IMG_PATH + "NewMessageHS.gif");
        composeBtn.setY(4);
        composeBtn.setWidth(70);
        composeBtn.setHeight(22);
        composeBtn.addActionListener(Button.ACTION_CLICK, composeListener);
        tools.getChildren().add(composeBtn);
        getChildren().add(tools);
        mailBoxTree = new Tree();
        mailBoxTree.setPosition(0, tools.getHeight() + tools.getY());
        mailBoxTree.addActionListener(Tree.ACTION_CLICK, clickListener);
        getChildren().add(mailBoxTree);
        addPropertyChangeListener(new String[] { PROPERTY_WIDTH, PROPERTY_HEIGHT }, sizeListener);
        root = mailBoxTree.getRootItem();
        addFolder("Inbox");
        addFolder("Sent");
        addFolder("Contacts");
    }

    /**
     * Provides a simple interface for adding folders to the MailBoxViewer's
     * Tree. The logic for determining which folders get added occurs in
     * MailClient.java
     * 
     * @param name
     *            The name of the new folder.
     */
    private void addFolder(String name) {
        Tree.Item newFolder = new Tree.Item();
        newFolder.setText(name);
        newFolder.setImage(MailClient.IMG_PATH + "folderclose.gif");
        newFolder.setUserObject(new ArrayGrid());
        folderMap.put(name, newFolder);
        root.getChildren().add(newFolder);
    }

    /**
     * Stores an array of javax.mail.Messages as a thinwire.util.ArrayGrid,
     * organized as they will be displayed in the GridBox in the MailTabSheet.
     * This method can handle plain text, html, multipart/mixed (html and text),
     * or multipart/alternative (text with an attachment). It also adds a
     * welcome message to the to of the Grid.
     * 
     * @param messages
     * @param folderName
     * @throws Exception
     */
    public void populateFolder(Message[] messages, String folderName, ProgressBar pb) throws Exception {
        if (pb != null) pb.setLength(messages.length > 0 ? messages.length : 1);
        ArrayGrid folderGrid = (ArrayGrid) folderMap.get(folderName).getUserObject();
        folderGrid.getRows().clear();
        folderGrid.getRows().add(getWelcomeMessage());
        for (Message m : messages) {
            ArrayGrid.Row newRow = new ArrayGrid.Row();
            newRow.add(m.getFrom()[0]);
            newRow.add(m.getSubject());
            newRow.add(m.getSentDate());
            newRow.add(m);
            folderGrid.getRows().add(newRow);
            if (pb != null && pb.getCurrentIndex() < pb.getLength() - 1) pb.setCurrentIndex(pb.getCurrentIndex() + 1);
        }
        folderMap.get(folderName).setUserObject(folderGrid);
    }
    
    

    public ArrayGrid.Row getWelcomeMessage() {
        ArrayGrid.Row msg = new ArrayGrid.Row();
        msg.add("ThinWire Dev Team <info@thinwire.com>");
        msg.add("Welcome to ThinWire MailClient");
        msg.add(new Date());
        msg.add("From: <span style=\"font-weight: bold;\">ThinWire Dev Team <info@thinwire.com></span><br />To: <span style=\""
            + "font-weight: bold;\">New ThinWire User</span><br />Subject: <span style=\"font-weight: bold;\">"
            + "Welcome to ThinWire MailClient</span><br /><br /><hr /><br />"
            + "<h1>Welcome to the ThinWire Mail Application</h1>"
            + "<p>"
            + "    This application is a working POP3 mail browser.  The email configuration you specified in the properties"
            + "    dialog has been saved to the thinwire_mail.properties file.  The next time you load this app, the "
            + "    parameters you specified will be loaded from there."
            + "</p>");
        return msg;
    }

    public void openFolder(String folderName) {
        mailBoxTree.fireAction("click", folderMap.get(folderName));
    }
    
    public Button getCheckBtn() {
        return checkBtn;
    }
}
