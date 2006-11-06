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
import thinwire.ui.AlignX;
import thinwire.ui.Dialog;
import thinwire.ui.Label;
import thinwire.ui.MessageBox;
import thinwire.ui.TextArea;
import thinwire.ui.TextField;
import thinwire.ui.event.ActionEvent;
import thinwire.ui.event.ActionListener;
import thinwire.ui.layout.TableUnitModel;

/**
 * The ComposeDialog contains a Dialog set up for composing a new email. It has
 * a ToolBar with buttons for Send and Cancel, TextFields for To, Cc, and
 * Subject, and a TextArea for the body of the message. The Dialog is resizable,
 * and all the components resize themselves appropriately.
 * 
 * @author Ted C. Howard
 */
public class ComposeDialog extends Object {
    private Dialog dialog;
    private ToolBar toolBar;
    private TextField toField;
    private TextField ccField;
    private TextField subjectField;
    private TextArea msgBodyArea;

    ComposeDialog() {
        dialog = new Dialog();
        dialog.setResizeAllowed(true);
        dialog.setBounds(50, 50, 640, 480);
        dialog.setTitle("New Message");
        
        TableUnitModel tum = new TableUnitModel();
        tum.setWidths(56, 0);
        tum.setHeights(30, 20, 20, 20, 0);
        tum.setRowSpace(2);
        tum.setColumnSpace(2);
        dialog.setUnitModel(tum);
        
        toolBar = new ToolBar();
        toolBar.setBounds(0, 0, 2, 1);
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
        toField.setBounds(1, 1, 1, 1);
        dialog.getChildren().add(toField);

        Label toFieldLbl = new Label();
        toFieldLbl.setText("To:");
        toFieldLbl.setAlignX(AlignX.RIGHT);
        toFieldLbl.setBounds(0, 1, 1, 1);
        toFieldLbl.setLabelFor(toField);
        dialog.getChildren().add(toFieldLbl);

        ccField = new TextField();
        ccField.setBounds(1, 2, 1, 1);
        dialog.getChildren().add(ccField);
        Label ccFieldLbl = new Label();
        ccFieldLbl.setText("Cc:");
        ccFieldLbl.setAlignX(AlignX.RIGHT);
        ccFieldLbl.setBounds(0, 2, 1, 1);
        ccFieldLbl.setLabelFor(ccField);
        dialog.getChildren().add(ccFieldLbl);

        subjectField = new TextField("Hey! You can resize this Dialog!");
        subjectField.setBounds(1, 3, 1, 1);
        dialog.getChildren().add(subjectField);
        Label subjectFieldLbl = new Label();
        subjectFieldLbl.setText("Subject:");
        subjectFieldLbl.setAlignX(AlignX.RIGHT);
        subjectFieldLbl.setBounds(0, 3, 1, 1);
        subjectFieldLbl.setLabelFor(subjectField);
        dialog.getChildren().add(subjectFieldLbl);

        msgBodyArea = new TextArea();
        msgBodyArea.setBounds(0, 4, 2, 1);
        dialog.getChildren().add(msgBodyArea);
    }

    public Dialog getDialog() {
        return dialog;
    }
}
