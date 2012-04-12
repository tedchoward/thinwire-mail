/*
                             ThinWire(R) Mail Demo
                 Copyright (C) 2006-2007 Custom Credit Systems

  This library is free software; you can redistribute it and/or modify it under
  the terms of the GNU Lesser General Public License as published by the Free
  Software Foundation; either version 2.1 of the License, or (at your option) any
  later version.

  This library is distributed in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License along
  with this library; if not, write to the Free Software Foundation, Inc., 59
  Temple Place, Suite 330, Boston, MA 02111-1307 USA

  Users interested in finding out more about the ThinWire framework should visit
  the ThinWire framework website at http://www.thinwire.com. For those interested
  in discussing the details of how this demo was built, you can contact the 
  developer via email at "Ted C. Howard" <tedchoward at gmail dot com>.
*/

package thinwire.apps.mail;

import java.util.Date;
import java.util.HashMap;

import javax.mail.Message;

import thinwire.ui.Button;
import thinwire.ui.Label;
import thinwire.ui.Panel;
import thinwire.ui.ProgressBar;
import thinwire.ui.Tree;
import thinwire.ui.event.ActionEvent;
import thinwire.ui.event.ActionListener;
import thinwire.ui.layout.TableLayout;
import thinwire.ui.style.Background;
import thinwire.ui.style.Color;
import thinwire.ui.style.Effect;
import thinwire.ui.style.FX;
import thinwire.util.ArrayGrid;

/**
 * The MailBoxViewer is a Panel that contains a Tree with folders representing
 * the folders in a MailBox. It also contains Buttons for refreshing the
 * contents from the mail server, and composing a new outgoing message.
 * 
 * @author Ted C. Howard
 */
class MailBoxViewer extends Panel {
    private MailClient mc;
    private MailTabSheet folderView;
    private Tree mailBoxTree;
    private Tree.Item root;
    private HashMap<String, Tree.Item> folderMap;
    private Button checkBtn;
    private Button composeBtn;
    private Label banner;

    ActionListener checkMailListener = new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
            try {
                mc.checkMail();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    ActionListener composeListener = new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
            ComposeDialog composeDlg = new ComposeDialog();
            composeDlg.getDialog().getStyle().getFX().setVisibleChange(Effect.Motion.SMOOTH);
            composeDlg.getDialog().setVisible(true);
        }
    };

    MailBoxViewer(MailClient mc, MailTabSheet folderView) {
        getStyle().getBackground().setColor(Color.SILVER);
        this.mc = mc;
        this.folderView = folderView;
        folderMap = new HashMap<String, Tree.Item>();

        setLayout(new TableLayout(new double[][] {{0, 90, 0, 70, 0}, {46, 4, 22, 4, 0}}));
        
        banner = new Label();
        banner.getStyle().getBackground().setImage(MailClient.IMG_PATH + "MailDemoLogo.png");
        banner.getStyle().getBackground().setPosition(Background.Position.CENTER);
        banner.setLimit("0, 0, 5, 1");
        getChildren().add(banner);
        
        checkBtn = new Button("Check Mail", MailClient.IMG_PATH + "SychronizeListHS.gif");
        checkBtn.setLimit("1, 2, 1, 1");
        checkBtn.addActionListener(Button.ACTION_CLICK, checkMailListener);
        getChildren().add(checkBtn);
        
        composeBtn = new Button("Compose", MailClient.IMG_PATH + "NewMessageHS.gif");
        composeBtn.setLimit("3, 2, 1, 1");
        composeBtn.addActionListener(Button.ACTION_CLICK, composeListener);
        getChildren().add(composeBtn);
        
        
        mailBoxTree = new Tree();
        mailBoxTree.setLimit("0, 4, 5, 1");
        mailBoxTree.addActionListener(Tree.ACTION_CLICK, new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                MailBoxViewer.this.folderView.setText(((Tree.Item) ev.getSource()).getText());
                try {
                    MailBoxViewer.this.folderView.populateMessageList((ArrayGrid) ((Tree.Item) ev.getSource()).getUserObject());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        getChildren().add(mailBoxTree);
        
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
    void populateFolder(Message[] messages, String folderName, ProgressBar pb) throws Exception {
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
    
    

    ArrayGrid.Row getWelcomeMessage() {
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

    void openFolder(String folderName) {
        mailBoxTree.fireAction(new ActionEvent(ACTION_CLICK, mailBoxTree, folderMap.get(folderName)));
    }
    
    Button getCheckBtn() {
        return checkBtn;
    }
}
