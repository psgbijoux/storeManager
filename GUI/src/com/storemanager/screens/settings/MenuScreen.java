package com.storemanager.screens.settings;

import com.storemanager.components.AbstractPanel;
import com.storemanager.components.ImageButton;
import com.storemanager.components.Window;
import com.storemanager.models.Menu;
import com.storemanager.service.MenuService;
import com.storemanager.service.ServiceLocator;
import com.storemanager.service.ServiceName;
import com.storemanager.util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class MenuScreen extends AbstractPanel {
    private StoreLogger logger = StoreLogger.getInstance(MenuScreen.class);
    private Window baseWindow;
    private JButton close, add, edit, cancel, save, delete, open, back;
    private JTextField name, menuCommand, imagePath;
    private JPanel editPanel;
    private JScrollPane scrollPane;
    private JTable table;
    private String command;
    private Menu parentMenu;
    private int parentId;

    public MenuScreen(Window baseWindow) {
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

        open = new ImageButton(ButtonEnum.OPEN, this);
        open.setLocation(440, 80);
        open.setSize(ButtonSizeEnum.DEFAULT.getSize());
        this.add(open);

        back = new ImageButton(ButtonEnum.BACK, this);
        back.setLocation(440, 80);
        back.setSize(ButtonSizeEnum.DEFAULT.getSize());
        back.setVisible(false);
        this.add(back);

        loadMenus();
    }

    private void loadMenus() {
        loadMenus(null);
    }

    private void loadMenus(Menu parent) {
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.addColumn("#");
        tableModel.addColumn("Name");
        tableModel.addColumn("Command");
        MenuService service = ServiceLocator.getService(ServiceName.MENU_SERVICE);
        List<Menu> menuList = null;
        try {
            if (parent == null) {
                menuList = service.getMenuList();
            } else {
                menuList = service.getSubMenuList(parent);
            }
        } catch (ServiceException e) {
            Message.show(e);
        }


        for (Menu menu : menuList) {
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
        scrollPane.setBounds(30, 30, 400, 540);
        this.add(scrollPane);
        this.repaint();
    }

    private void edit(Menu menu) {
        editPanel = new JPanel();
        editPanel.setLayout(null);
        editPanel.setBounds(460, 180, 280, 220);
        editPanel.setBackground(Color.lightGray);

        String parentValue;
        if (parentMenu != null) {
            parentValue = "Category: " + parentMenu.getName();
            parentId = parentMenu.getId();
        } else {
            parentValue = "Main menu";
            parentId = 0;
        }
        JLabel parentLabel = new JLabel(parentValue);
        parentLabel.setBounds(20, 20, 150, 25);
        editPanel.add(parentLabel);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(20, 50, 100, 25);
        editPanel.add(nameLabel);

        name = new JTextField();
        name.setBounds(100, 50, 150, 25);
        editPanel.add(name);

        JLabel commandLabel = new JLabel("Command:");
        commandLabel.setBounds(20, 80, 100, 25);
        editPanel.add(commandLabel);

        menuCommand = new JTextField();
        menuCommand.setBounds(100, 80, 150, 25);
        editPanel.add(menuCommand);

        JLabel imagePathLabel = new JLabel("Image Path:");
        imagePathLabel.setBounds(20, 110, 100, 25);
        editPanel.add(imagePathLabel);

        imagePath = new JTextField();
        imagePath.setBounds(100, 110, 150, 25);
        editPanel.add(imagePath);

        cancel = new ImageButton(ButtonEnum.CANCEL, this);
        cancel.setLocation(150, 170);
        cancel.setSize(ButtonSizeEnum.DEFAULT.getSize());
        editPanel.add(cancel);

        save = new ImageButton(ButtonEnum.SAVE, this);
        save.setLocation(30, 170);
        save.setSize(ButtonSizeEnum.DEFAULT.getSize());
        editPanel.add(save);

        if (menu != null) {
            name.setText(menu.getName());
            menuCommand.setText(menu.getCommand());
            imagePath.setText(menu.getImagePath());
        }
        this.add(editPanel);
        this.repaint();
    }

    public void actionPerformed(ActionEvent e) {
        ImageButton trigger = (ImageButton) e.getSource();
        MenuService service = ServiceLocator.getService(ServiceName.MENU_SERVICE);

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
                int response = JOptionPane.showConfirmDialog(null, "Do you want to delete menu?", "Delete", JOptionPane.YES_NO_OPTION);
                if (response == 0) {
                    int menuId = Integer.parseInt(table.getValueAt(index, 0).toString());
                    try {
                        service.delete(new Menu(menuId));
                    } catch (ServiceException e1) {
                        Message.show(e1);
                    }
                    loadMenus(parentMenu);
                    JOptionPane.showMessageDialog(null, "Menu deleted.");
                    this.remove(editPanel);
                    this.repaint();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Select a menu first.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (trigger.getCommand().equals(ButtonEnum.EDIT.getCommand())) {
            int index = table.getSelectedRow();
            if (index >= 0) {
                int menuId = Integer.parseInt(table.getValueAt(index, 0).toString());
                Menu menu = null;
                try {
                    menu = service.load(menuId);
                } catch (ServiceException e1) {
                    Message.show(e1);
                }
                command = "EDIT";
                edit(menu);
            } else {
                JOptionPane.showMessageDialog(null, "Select a menu first.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (trigger.getCommand().equals(ButtonEnum.CANCEL.getCommand())) {
            this.remove(editPanel);
            this.repaint();
        }

        if (trigger.getCommand().equals(ButtonEnum.SAVE.getCommand())) {
            StringBuilder sb = new StringBuilder();
            sb.append(FieldValidator.validateStringField("Name", name.getText()));
            sb.append(FieldValidator.validateStringField("Command", menuCommand.getText()));
            if ("ADD".equals(command)) {
                sb.append(FieldValidator.validateStringField("Image Path", imagePath.getText()));
            }
            if (sb.length() > 0) {
                JOptionPane.showMessageDialog(null, sb.toString(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if ("ADD".equals(command)) {
                Menu menu = new Menu();
                menu.setName(name.getText());
                menu.setImagePath(imagePath.getText());
                menu.setCommand(menuCommand.getText());
                menu.setParentId(parentId);
                try {
                    service.add(menu);
                } catch (ServiceException e1) {
                    Message.show(e1);
                }
            } else if ("EDIT".equals(command)) {
                int index = table.getSelectedRow();
                if (index >= 0) {
                    int menuId = Integer.parseInt(table.getValueAt(index, 0).toString());
                    Menu menu = null;
                    try {
                        menu = service.load(menuId);
                    } catch (ServiceException e1) {
                        Message.show(e1);
                    }
                    menu.setName(name.getText());
                    menu.setCommand(menuCommand.getText());
                    if (!Strings.isEmpty(imagePath.getText())) {
                        menu.setImagePath(imagePath.getText());
                    }
                    menu.setParentId(parentId);
                    try {
                        service.update(menu);
                    } catch (ServiceException e1) {
                        Message.show(e1);
                    }
                }
            }
            loadMenus(parentMenu);
            this.remove(editPanel);
            this.repaint();
        }
        if (trigger.getCommand().equals(ButtonEnum.OPEN.getCommand())) {
            int index = table.getSelectedRow();
            if (index >= 0) {
                int menuId = Integer.parseInt(table.getValueAt(index, 0).toString());
                Menu menu = null;
                try {
                    menu = service.load(menuId);
                } catch (ServiceException e1) {
                    Message.show(e1);
                }
                parentMenu = menu;
                loadMenus(menu);
                open.setVisible(false);
                back.setVisible(true);
                this.repaint();
            } else {
                JOptionPane.showMessageDialog(null, "Select a menu first.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (trigger.getCommand().equals(ButtonEnum.BACK.getCommand())) {
            if (cancel != null) {
                cancel.doClick();
            }
            parentMenu = null;
            loadMenus();
            open.setVisible(true);
            back.setVisible(false);
            this.repaint();
        }
    }
}
