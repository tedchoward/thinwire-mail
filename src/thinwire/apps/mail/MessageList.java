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

import static thinwire.ui.GridBox.Row.PROPERTY_ROW_SELECTED;

import java.util.Comparator;
import java.util.Date;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;

import thinwire.ui.GridBox;
import thinwire.ui.Panel;
import thinwire.ui.WebBrowser;
import thinwire.ui.GridBox.Column.SortOrder;
import thinwire.ui.event.PropertyChangeEvent;
import thinwire.ui.event.PropertyChangeListener;
import thinwire.util.Grid;

/**
 * The MessageList is a Panel that contains the GridBox that displays the list
 * of messages in the current folder. In actuality, it contains two GridBoxes
 * that alternate their visibility. messageGrid shows the complete set of
 * messages in the folder. altGrid shows the set resulting from a search.
 * 
 * @author Ted C. Howard
 */
class MessageList extends Panel {
    private GridBox messageGrid;
    private GridBox altGrid;
    private WebBrowser mv;
    private MailClient mc;
    private int msgIndex;

    private PropertyChangeListener sizeListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent ev) {
            if (ev.getPropertyName().equals(PROPERTY_WIDTH)) {
                int size = ((Panel) ev.getSource()).getInnerWidth();
                messageGrid.setWidth(size);
                altGrid.setWidth(size);
            } else {
                int size = ((Panel) ev.getSource()).getInnerHeight() - MailClient.BORDER_SIZE * 2;
                messageGrid.setHeight(size);
                altGrid.setHeight(size);
            }
        }
    };

    private PropertyChangeListener selectionChange = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent ev) {
            GridBox.Row curRow = (GridBox.Row) ev.getSource();
            try {
                getMessage(messageGrid.getRows().indexOf(curRow));
                mv.setContent(((GridBox.Row) ev.getSource()).get("content").toString());                
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    /*
     * When the altGrid is being displayed, all selection events are passed on
     * to the messageGrid.
     */
    private PropertyChangeListener altSelectionListener = new PropertyChangeListener() {

        public void propertyChange(PropertyChangeEvent ev) {
            GridBox.Row selectedRow = (GridBox.Row) ev.getSource();
            messageGrid.getRows().get(messageGrid.getRows().indexOf(selectedRow)).setSelected(true);
        }

    };

    MessageList(WebBrowser mv, MailClient mc) {
        msgIndex = -1;
        this.mv = mv;
        this.mc = mc;
        messageGrid = new GridBox();
        initGrid(messageGrid);
        messageGrid.addPropertyChangeListener(PROPERTY_ROW_SELECTED, selectionChange);
        getChildren().add(messageGrid);
        altGrid = new GridBox();
        initGrid(altGrid);
        altGrid.setVisible(false);
        altGrid.addPropertyChangeListener(PROPERTY_ROW_SELECTED, altSelectionListener);
        getChildren().add(altGrid);
        addPropertyChangeListener(new String[] { PROPERTY_WIDTH, PROPERTY_HEIGHT }, sizeListener);
    }

    void populate(Grid folderGrid) throws Exception {
        msgIndex = -1;
        messageGrid.getRows().clear();
        mv.setLocation("about:blank");
        for (Object curRow : folderGrid.getRows()) {
            messageGrid.getRows().add(new GridBox.Row((Grid.Row) curRow));
        }
        messageGrid.getColumns().get(2).setSortOrder(SortOrder.DESC);
        MessageRetriever mr = new MessageRetriever(this, mc);
        mr.start();
    }

    void deleteMessage() {
        if (messageGrid.getSelectedRow() != null) messageGrid.getRows().remove(messageGrid.getSelectedRow());
    }

    private void initGrid(GridBox gb) {
        GridBox.Column fromCol = new GridBox.Column();
        fromCol.setName("From");
        fromCol.setVisible(true);
        gb.getColumns().add(fromCol);
        GridBox.Column subjCol = new GridBox.Column();
        subjCol.setName("Subject");
        subjCol.setVisible(true);
        gb.getColumns().add(subjCol);
        GridBox.Column dateCol = new GridBox.Column();
        dateCol.setName("Date Received");
        dateCol.setVisible(true);
        gb.getColumns().add(dateCol);
        /*
         * GridBox.Columns can be sorted by simply clicking on the column
         * header. By default they sort alphabetically. In the case of the date
         * column, we need to define a new Comparator.
         */
        dateCol.setSortComparator(new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Date) o1).compareTo((Date) o2);
            }
        });
        GridBox.Column contentCol = new GridBox.Column();
        contentCol.setName("content");
        contentCol.setVisible(false);
        gb.getColumns().add(contentCol);
        gb.setVisibleHeader(true);
    }

    /**
     * @return the main GridBox that contains the complete set of messages
     */
    GridBox getMessageGrid() {
        return messageGrid;
    }

    /**
     * @return the alternate GridBox used for displaying search results
     */
    GridBox getAltGrid() {
        return altGrid;
    }
    
    private synchronized void getMessage(int msgIndex) throws Exception {
        if (msgIndex < messageGrid.getRows().size()) {
            Object tmp = messageGrid.getRows().get(msgIndex).get("content");
            if (tmp instanceof Message) {
                messageGrid.getRows().get(msgIndex).set(3, getMessage((Message) tmp));
            }
        }
    }
    
    synchronized boolean getNextMessage() throws Exception {
        if (++msgIndex < messageGrid.getRows().size()) {
            Object tmp = messageGrid.getRows().get(msgIndex).get("content");
            if (tmp instanceof Message) {
                messageGrid.getRows().get(msgIndex).set(3, getMessage((Message) tmp));
            } else {
                return getNextMessage();
            }
            return true;
        } else {
            return false;
        }
    }
    
    synchronized String getMessage(Message m) throws Exception {
        StringBuilder sb = new StringBuilder();
        Address[] recipients = m.getRecipients(Message.RecipientType.TO);
        for (Address a : recipients) {
            sb.append(a.toString()).append("; ");
        }
        String header = "From: <span style=\"font-weight: bold;\">"
            + m.getFrom()[0] + "</span><br />To: <span style=\""
            + "font-weight: bold;\">" + sb.toString()
            + "</span><br />Subject: <span style=\"font-weight: bold;\">"
            + m.getSubject() + "</span><br /><br /><hr /><br />";
        if (m.getContent().getClass().equals(MimeMultipart.class)) {
            MimeMultipart mp = (MimeMultipart) m.getContent();
            String content = new String();
            if (mp.getContentType().indexOf("multipart/alternative") >= 0) {
                for (int i = 0, cnt = mp.getCount(); i < cnt; i++) {
                    Part part = mp.getBodyPart(i);
                    content = header + part.getContent().toString();
                }
            } else if (mp.getContentType().indexOf("multipart/mixed") >= 0) {
                for (int i = 0, cnt = mp.getCount(); i < cnt; i++) {
                    Part part = mp.getBodyPart(i);
                    if (part.getContentType().indexOf("text") >= 0) {
                        content = header + part.getContent().toString();
                    }
                }
            }
            return content;
        } else {
            return header + m.getContent().toString().replaceAll("\n", "<br/>");
        }
    }
}
