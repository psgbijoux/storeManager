package com.storemanager.screens.settings;

import com.storemanager.components.AbstractPanel;
import com.storemanager.components.ImageButton;
import com.storemanager.components.Window;
import com.storemanager.models.Role;
import com.storemanager.service.RoleService;
import com.storemanager.service.ServiceLocator;
import com.storemanager.service.ServiceName;
import com.storemanager.util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class RoleScreen extends AbstractPanel {
    private StoreLogger logger = StoreLogger.getInstance(RoleScreen.class);
    private Window baseWindow;
    private JButton close, add, edit, cancel, save, delete;
    private JTextField name, description;
    private JPanel editPanel;
    private JScrollPane scrollPane;
    private JTable table;
    private String command;

    public RoleScreen(Window baseWindow) {
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
        loadRoles();
    }

    private void loadRoles() {
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.addColumn("#");
        tableModel.addColumn("Name");
        tableModel.addColumn("Description");
        RoleService service = ServiceLocator.getService(ServiceName.ROLE_SERVICE);
        List<Role> roleList = new ArrayList<Role>();
        try {
            roleList = service.getRoles();
        } catch (ServiceException e) {
            Message.show(e);
        }
        for (Role role : roleList) {
            Object[] data = new Object[3];
            data[0] = role.getId();
            data[1] = role.getName();
            data[2] = role.getDescription();
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


    private void edit(Role role) {
        editPanel = new JPanel();
        editPanel.setLayout(null);
        editPanel.setBounds(460, 180, 280, 180);
        editPanel.setBackground(Color.lightGray);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(20, 20, 100, 25);
        editPanel.add(nameLabel);

        name = new JTextField();
        name.setBounds(100, 20, 150, 25);
        editPanel.add(name);

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setBounds(20, 50, 100, 25);
        editPanel.add(descriptionLabel);

        description = new JTextField();
        description.setBounds(100, 50, 150, 25);
        editPanel.add(description);

        cancel = new ImageButton(ButtonEnum.CANCEL, this);
        cancel.setLocation(150, 120);
        cancel.setSize(ButtonSizeEnum.DEFAULT.getSize());
        editPanel.add(cancel);

        save = new ImageButton(ButtonEnum.SAVE, this);
        save.setLocation(30, 120);
        save.setSize(ButtonSizeEnum.DEFAULT.getSize());
        editPanel.add(save);

        if (role != null) {
            name.setText(role.getName());
            description.setText(role.getDescription());
        }
        this.add(editPanel);
        this.repaint();
    }

    public void actionPerformed(ActionEvent e) {
        ImageButton trigger = (ImageButton) e.getSource();
        RoleService service = ServiceLocator.getService(ServiceName.ROLE_SERVICE);

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
                int response = JOptionPane.showConfirmDialog(null, "Do you want to delete role?", "Delete", JOptionPane.YES_NO_OPTION);
                if (response == 0) {
                    int roleId = Integer.parseInt(table.getValueAt(index, 0).toString());
                    try {
                        service.delete(new Role(roleId));
                    } catch (ServiceException e1) {
                        Message.show(e1);
                    }
                    loadRoles();
                    JOptionPane.showMessageDialog(null, "Role deleted.");
                    this.remove(editPanel);
                    this.repaint();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Select a role first.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (trigger.getCommand().equals(ButtonEnum.EDIT.getCommand())) {
            int index = table.getSelectedRow();
            if (index >= 0) {
                int roleId = Integer.parseInt(table.getValueAt(index, 0).toString());
                Role role = null;
                try {
                    role = service.load(roleId);
                } catch (ServiceException e1) {
                    Message.show(e1);
                }
                command = "EDIT";
                edit(role);
            } else {
                JOptionPane.showMessageDialog(null, "Select a role first.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (trigger.getCommand().equals(ButtonEnum.CANCEL.getCommand())) {
            this.remove(editPanel);
            this.repaint();
        }

        if (trigger.getCommand().equals(ButtonEnum.SAVE.getCommand())) {
            StringBuilder sb = new StringBuilder();
            sb.append(FieldValidator.validateStringField("Name", name.getText()));
            sb.append(FieldValidator.validateStringField("Description", description.getText()));
            if (sb.length() > 0) {
                JOptionPane.showMessageDialog(null, sb.toString(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ("ADD".equals(command)) {
                Role role = new Role();
                role.setName(name.getText());
                role.setDescription(description.getText());
                try {
                    service.add(role);
                } catch (ServiceException e1) {
                    Message.show(e1);
                }
            } else if ("EDIT".equals(command)) {
                int index = table.getSelectedRow();
                if (index >= 0) {
                    int roleId = Integer.parseInt(table.getValueAt(index, 0).toString());
                    try {
                        Role role = service.load(roleId);
                        role.setName(name.getText());
                        role.setDescription(description.getText());
                        service.update(role);
                    } catch (ServiceException e1) {
                        Message.show(e1);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Select a role first.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            loadRoles();
            this.remove(editPanel);
            this.repaint();
        }
    }
}
