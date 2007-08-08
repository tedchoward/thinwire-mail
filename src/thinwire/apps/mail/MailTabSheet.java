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

import java.util.Properties;

import thinwire.ui.Hyperlink;
import thinwire.ui.MessageBox;
import thinwire.ui.Panel;
import thinwire.ui.TabSheet;
import thinwire.ui.WebBrowser;
import thinwire.ui.event.ActionEvent;
import thinwire.ui.event.ActionListener;
import thinwire.ui.layout.SplitLayout;
import thinwire.ui.layout.TableLayout;
import thinwire.ui.style.Color;
import thinwire.util.Grid;

/**
 * The MailTabSheet is a TabSheet that contains a ToolBar at the top, a GridBox
 * for displaying a list of messages, and a WebBrowser for displaying the
 * content of the currently selected message.
 * 
 * @author Ted C. Howard
 */
class MailTabSheet extends TabSheet {
    private ToolBar toolBar;
    private Panel contentPanel;
    private WebBrowser mv;
    private MessageList ml;
    private MailClient mc;

    /*
     * The following ActionListeners aren't private so they can be referenced by the
     * Menu created in MailClient.java
     */
    ActionListener printAction = new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
            Hyperlink h = new Hyperlink();
            h.setText("Click Here to Open the Message in a New Window");
            h.setWidth(225);
            h.setHeight(20);
            h.setLocation(mv.getLocation());
            MessageBox.confirm(null, "Print the Email", h, "Done");
        }
    };

    ActionListener acctSettingsAction = new ActionListener() {
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
        mv = new WebBrowser();
        mv.getStyle().getBackground().setColor(Color.WHITE);
        mv.getStyle().getBackground().setColor(getStyle().getBackground().getColor());
        ml = new MessageList(mv, mc);
        ml.getStyle().getBackground().setColor(getStyle().getBackground().getColor());
        setLayout(new TableLayout(new double[][] {{0}, {30, 0}}));
        this.mc = mc;
        toolBar = new ToolBar();
        toolBar.getStyle().getBackground().setColor(getStyle().getBackground().getColor());
        toolBar.setLimit("0, 0");
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
        contentPanel.setLimit("0, 1");

        contentPanel.getChildren().add(ml);
        contentPanel.getChildren().add(mv);
        contentPanel.setLayout(new SplitLayout(.50));
        contentPanel.getStyle().getBackground().setColor(getStyle().getBackground().getColor());
        getChildren().add(contentPanel);
    }

    void populateMessageList(Grid folderGrid) throws Exception {
        ml.populate(folderGrid);
    }
}
