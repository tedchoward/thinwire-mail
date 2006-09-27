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
public class PropertiesDialog {
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

    private ActionListener okClickListener = new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
            if (useDefault.isChecked()) {
                properties.setProperty("useDefault", "YES");
                try {
                    File file = Application.current().getRelativeFile("thinwire_mail.properties");
                    if (!file.exists()) file.createNewFile();
                    properties.load(new FileInputStream(file));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                properties.setProperty("useDefault", "");
                if (useSSL.isChecked()) {
                    properties.setProperty("connectionType", "pop3+ssl");
                } else {
                    properties.setProperty("connectionType", "pop3");
                }
                properties.setProperty("username", userName.getText());
                properties.setProperty("password", password.getText());
                properties.setProperty("server", server.getText());
                properties.setProperty("emailAddress", emailAddress.getText());
            }
            returnValue = 1;
            dialog.setVisible(false);
        }
    };
    
    private ActionListener cancelClickListener = new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
            returnValue = 0;
            properties.setProperty("useDefault", "YES");
            dialog.setVisible(false);
        }
    };

    PropertiesDialog(Properties properties) {
        this.properties = properties;
        dialog = new Dialog();
        dialog.setBounds(10, 10, 425, 200);
        dialog.setTitle("Account Settings");
        
        useDefault = new CheckBox("Use Demo Account");
        useDefault.setBounds(115, 10, 160, 20);
        useDefault.addPropertyChangeListener(CheckBox.PROPERTY_CHECKED, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent ev) {
                if ((Boolean) ev.getNewValue()) {
                    userName.setVisible(false);
                    userName.getLabel().setVisible(false);
                    password.setVisible(false);
                    password.getLabel().setVisible(false);
                    server.setVisible(false);
                    server.getLabel().setVisible(false);
                    emailAddress.setVisible(false);
                    emailAddress.getLabel().setVisible(false);
                    useSSL.setVisible(false);
                    okBtn.setY(15);
                    cancelBtn.setY(15);
                    dialog.setHeight(75);
                } else {
                    dialog.setHeight(200);
                    okBtn.setY(140);
                    cancelBtn.setY(140);
                    userName.setVisible(true);
                    userName.getLabel().setVisible(true);
                    password.setVisible(true);
                    password.getLabel().setVisible(true);
                    server.setVisible(true);
                    server.getLabel().setVisible(true);
                    emailAddress.setVisible(true);
                    emailAddress.getLabel().setVisible(true);
                    useSSL.setVisible(true);
                }
            }
        });
        dialog.getChildren().add(useDefault);

        userName = new TextField();
        userName.setBounds(115, 35, 300, 20);
        userName.setText(properties.getProperty("username", ""));
        dialog.getChildren().add(userName);
        Label userNameLbl = new Label();
        userNameLbl.setText("User Name:");
        userNameLbl.setBounds(5, 35, 100, 20);
        userNameLbl.setAlignX(AlignX.RIGHT);
        userNameLbl.setLabelFor(userName);
        dialog.getChildren().add(userNameLbl);

        password = new TextField();
        password.setBounds(115, 60, 300, 20);
        password.setInputHidden(true);
        password.setText(properties.getProperty("password", ""));
        dialog.getChildren().add(password);
        Label passwordLbl = new Label();
        passwordLbl.setText("Password:");
        passwordLbl.setBounds(5, 60, 100, 20);
        passwordLbl.setAlignX(AlignX.RIGHT);
        passwordLbl.setLabelFor(password);
        dialog.getChildren().add(passwordLbl);

        server = new TextField();
        server.setBounds(115, 85, 300, 20);
        server.setText(properties.getProperty("server", ""));
        dialog.getChildren().add(server);
        Label serverLbl = new Label();
        serverLbl.setText("Server:");
        serverLbl.setBounds(5, 85, 100, 20);
        serverLbl.setAlignX(AlignX.RIGHT);
        serverLbl.setLabelFor(server);
        dialog.getChildren().add(serverLbl);

        emailAddress = new TextField();
        emailAddress.setBounds(115, 110, 300, 20);
        emailAddress.setText(properties.getProperty("emailAddress", ""));
        dialog.getChildren().add(emailAddress);
        Label emailLbl = new Label();
        emailLbl.setText("Email Address:");
        emailLbl.setBounds(5, 110, 100, 20);
        emailLbl.setAlignX(AlignX.RIGHT);
        emailLbl.setLabelFor(emailAddress);
        dialog.getChildren().add(emailLbl);

        useSSL = new CheckBox();
        useSSL.setText("SSL Connection");
        useSSL.setBounds(115, 135, 120, 20);
        if (properties.getProperty("connectionType", "").equals("pop3+ssl")) useSSL.setChecked(true);
        dialog.getChildren().add(useSSL);

        okBtn = new Button("OK", MailClient.IMG_PATH + "ok.png");
        okBtn.setBounds(290, 140, 60, 30);
        okBtn.addActionListener(ACTION_CLICK, okClickListener);
        dialog.getChildren().add(okBtn);
        
        cancelBtn = new Button("Cancel", MailClient.IMG_PATH + "cancel.png");
        cancelBtn.setBounds(355, 140, 60, 30);
        cancelBtn.addActionListener(ACTION_CLICK, cancelClickListener);
        dialog.getChildren().add(cancelBtn);
        
        useDefault.setChecked(properties.getProperty("useDefault", "YES") == "YES");
    }

    public Dialog getDialog() {
        return dialog;
    }

    public Properties getProperties() {
        return properties;
    }
    
    public int confirm() {
        returnValue = 0;
        dialog.setVisible(true);
        return returnValue;
    }

}
