package thinwire.apps.mail;

public class MessageRetriever extends Thread {
    
    private MailClient mc;
    private MessageList ml;
    
    MessageRetriever(MessageList ml, MailClient mc) {
        this.ml = ml;
        this.mc = mc;
    }
    
    public void run() {
        try {
            while (ml.getNextMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            mc.closeConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
