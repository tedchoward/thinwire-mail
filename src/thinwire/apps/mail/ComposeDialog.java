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
import static thinwire.ui.Component.PROPERTY_HEIGHT;
import static thinwire.ui.Component.PROPERTY_WIDTH;
import thinwire.ui.AlignX;
import thinwire.ui.Dialog;
import thinwire.ui.Label;
import thinwire.ui.MessageBox;
import thinwire.ui.TextArea;
import thinwire.ui.TextField;
import thinwire.ui.event.ActionEvent;
import thinwire.ui.event.ActionListener;
import thinwire.ui.event.PropertyChangeEvent;
import thinwire.ui.event.PropertyChangeListener;

/**
 * The ComposeDialog contains a Dialog set up for composing a new email. It has
 * a ToolBar with buttons for Send and Cancel, TextFields for To, Cc, and
 * Subject, and a TextArea for the body of the message. The Dialog is resizable,
 * and all the components resize themselves appropriately.
 * 
 * @author Ted C. Howard
 */
public class ComposeDialog extends Object {
    private static final int TEXT_BORDER_SPACE = 65;
    private Dialog dialog;
    private ToolBar toolBar;
    private TextField toField;
    private TextField ccField;
    private TextField subjectField;
    private TextArea msgBodyArea;

    private PropertyChangeListener sizeListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent ev) {
            int size = ((Integer) ev.getNewValue()) - (MailClient.BORDER_SIZE * 2);
            if (ev.getPropertyName().equals(PROPERTY_WIDTH)) {
                int newSize = size - TEXT_BORDER_SPACE;
                toolBar.setWidth(size);
                toField.setWidth(newSize);
                ccField.setWidth(newSize);
                subjectField.setWidth(newSize);
                msgBodyArea.setWidth(size - 10);
            } else {
                msgBodyArea.setHeight(msgBodyArea.getHeight() + (((Integer) ev.getNewValue()) - ((Integer) ev.getOldValue())));
            }
        }
    };

    ComposeDialog() {
        dialog = new Dialog();
        dialog.setResizeAllowed(true);
        dialog.setBounds(50, 50, 640, 480);
        dialog.setTitle("New Message");
        toolBar = new ToolBar();
        toolBar.setX(0);
        toolBar.setY(0);
        toolBar.setWidth(634);
        toolBar.setHeight(30);
        toolBar.addButton("Send", MailClient.IMG_PATH + "EnvelopeHS.gif");
        toolBar.getButton("Send").addActionListener(ACTION_CLICK, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MessageBox.confirm("Feature not enabled");
            }
        });
        toolBar.addButton("Cancel", MailClient.IMG_PATH + "delete_16x.gif");
        toolBar.getButton("Cancel").addActionListener(ACTION_CLICK, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
            }
        });
        dialog.getChildren().add(toolBar);
        toField = new TextField();
        toField.setBounds(55, 40, 569, 20);
        dialog.getChildren().add(toField);

        Label toFieldLbl = new Label();
        toFieldLbl.setText("To:");
        toFieldLbl.setAlignX(AlignX.RIGHT);
        toFieldLbl.setBounds(20, 40, 30, 20);
        toFieldLbl.setLabelFor(toField);
        dialog.getChildren().add(toFieldLbl);

        ccField = new TextField();
        ccField.setBounds(55, 65, 569, 20);
        dialog.getChildren().add(ccField);
        Label ccFieldLbl = new Label();
        ccFieldLbl.setText("Cc:");
        ccFieldLbl.setAlignX(AlignX.RIGHT);
        ccFieldLbl.setBounds(20, 65, 30, 20);
        ccFieldLbl.setLabelFor(ccField);
        dialog.getChildren().add(ccFieldLbl);

        subjectField = new TextField("Hey! You can resize this Dialog!");
        subjectField.setBounds(55, 90, 569, 20);
        dialog.getChildren().add(subjectField);
        Label subjectFieldLbl = new Label();
        subjectFieldLbl.setText("Subject:");
        subjectFieldLbl.setAlignX(AlignX.RIGHT);
        subjectFieldLbl.setBounds(0, 90, 50, 20);
        subjectFieldLbl.setLabelFor(subjectField);
        dialog.getChildren().add(subjectFieldLbl);

        msgBodyArea = new TextArea();
        msgBodyArea.setBounds(MailClient.BORDER_SIZE, 115, 624, dialog.getInnerHeight() - 115 - 5);
        dialog.getChildren().add(msgBodyArea);
        dialog.addPropertyChangeListener(new String[] { PROPERTY_WIDTH, PROPERTY_HEIGHT }, sizeListener);
    }

    public Dialog getDialog() {
        return dialog;
    }
}
