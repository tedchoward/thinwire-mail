/*
                         ThinWire(TM) Mail Demo
              Copyright (C) 2003-2006 Custom Credit Systems
   
 This program is free software; you can redistribute it and/or modify it under
 the terms of the GNU General Public License as published by the Free Software
 Foundation; either version 2 of the License, or (at your option) any later
 version.
   
 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
   
 You should have received a copy of the GNU General Public License along with
 this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 Place, Suite 330, Boston, MA 02111-1307 USA
  
 Users wishing to use this library in proprietary products which are not 
 themselves to be released under the GNU Public License should contact Custom
 Credit Systems for a license to do so.
   
               Custom Credit Systems, Richardson, TX 75081, USA.
                          http://www.thinwire.com
 */
package thinwire.apps.mail;

import java.util.Comparator;
import java.util.Date;

import thinwire.ui.Panel;
import thinwire.ui.GridBox;
import thinwire.ui.Component;
import thinwire.ui.event.PropertyChangeEvent;
import thinwire.ui.event.PropertyChangeListener;
import thinwire.util.Grid;

public class MessageList extends Panel {
	private GridBox messageGrid;
	private GridBox altGrid;
	private MessageViewer mv;
	
	private PropertyChangeListener sizeListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent ev) {
			int size = ((Integer) ev.getNewValue()) - (MailClient.BORDER_SIZE * 2);
			
			if (ev.getPropertyName().equals(GridBox.PROPERTY_WIDTH)) {
				messageGrid.setWidth(size);
				altGrid.setWidth(size);
			} else {
				messageGrid.setHeight(size);
				altGrid.setHeight(size);
			}
		}
	};
	
	private PropertyChangeListener selectionChange = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent ev) {
			try {
				mv.setMessage(((GridBox.Row) ev.getSource()).get("content").toString());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	};
	
	private PropertyChangeListener altSelectionListener = new PropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent ev) {
			GridBox.Row selectedRow = (GridBox.Row) ev.getSource();
			messageGrid.getRows().get(messageGrid.getRows().indexOf(selectedRow)).setSelected(true);
		}
		
	};
	
	MessageList(MessageViewer mv) {
		this.mv = mv;
		messageGrid = new GridBox();
		initGrid(messageGrid);
		messageGrid.addPropertyChangeListener(GridBox.Row.PROPERTY_ROW_SELECTED, selectionChange);
		getChildren().add(messageGrid);
		altGrid = new GridBox();
		initGrid(altGrid);
		altGrid.setVisible(false);
		altGrid.addPropertyChangeListener(GridBox.Row.PROPERTY_ROW_SELECTED, altSelectionListener);
		getChildren().add(altGrid);
		addPropertyChangeListener(new String[]{Component.PROPERTY_WIDTH, Component.PROPERTY_HEIGHT}, sizeListener);
	}
	
	public void populate(Grid folderGrid) throws Exception {
		messageGrid.getRows().clear();
		mv.clear();
		for (Object curRow : folderGrid.getRows()) {
			messageGrid.getRows().add(new GridBox.Row((Grid.Row) curRow));
		}
	}
	
	public void deleteMessage() {
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
		dateCol.setSortComparator(new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Date) o1).compareTo((Date) o2);
			}
		});
		gb.getColumns().add(dateCol);
		GridBox.Column contentCol = new GridBox.Column();
		contentCol.setName("content");
		contentCol.setVisible(false);
		gb.getColumns().add(contentCol);
		gb.setVisibleHeader(true);
	}
	
	public GridBox getMessageGrid() {
		return messageGrid;
	}
	
	public GridBox getAltGrid() {
		return altGrid;
	}
}
