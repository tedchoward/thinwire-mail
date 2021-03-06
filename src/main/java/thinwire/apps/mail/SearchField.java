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


import java.util.List;

import thinwire.ui.Button;
import thinwire.ui.GridBox;
import thinwire.ui.Panel;
import thinwire.ui.TextField;
import thinwire.ui.event.ActionEvent;
import thinwire.ui.event.ActionListener;

/**
 * The SearchField is a Panel containing a TextField and a Button. It searches
 * one GridBox for the values typed in the TextField and sends the results to a
 * different GridBox.
 * 
 * @author Ted C. Howard
 */
class SearchField extends Panel {
    private TextField searchField;
    private Button searchBtn;
    private GridBox searchSrc;
    private GridBox searchTarget;

    /*
     * The MessageList component contains two GridBoxes. The listener searches
     * one and populates the other with the results and then toggles their
     * visibility.
     */
    private ActionListener searchBtnClickListener = new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
            if (searchBtn.getText().equals("Search")) {
                searchTarget.getRows().clear();
                List<GridBox.Row> srcRows = searchSrc.getRows();
                if (srcRows.size() > 0) populateResult(srcRows, searchTarget, searchField.getText());
                searchSrc.setVisible(false);
                searchTarget.setVisible(true);
                searchBtn.setText("Clear");
            } else {
                searchSrc.setVisible(true);
                searchTarget.setVisible(false);
                searchBtn.setText("Search");
            }
        }
    };

    SearchField(GridBox searchSrc, GridBox searchTarget) {
        this.searchSrc = searchSrc;
        this.searchTarget = searchTarget;
        setWidth(270);
        setHeight(30);
        searchField = new TextField();
        searchField.setX(0);
        searchField.setY(5);
        searchField.setWidth(200);
        searchField.setHeight(20);
        getChildren().add(searchField);
        searchBtn = new Button("Search", MailClient.IMG_PATH + "GoLtrHS.gif");
        searchBtn.setX(205);
        searchBtn.setY(5);
        searchBtn.setWidth(62);
        searchBtn.setHeight(20);
        searchBtn.addActionListener(ACTION_CLICK, searchBtnClickListener);
        getChildren().add(searchBtn);
    }

    private void populateResult(List<GridBox.Row> src, GridBox target, String value) {
        boolean addRow;
        for (GridBox.Row curRow : src) {
            addRow = false;
            for (Object o : curRow) {
                if (o.toString().toLowerCase().indexOf(value) >= 0) addRow = true;
            }
            if (addRow) target.getRows().add(curRow);
        }
    }
}
