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

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import thinwire.ui.AlignX;
import thinwire.ui.Application;
import thinwire.ui.Button;
import thinwire.ui.CheckBox;
import thinwire.ui.Dialog;
import thinwire.ui.Label;
import thinwire.ui.TextField;
import thinwire.ui.event.ActionEvent;
import thinwire.ui.event.ActionListener;
import thinwire.ui.event.PropertyChangeEvent;
import thinwire.ui.event.PropertyChangeListener;
import thinwire.ui.layout.TableLayout;

/**
 * The PropertiesDialog contains a Dialog for getting the POP3 account settings
 * from the user. These settings are stored in a standard Java Properties file
 * called thinwire_mail.properties. If the file exists, the properties are
 * loaded from the file and the fields in the dialog are automatically
 * populated. When the OK button is clicked, the current values on the screen
 * are written out to the properties file.
 * 
 * @author Ted C. Howard
 */
class PropertiesDialog {
    private Dialog dialog;
    private TextField userName;
    private TextField password;
    private TextField server;
    private TextField emailAddress;
    private CheckBox useSSL;
    private Button okBtn;
    private Button cancelBtn;
    private Properties properties;
    private CheckBox useDefault;
    private int returnValue;

    PropertiesDialog(Properties properties) {
        this.properties = properties;
        dialog = new Dialog();
        dialog.setBounds(10, 10, 425, 200);
        dialog.setTitle("Account Settings");
        dialog.setLayout(new TableLayout(
            new double[][] {{115, 0, 60, 60, 5}, 
                            {5, 20, 20, 20, 20, 20, 20, 5, 5}}, 
            0, 5));
        
        useDefault = new CheckBox("Use Demo Account");
        useDefault.setLimit("1, 1, 1, 1");
        useDefault.addPropertyChangeListener(CheckBox.PROPERTY_CHECKED, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent ev) {
                if ((Boolean) ev.getNewValue()) {
                    userName.setEnabled(false);
                    password.setEnabled(false);
                    server.setEnabled(false);
                    emailAddress.setEnabled(false);
                    useSSL.setEnabled(false);
                } else {
                    userName.setEnabled(true);
                    password.setEnabled(true);
                    server.setEnabled(true);
                    emailAddress.setEnabled(true);
                    useSSL.setEnabled(true);
                }
            }
        });
        dialog.getChildren().add(useDefault);

        userName = new TextField();
        userName.setLimit("1, 2, 3, 1");
        userName.setText(properties.getProperty("username", ""));
        dialog.getChildren().add(userName);
        
        Label userNameLbl = new Label();
        userNameLbl.setText("User Name:");
        userNameLbl.setLimit("0, 2, 1, 1");
        userNameLbl.setAlignX(AlignX.RIGHT);
        userNameLbl.setLabelFor(userName);
        dialog.getChildren().add(userNameLbl);

        password = new TextField();
        password.setLimit("1, 3, 3, 1");
        password.setInputHidden(true);
        password.setText(properties.getProperty("password", ""));
        dialog.getChildren().add(password);
        
        Label passwordLbl = new Label();
        passwordLbl.setText("Password:");
        passwordLbl.setLimit("0, 3, 1, 1");
        passwordLbl.setAlignX(AlignX.RIGHT);
        passwordLbl.setLabelFor(password);
        dialog.getChildren().add(passwordLbl);

        server = new TextField();
        server.setLimit("1, 4, 3, 1");
        server.setText(properties.getProperty("server", ""));
        dialog.getChildren().add(server);
        
        Label serverLbl = new Label();
        serverLbl.setText("Server:");
        serverLbl.setLimit("0, 4, 1, 1");
        serverLbl.setAlignX(AlignX.RIGHT);
        serverLbl.setLabelFor(server);
        dialog.getChildren().add(serverLbl);

        emailAddress = new TextField();
        emailAddress.setLimit("1, 5, 3, 1");
        emailAddress.setText(properties.getProperty("emailAddress", ""));
        dialog.getChildren().add(emailAddress);
        Label emailLbl = new Label();
        emailLbl.setText("Email Address:");
        
        emailLbl.setLimit("0, 5, 1, 1");
        emailLbl.setAlignX(AlignX.RIGHT);
        emailLbl.setLabelFor(emailAddress);
        dialog.getChildren().add(emailLbl);

        useSSL = new CheckBox();
        useSSL.setText("SSL Connection");
        useSSL.setLimit("1, 6, 1, 1");
        if (properties.getProperty("connectionType", "").equals("pop3+ssl")) useSSL.setChecked(true);
        dialog.getChildren().add(useSSL);

        okBtn = new Button("OK", MailClient.IMG_PATH + "ok.png");
        okBtn.setLimit("2, 6, 1, 2");
        okBtn.addActionListener(ACTION_CLICK, new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                if (useDefault.isChecked()) {
                    PropertiesDialog.this.properties.setProperty("useDefault", "YES");
                    try {
                        File file = Application.current().getRelativeFile("thinwire_mail.properties");
                        if (!file.exists()) file.createNewFile();
                        PropertiesDialog.this.properties.load(new FileInputStream(file));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    PropertiesDialog.this.properties.setProperty("useDefault", "");
                    if (useSSL.isChecked()) {
                        PropertiesDialog.this.properties.setProperty("connectionType", "pop3+ssl");
                    } else {
                        PropertiesDialog.this.properties.setProperty("connectionType", "pop3");
                    }
                    PropertiesDialog.this.properties.setProperty("username", userName.getText());
                    PropertiesDialog.this.properties.setProperty("password", password.getText());
                    PropertiesDialog.this.properties.setProperty("server", server.getText());
                    PropertiesDialog.this.properties.setProperty("emailAddress", emailAddress.getText());
                }
                returnValue = 1;
                dialog.setVisible(false);
            }
        });
        dialog.getChildren().add(okBtn);
        
        cancelBtn = new Button("Cancel", MailClient.IMG_PATH + "cancel.png");
        cancelBtn.setLimit("3, 6, 1, 2");
        cancelBtn.addActionListener(ACTION_CLICK, new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                returnValue = 0;
                PropertiesDialog.this.properties.setProperty("useDefault", "YES");
                dialog.setVisible(false);
            }
        });
        dialog.getChildren().add(cancelBtn);
        
        useDefault.setChecked(properties.getProperty("useDefault", "YES") == "YES");
    }

    Dialog getDialog() {
        return dialog;
    }

    Properties getProperties() {
        return properties;
    }
    
    int confirm() {
        returnValue = 0;
        dialog.setVisible(true);
        return returnValue;
    }
}
