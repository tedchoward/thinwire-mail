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
import thinwire.ui.style.Color;
import thinwire.util.Grid;

/**
 * The MailTabSheet is a TabSheet that contains a ToolBar at the top, a GridBox
 * for displaying a list of messages, and a WebBrowser for displaying the
 * content of the currently selected message.
 * 
 * @author Ted C. Howard
 */
public class MailTabSheet extends TabSheet {
    private ToolBar toolBar;
    private Panel contentPanel;
    private MessageViewer mv;
    private MessageList ml;
    private MailClient mc;

    /*
     * When the MailTabSheet is resized as a result of the browser being
     * resized, the internal components are resized accordingly.
     */
    private PropertyChangeListener sizeListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent ev) {
            //int size = ((Integer) ev.getNewValue()) - (MailClient.BORDER_SIZE);
            if (ev.getPropertyName().equals(PROPERTY_WIDTH)) {
                int size = ((TabSheet) ev.getSource()).getInnerWidth();
                contentPanel.setWidth(size);
                toolBar.setWidth(size);
            } else {
                int size = ((TabSheet) ev.getSource()).getInnerHeight() - toolBar.getHeight();
                if (size > 0) {
                    contentPanel.setHeight(size);
                } else {
                    contentPanel.setHeight(0);
                }
            }
        }
    };

    /*
     * The following ActionListeners are public so they can be referenced by the
     * Menu created in MailClient.java
     */
    public ActionListener printAction = new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
            Hyperlink h = new Hyperlink();
            h.setText("Click Here to Open the Message in a New Window");
            h.setWidth(225);
            h.setHeight(20);
            h.setLocation(mv.getLocation());
            MessageBox.confirm(null, "Print the Email", h, "Done");
        }
    };

    public ActionListener acctSettingsAction = new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
            Properties properties = mc.getProperties();
            if (properties.getProperty("useDefault").equals("YES")) properties.clear();
            PropertiesDialog propDlg = new PropertiesDialog(properties);
            if (propDlg.confirm() == 1) {
                try {
                    mc.checkMail();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    };

    MailTabSheet(MailClient mc) {
        getStyle().getBackground().setColor(Color.SILVER);
        getStyle().getBorder().setColor(getStyle().getBackground().getColor());
        mv = new MessageViewer();
        mv.getStyle().getBackground().setColor(getStyle().getBackground().getColor());
        ml = new MessageList(mv, mc);
        ml.getStyle().getBackground().setColor(getStyle().getBackground().getColor());
        this.mc = mc;
        toolBar = new ToolBar();
        toolBar.getStyle().getBackground().setColor(getStyle().getBackground().getColor());
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
        toolBar.addButton("Account Settings", MailClient.IMG_PATH + "list.png");
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
        contentPanel.getStyle().getBackground().setColor(getStyle().getBackground().getColor());
        getChildren().add(contentPanel);
        addPropertyChangeListener(new String[] { PROPERTY_WIDTH, PROPERTY_HEIGHT }, sizeListener);
    }

    public void populateMessageList(Grid folderGrid) throws Exception {
        ml.populate(folderGrid);
    }
}
