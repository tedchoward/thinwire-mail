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

/*
 * @author Ted C. Howard
*/
class MessageRetriever extends Thread {
    
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
