package com.storemanager.screens.settings;

import com.storemanager.components.AbstractPanel;
import com.storemanager.components.ImageButton;
import com.storemanager.components.Window;
import com.storemanager.models.Settings;
import com.storemanager.service.ServiceLocator;
import com.storemanager.service.ServiceName;
import com.storemanager.service.SettingsService;
import com.storemanager.util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SettingsScreen extends AbstractPanel {
    private Window baseWindow;
    private JButton close, add, edit, cancel, save, delete;
    private JTextField value;
    private JComboBox name;
    private JPanel editPanel;
    private JScrollPane scrollPane;
    private JTable table;
    private String command;

    public SettingsScreen(Window baseWindow) {
        this.baseWindow = baseWindow;
        this.setSize(800, 600);
        this.setBackground(Color.gray);
        this.setLayout(null);

        close = new ImageButton(ButtonEnum.CLOSE, this);
        close.setLocation(670, 530);
        close.setSize(ButtonSizeEnum.DEFAULT.getSize());
        this.add(close);

        //action buttons
        add = new ImageButton(ButtonEnum.ADD, this);
        add.setLocation(440, 30);
        add.setSize(ButtonSizeEnum.DEFAULT.getSize());
        this.add(add);

        edit = new ImageButton(ButtonEnum.EDIT, this);
        edit.setLocation(560, 30);
        edit.setSize(ButtonSizeEnum.DEFAULT.getSize());
        this.add(edit);

        delete = new ImageButton(ButtonEnum.DELETE, this);
        delete.setLocation(680, 30);
        delete.setSize(ButtonSizeEnum.DEFAULT.getSize());
        this.add(delete);
        try {
            loadSettings();
        } catch (ServiceException e) {
            Message.show(e);
        }
    }

    private void loadSettings() throws ServiceException {
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.addColumn("#");
        tableModel.addColumn("Name");
        tableModel.addColumn("Value");
        SettingsService service = ServiceLocator.getService(ServiceName.SETTINGS_SERVICE);
        java.util.List<Settings> settingsList = service.getSettings();

        for (Settings settings : settingsList) {
            Object[] data = new Object[3];
            data[0] = settings.getId();
            data[1] = settings.getName();
            data[2] = settings.getValue();
            tableModel.addRow(data);
        }
        table = new JTable(tableModel);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(30);
        table.getColumnModel().getColumn(1).setPreferredWidth(157);
        table.getColumnModel().getColumn(2).setPreferredWidth(210);

        if (scrollPane != null) {
            this.remove(scrollPane);
        }
        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 30, 400, 540);
        this.add(scrollPane);
        this.repaint();
    }

    private void edit(Settings settings) {
        editPanel = new JPanel();
        editPanel.setLayout(null);
        editPanel.setBounds(460, 180, 280, 180);
        editPanel.setBackground(Color.lightGray);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(20, 20, 100, 25);
        editPanel.add(nameLabel);

        name = new JComboBox(SettingsEnum.values());
        name.setBounds(80, 20, 180, 25);
        editPanel.add(name);

        JLabel descriptionLabel = new JLabel("Value:");
        descriptionLabel.setBounds(20, 50, 100, 25);
        editPanel.add(descriptionLabel);

        value = new JTextField();
        value.setBounds(80, 50, 180, 25);
        editPanel.add(value);

        cancel = new ImageButton(ButtonEnum.CANCEL, this);
        cancel.setLocation(150, 120);
        cancel.setSize(ButtonSizeEnum.DEFAULT.getSize());
        editPanel.add(cancel);

        save = new ImageButton(ButtonEnum.SAVE, this);
        save.setLocation(30, 120);
        save.setSize(ButtonSizeEnum.DEFAULT.getSize());
        editPanel.add(save);

        if (settings != null) {
            name.setSelectedItem(settings.getName());
            value.setText(settings.getValue());
        }
        this.add(editPanel);
        this.repaint();
        this.validate();
    }

    public void actionPerformed(ActionEvent e) {
        ImageButton trigger = (ImageButton) e.getSource();
        SettingsService service = ServiceLocator.getService(ServiceName.SETTINGS_SERVICE);

        if (trigger.getCommand().equals(ButtonEnum.CLOSE.getCommand())) {
            baseWindow.closeScreen();
        }
        if (trigger.getCommand().equals(ButtonEnum.ADD.getCommand())) {
            command = "ADD";
            edit(null);
        }

        if (trigger.getCommand().equals(ButtonEnum.DELETE.getCommand())) {
            int index = table.getSelectedRow();
            if (index >= 0) {
                int response = JOptionPane.showConfirmDialog(null, "Do you want to delete setting?", "Delete", JOptionPane.YES_NO_OPTION);
                if (response == 0) {
                    int settingId = Integer.parseInt(table.getValueAt(index, 0).toString());
                    try {
                        service.delete(new Settings(settingId));
                        loadSettings();
                    } catch (ServiceException e1) {
                        Message.show(e1);
                    }
                    JOptionPane.showMessageDialog(null, "Setting deleted.");
                    this.remove(editPanel);
                    this.repaint();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Select a setting first.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (trigger.getCommand().equals(ButtonEnum.EDIT.getCommand())) {
            int index = table.getSelectedRow();
            if (index >= 0) {
                int settingId = Integer.parseInt(table.getValueAt(index, 0).toString());
                try {
                    Settings settings = service.loadSettings(settingId);
                    command = "EDIT";
                    edit(settings);
                } catch (ServiceException e1) {
                    Message.show(e1);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Select a setting first.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (trigger.getCommand().equals(ButtonEnum.CANCEL.getCommand())) {
            this.remove(editPanel);
            this.repaint();
        }

        if (trigger.getCommand().equals(ButtonEnum.SAVE.getCommand())) {
            StringBuilder sb = new StringBuilder();
            sb.append(FieldValidator.validateStringField("Value", value.getText()));
            if (sb.length() > 0) {
                JOptionPane.showMessageDialog(null, sb.toString(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ("ADD".equals(command)) {
                Settings settings = new Settings(name.getSelectedItem().toString(), value.getText());
                try {
                    service.addNew(settings);
                } catch (ServiceException e1) {
                    Message.show(e1);
                }
            } else if ("EDIT".equals(command)) {
                int index = table.getSelectedRow();
                if (index >= 0) {
                    int settingId = Integer.parseInt(table.getValueAt(index, 0).toString());
                    try {
                        Settings settings = service.loadSettings(settingId);
                        settings.setName(name.getSelectedItem().toString());
                        settings.setValue(value.getText());
                        service.update(settings);
                    } catch (ServiceException e1) {
                        Message.show(e1);
                    }
                }
            }
            try {
                loadSettings();
            } catch (ServiceException e1) {
                Message.show(e1);
            }
            this.remove(editPanel);
            this.repaint();
        }
    }
}
