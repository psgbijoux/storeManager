package com.storemanager.renderers;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class MultiLineTableCellRenderer extends JList<String> implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        boolean redBorderFlag = false;

        //make multi line where the cell value is String[]
        if (value instanceof String[]) {
            setListData((String[]) value);
            String[] values = (String[]) value;
            if (values.length == 5 && values[4].contains("stock: 0"))
                redBorderFlag = true;
        }

        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        setForeground(Color.DARK_GRAY);
        if (redBorderFlag) {
            setForeground(Color.RED);
        }

        if (isSelected) {
            setForeground(Color.WHITE);
            setBackground(UIManager.getColor("Table.selectionBackground"));
        } else {
            setBackground(UIManager.getColor("Table.background"));
        }

        return this;
    }
}
