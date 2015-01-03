package com.storemanager.screens.categories;

import com.storemanager.components.AbstractPanel;
import com.storemanager.components.ImageButton;
import com.storemanager.components.Window;
import com.storemanager.models.Category;
import com.storemanager.service.CategoryService;
import com.storemanager.service.ServiceLocator;
import com.storemanager.service.ServiceName;
import com.storemanager.util.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.List;

public class CategoryScreen extends AbstractPanel {
    private StoreLogger logger = StoreLogger.getInstance(CategoryScreen.class);
    private Window baseWindow;
    private JTree tree;
    private JButton close, add, edit, cancel, save, delete;
    private JPanel editPanel;
    private Category currentCategory, parentCategory;
    private JTextField name, description;


    public CategoryScreen(Window baseWindow) {
        this.baseWindow = baseWindow;
        this.setSize(800, 600);
        this.setBackground(Color.gray);
        this.setLayout(null);

        close = new ImageButton(ButtonEnum.CLOSE, this);
        close.setLocation(670, 530);
        close.setSize(ButtonSizeEnum.DEFAULT.getSize());
        this.add(close);

        //categories tree
        createTree();

        //create buttons
        add = new ImageButton(ButtonEnum.ADD, this);
        add.setLocation(500, 20);
        add.setSize(ButtonSizeEnum.DEFAULT.getSize());
        this.add(add);

        edit = new ImageButton(ButtonEnum.EDIT, this);
        edit.setLocation(500, 70);
        edit.setSize(ButtonSizeEnum.DEFAULT.getSize());
        this.add(edit);

        delete = new ImageButton(ButtonEnum.DELETE, this);
        delete.setLocation(500, 120);
        delete.setSize(ButtonSizeEnum.DEFAULT.getSize());
        this.add(delete);
    }

    public void createTree() {
        CategoryService service = ServiceLocator.getService(ServiceName.CATEGORY_SERVICE);
        try {
            List<Category> categoryList = service.getCategoryList(null);
        } catch (ServiceException e) {
            Message.show(e);
        }
        Category parentC = new Category(0, -1, "Category", null);
        DefaultMutableTreeNode parent = new DefaultMutableTreeNode(parentC, true);
        createTree(parent);
        if (tree != null) {
            this.remove(tree);
        }
        tree = new JTree(parent);
        tree.setBackground(Color.lightGray);
        tree.setBounds(30, 20, 450, 560);
        this.add(tree);
        expandAll(tree, true);
        if (editPanel != null) {
            this.remove(editPanel);
        }
        this.repaint();
    }

    public void createTree(DefaultMutableTreeNode parent) {
        CategoryService service = ServiceLocator.getService(ServiceName.CATEGORY_SERVICE);
        Category parentCategory = (Category) parent.getUserObject();
        List<Category> categoryList = null;
        try {
            categoryList = service.getCategoryList(parentCategory);
        } catch (ServiceException e) {
            Message.show(e);
        }
        for (Category category : categoryList) {
            DefaultMutableTreeNode branch = new DefaultMutableTreeNode(category, true);
            parent.add(branch);
            createTree(branch);
        }
    }

    public void edit(Category category, Category parent) {
        editPanel = new JPanel();
        editPanel.setLayout(null);
        editPanel.setBounds(500, 180, 280, 250);
        editPanel.setBackground(Color.lightGray);

        JLabel parentLabel = new JLabel("Parent: " + parent.getName());
        parentLabel.setBounds(20, 30, 200, 25);
        editPanel.add(parentLabel);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(20, 60, 100, 25);
        editPanel.add(nameLabel);

        name = new JTextField();
        name.setBounds(100, 60, 150, 25);
        editPanel.add(name);

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setBounds(20, 90, 100, 25);
        editPanel.add(descriptionLabel);

        description = new JTextField();
        description.setBounds(100, 90, 150, 25);
        editPanel.add(description);

        cancel = new ImageButton(ButtonEnum.CANCEL, this);
        cancel.setLocation(160, 200);
        cancel.setSize(ButtonSizeEnum.DEFAULT.getSize());
        editPanel.add(cancel);

        save = new ImageButton(ButtonEnum.SAVE, this);
        save.setLocation(40, 200);
        save.setSize(ButtonSizeEnum.DEFAULT.getSize());
        editPanel.add(save);

        if (category != null) {
            name.setText(category.getName());
            description.setText(category.getDescription());
        }
        this.add(editPanel);
        this.repaint();
    }

    public void actionPerformed(ActionEvent e) {
        ImageButton trigger = (ImageButton) e.getSource();
        CategoryService service = ServiceLocator.getService(ServiceName.CATEGORY_SERVICE);

        if (trigger.getCommand().equals(ButtonEnum.CLOSE.getCommand())) {
            baseWindow.closeScreen();
        }
        if (trigger.getCommand().equals(ButtonEnum.ADD.getCommand())) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node != null) {
                Category category = (Category) node.getUserObject();
                currentCategory = null;
                parentCategory = category;
                edit(null, category);
            } else {
                JOptionPane.showMessageDialog(null, "Select the parent category.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (trigger.getCommand().equals(ButtonEnum.DELETE.getCommand())) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node != null) {
                Category category = (Category) node.getUserObject();
                if (category.getId() == 0 && category.getParentId() == -1) {
                    JOptionPane.showMessageDialog(null, "Cannot delete root.");
                    return;
                }
                //delete here
                int response = JOptionPane.showConfirmDialog(null, "Do you want to delete category: " + category.getName(), "Delete", JOptionPane.YES_NO_OPTION);
                if (response == 0) {
                    //delete here
                    try {
                        service.delete(category);
                    } catch (ServiceException e1) {
                        Message.show(e1);
                    }
                    createTree();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Select a category first.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (trigger.getCommand().equals(ButtonEnum.EDIT.getCommand())) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node != null) {
                Category category = (Category) node.getUserObject();
                if (category.getId() == 0 && category.getParentId() == -1) {
                    JOptionPane.showMessageDialog(null, "Cannot edit root.");
                    return;
                }
                DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
                if (parentNode != null) {
                    Category parent = (Category) parentNode.getUserObject();
                    currentCategory = category;
                    parentCategory = parent;

                    edit(category, parent);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Select a category first.", "Validation Error", JOptionPane.ERROR_MESSAGE);
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
            if (parentCategory != null) {
                if (currentCategory != null) {
                    //edit
                    Category category = currentCategory;
                    category.setName(name.getText());
                    category.setDescription(description.getText());
                    try {
                        service.update(category);
                    } catch (ServiceException e1) {
                        Message.show(e1);
                    }
                    createTree();
                } else {
                    //add
                    Category category = new Category(parentCategory.getId(), name.getText(), description.getText());
                    try {
                        service.add(category);
                    } catch (ServiceException e1) {
                        Message.show(e1);
                    }
                    createTree();
                }
            }
        }
    }

    public static void expandAll(JTree tree, boolean expand) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();

        // Traverse tree from root
        expandAll(tree, new TreePath(root), expand);
    }

    private static void expandAll(JTree tree, TreePath parent, boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }

        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }
}
