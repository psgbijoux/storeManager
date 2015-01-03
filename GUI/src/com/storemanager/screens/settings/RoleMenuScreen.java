package com.storemanager.screens.settings;

import com.storemanager.components.AbstractPanel;
import com.storemanager.components.ImageButton;
import com.storemanager.components.Window;
import com.storemanager.models.Menu;
import com.storemanager.models.Role;
import com.storemanager.models.XrefMenuRole;
import com.storemanager.service.*;
import com.storemanager.util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class RoleMenuScreen extends AbstractPanel {
    private StoreLogger logger = StoreLogger.getInstance(RoleMenuScreen.class);
    private Window baseWindow;
    private JButton close, add, cancel, save, delete;
    private JPanel editPanel;
    private JComboBox<Role> roleBox, menuBox;
    private JScrollPane scrollPane;
    private JTable table;

    public RoleMenuScreen(Window baseWindow) {
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

        delete = new ImageButton(ButtonEnum.DELETE, this);
        delete.setLocation(560, 30);
        delete.setSize(ButtonSizeEnum.DEFAULT.getSize());
        this.add(delete);

        //role ddl
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setBounds(30, 30, 100, 30);
        this.add(roleLabel);

        RoleService service = ServiceLocator.getService(ServiceName.ROLE_SERVICE);
        List<Role> roleList = new ArrayList<Role>();
        try {
            roleList = service.getRoles();
        } catch (ServiceException e) {
            Message.show(e);
        }
        roleBox = new JComboBox(roleList.toArray());
        roleBox.setBounds(63, 30, 365, 25);
        roleBox.addActionListener(this);
        this.add(roleBox);

        Role role = (Role) roleBox.getSelectedItem();
        loadRoleMenus(role.getId());
    }

    private void loadRoleMenus(int roleId) {
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Description");
        MenuService service = ServiceLocator.getService(ServiceName.MENU_SERVICE);
        List<Menu> menuList = new ArrayList<Menu>();
        try {
            menuList = service.getMenuList(roleId);
        } catch (ServiceException e) {
            Message.show(e);
        }
        int count = 0;
        for (Menu menu : menuList) {
            count++;
            Object[] data = new Object[3];
            data[0] = menu.getId();
            data[1] = menu.getName();
            data[2] = menu.getCommand();
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
        scrollPane.setBounds(30, 70, 400, 500);
        this.add(scrollPane);
        this.repaint();
    }

    private void edit() {
        editPanel = new JPanel();
        editPanel.setLayout(null);
        editPanel.setBounds(460, 180, 280, 180);
        editPanel.setBackground(Color.lightGray);

        JLabel nameLabel = new JLabel("Menu:");
        nameLabel.setBounds(20, 20, 100, 25);
        editPanel.add(nameLabel);

        MenuService service = ServiceLocator.getService(ServiceName.MENU_SERVICE);
        List<Menu> menuList = null;
        try {
            menuList = service.getMenuList();
        } catch (ServiceException e) {
            Message.show(e);
        }
        menuBox = new JComboBox(menuList.toArray());
        menuBox.setBounds(75, 20, 170, 25);
        editPanel.add(menuBox);

        cancel = new ImageButton(ButtonEnum.CANCEL, this);
        cancel.setLocation(150, 120);
        cancel.setSize(ButtonSizeEnum.DEFAULT.getSize());
        editPanel.add(cancel);

        save = new ImageButton(ButtonEnum.SAVE, this);
        save.setLocation(30, 120);
        save.setSize(ButtonSizeEnum.DEFAULT.getSize());
        editPanel.add(save);


        this.add(editPanel);
        this.repaint();
        this.validate();
    }


    public void actionPerformed(ActionEvent e) {
        // role ddl
        if (e.getSource() instanceof JComboBox && (JComboBox) e.getSource() == roleBox) {
            //implement change listener
            Role role = (Role) roleBox.getSelectedItem();
            loadRoleMenus(role.getId());
            return;
        }
        ImageButton trigger = (ImageButton) e.getSource();

        if (trigger.getCommand().equals(ButtonEnum.CLOSE.getCommand())) {
            baseWindow.closeScreen();
        }


        if (trigger.getCommand().equals(ButtonEnum.ADD.getCommand())) {
            edit();
        }
        if (trigger.getCommand().equals(ButtonEnum.DELETE.getCommand())) {
            int index = table.getSelectedRow();
            if (index >= 0) {
                int response = JOptionPane.showConfirmDialog(null, "Do you want to remove menu from role?", "Delete", JOptionPane.YES_NO_OPTION);
                if (response == 0) {
                    int menuId = Integer.parseInt(table.getValueAt(index, 0).toString());
                    Role role = (Role) roleBox.getSelectedItem();
                    XrefMenuRoleService service = ServiceLocator.getService(ServiceName.XREF_MENU_ROLE_SERVICE);
                    try {
                        XrefMenuRole menuRole = service.load(menuId, role.getId());
                        service.delete(menuRole);
                    } catch (ServiceException e1) {
                        Message.show(e1);
                    }
                    loadRoleMenus(role.getId());
                    JOptionPane.showMessageDialog(null, "Menu deleted.");
                    this.repaint();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Select a Role-Menu first.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (trigger.getCommand().equals(ButtonEnum.CANCEL.getCommand())) {
            this.remove(editPanel);
            this.repaint();
        }

        if (trigger.getCommand().equals(ButtonEnum.SAVE.getCommand())) {
            Menu menu = (Menu) menuBox.getSelectedItem();
            Role role = (Role) roleBox.getSelectedItem();
            XrefMenuRole menuRole = new XrefMenuRole(menu.getId(), role.getId());
            XrefMenuRoleService service = ServiceLocator.getService(ServiceName.XREF_MENU_ROLE_SERVICE);
            try {
                service.add(menuRole);
            } catch (ServiceException e1) {
                Message.show(e1);
            }
            loadRoleMenus(role.getId());
            JOptionPane.showMessageDialog(null, "Menu added to role: " + role.getName());
            this.remove(editPanel);
            this.repaint();
        }
    }
}
