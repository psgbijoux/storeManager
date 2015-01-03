package com.storemanager.screens.settings;

import com.storemanager.components.AbstractPanel;
import com.storemanager.components.ImageButton;
import com.storemanager.components.Window;
import com.storemanager.models.Role;
import com.storemanager.models.User;
import com.storemanager.service.RoleService;
import com.storemanager.service.ServiceLocator;
import com.storemanager.service.ServiceName;
import com.storemanager.service.UserService;
import com.storemanager.util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class UserScreen extends AbstractPanel {
    private StoreLogger logger = StoreLogger.getInstance(UserScreen.class);
    private Window baseWindow;
    private JButton close, add, edit, cancel, save, delete;
    private JTextField username;
    private JPasswordField password, confirmPassword;
    private JComboBox role;
    private JPanel editPanel;
    private JScrollPane scrollPane;
    private JTable table;
    private String command;

    public UserScreen(Window baseWindow) {
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
        loadUsers();
    }

    private void loadUsers() {
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.addColumn("#");
        tableModel.addColumn("UserName");
        tableModel.addColumn("Role");
        UserService service = ServiceLocator.getService(ServiceName.USER_SERVICE);
        List<User> userList = new ArrayList<User>();
        try {
            userList = service.getUserList();
        } catch (ServiceException e) {
            Message.show(e);
        }
        for (User user : userList) {
            Object[] data = new Object[3];
            data[0] = user.getId();
            data[1] = user.getUsername();
            data[2] = user.getRole().getName();
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


    private void edit(User user) throws ServiceException {
        editPanel = new JPanel();
        editPanel.setLayout(null);
        editPanel.setBounds(460, 180, 310, 230);
        editPanel.setBackground(Color.lightGray);

        JLabel nameLabel = new JLabel("Username:");
        nameLabel.setBounds(20, 20, 100, 25);
        editPanel.add(nameLabel);

        username = new JTextField();
        username.setBounds(130, 20, 150, 25);
        editPanel.add(username);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(20, 50, 100, 25);
        editPanel.add(passwordLabel);

        password = new JPasswordField();
        password.setBounds(130, 50, 150, 25);
        editPanel.add(password);

        JLabel passwordConfirmLabel = new JLabel("Confirm Password:");
        passwordConfirmLabel.setBounds(20, 80, 120, 25);
        editPanel.add(passwordConfirmLabel);

        confirmPassword = new JPasswordField();
        confirmPassword.setBounds(130, 80, 150, 25);
        editPanel.add(confirmPassword);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setBounds(20, 110, 100, 25);
        editPanel.add(roleLabel);

        RoleService roleService = ServiceLocator.getService(ServiceName.ROLE_SERVICE);
        role = new JComboBox(roleService.getRoles().toArray());
        role.setBounds(130, 110, 170, 25);
        editPanel.add(role);

        cancel = new ImageButton(ButtonEnum.CANCEL, this);
        cancel.setLocation(150, 180);
        cancel.setSize(ButtonSizeEnum.DEFAULT.getSize());
        editPanel.add(cancel);

        save = new ImageButton(ButtonEnum.SAVE, this);
        save.setLocation(30, 180);
        save.setSize(ButtonSizeEnum.DEFAULT.getSize());
        editPanel.add(save);

        if (user != null) {
            username.setText(user.getUsername());
            password.setText(user.getPassword());
        }
        this.add(editPanel);
        this.repaint();
        this.validate();
    }

    public void actionPerformed(ActionEvent e) {
        ImageButton trigger = (ImageButton) e.getSource();
        UserService service = ServiceLocator.getService(ServiceName.USER_SERVICE);

        if (trigger.getCommand().equals(ButtonEnum.CLOSE.getCommand())) {
            baseWindow.closeScreen();
        }
        if (trigger.getCommand().equals(ButtonEnum.ADD.getCommand())) {
            command = "ADD";
            try {
                edit(null);
            } catch (ServiceException e1) {
                Message.show(e1);
            }
        }

        if (trigger.getCommand().equals(ButtonEnum.DELETE.getCommand())) {
            int index = table.getSelectedRow();
            if (index >= 0) {
                int response = JOptionPane.showConfirmDialog(null, "Do you want to delete user?", "Delete", JOptionPane.YES_NO_OPTION);
                if (response == 0) {
                    int userId = Integer.parseInt(table.getValueAt(index, 0).toString());
                    try {
                        service.deleteUser(new User(userId));
                        loadUsers();
                        JOptionPane.showMessageDialog(null, "User deleted.");
                        this.remove(editPanel);
                        this.repaint();
                    } catch (ServiceException e1) {
                        Message.show(e1);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Select a user first.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (trigger.getCommand().equals(ButtonEnum.EDIT.getCommand())) {
            int index = table.getSelectedRow();
            if (index >= 0) {
                int userId = Integer.parseInt(table.getValueAt(index, 0).toString());
                try {
                    User user = service.load(userId);
                    command = "EDIT";
                    edit(user);
                } catch (ServiceException e1) {
                    Message.show(e1);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Select a user first.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (trigger.getCommand().equals(ButtonEnum.CANCEL.getCommand())) {
            this.remove(editPanel);
            this.repaint();
        }

        if (trigger.getCommand().equals(ButtonEnum.SAVE.getCommand())) {
            StringBuilder sb = new StringBuilder();
            sb.append(FieldValidator.validateStringField("Username", username.getText()));
            sb.append(FieldValidator.validateStringField("Password", password.getText()));
            if (sb.length() > 0) {
                JOptionPane.showMessageDialog(null, sb.toString(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ("ADD".equals(command)) {
                User user = new User();
                user.setUsername(username.getText());
                user.setPassword(password.getText());
                Role selectedRole = (Role) role.getSelectedItem();
                if (selectedRole != null) {
                    user.setRoleId(selectedRole.getId());
                    user.setRole(selectedRole);
                }
                if (password.getText().equals(confirmPassword.getText())) {
                    try {
                        service.addUser(user);
                    } catch (ServiceException e1) {
                        Message.show(e1);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Password fields do not match.");
                }

            } else if ("EDIT".equals(command)) {
                int index = table.getSelectedRow();
                if (index >= 0) {
                    int userId = Integer.parseInt(table.getValueAt(index, 0).toString());
                    try {
                        User user = service.load(userId);
                        user.setUsername(username.getText());
                        user.setPassword(password.getText());
                        Role selectedRole = (Role) role.getSelectedItem();
                        if (selectedRole != null) {
                            user.setRoleId(selectedRole.getId());
                        } else {
                            JOptionPane.showMessageDialog(null, "Select a role.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        }
                        if (password.getText().equals(confirmPassword.getText())) {
                            service.updateUser(user);
                        } else {
                            JOptionPane.showMessageDialog(null, "Password fields do not match.");
                        }
                    } catch (ServiceException e1) {
                        Message.show(e1);
                    }
                }
            }
            loadUsers();
            this.remove(editPanel);
            this.repaint();
        }
    }
}
