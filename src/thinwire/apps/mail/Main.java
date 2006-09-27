package thinwire.apps.mail;


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
public class Main {
    public static void main(String[] args) throws Exception {
        new MailClient();
    }
}
