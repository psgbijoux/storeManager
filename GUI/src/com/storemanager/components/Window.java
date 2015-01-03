package com.storemanager.components;

import com.storemanager.listeners.MenuListener;
import com.storemanager.models.Menu;
import com.storemanager.models.User;
import com.storemanager.screens.file.NotAutorized;
import com.storemanager.service.MenuService;
import com.storemanager.service.ServiceLocator;
import com.storemanager.service.ServiceName;
import com.storemanager.util.ImagePath;
import com.storemanager.util.Message;
import com.storemanager.util.ServiceException;
import com.storemanager.util.StoreLogger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class Window extends JFrame {
    private StoreLogger logger = StoreLogger.getInstance(Window.class);
    private JPanel currentScreen;
    private JMenu message, user;
    private User loggedUser;

    public Window() {
        this.createBaseWindow();
        this.repaint();
        this.validate();

    }

    private void createBaseWindow() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setUndecorated(true);
        this.setResizable(false);
        this.setVisible(true);
        try {
            createMenu();
        } catch (ServiceException e) {
            Message.show(e);
        }
        setContent(new NotAutorized(this));
    }

    private void createMenu() throws ServiceException {
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);
        MenuListener menuListener = new MenuListener(this);
        if (loggedUser == null) {
            //File menu
            MenuService service = ServiceLocator.getService(ServiceName.MENU_SERVICE);
            Menu fileMenuModel = service.loadFileMenu();
            if (fileMenuModel == null) {
                JOptionPane.showMessageDialog(null, "Internal error. The application will close.");
                System.exit(0);
            }
            ImageMenu fileMenu = new ImageMenu(fileMenuModel);
            menuBar.add(fileMenu);

            List<Menu> list = service.getSubMenuList(fileMenuModel);
            for (Menu menu : list) {
                MenuItem login = new MenuItem(menu, menuListener);
                fileMenu.add(login);
            }
            user = new JMenu("Not logged in.");
        } else {
            MenuService service = ServiceLocator.getService(ServiceName.MENU_SERVICE);
            List<Menu> menuList = service.getMenuList(loggedUser);
            for (Menu menu : menuList) {
                ImageMenu menuObj = new ImageMenu(menu);
                menuBar.add(menuObj);
                List<Menu> subMenuList = service.getSubMenuList(menu);
                for (Menu subMenu : subMenuList) {
                    MenuItem submenuObj = new MenuItem(subMenu, menuListener);
                    menuObj.add(submenuObj);
                }
            }
            user = new JMenu("User: " + loggedUser.getUsername());
        }
        //logged in user information
        menuBar.add(Box.createHorizontalGlue());
        message = new JMenu("Processing...");
        try {
            File img = new File(ImagePath.BUSY.getImagePath());
            BufferedImage image = ImageIO.read(img);
            message.setIcon(new ImageIcon(image));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        message.enable(false);
        menuBar.add(message);

        user.enable(false);
        menuBar.add(user);

        hideMessage();
    }

    public void setContent(JPanel panel) {
        if (currentScreen != null) {
            this.remove(currentScreen);
        }
        currentScreen = panel;
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        cons.fill = GridBagConstraints.CENTER;
        cons.ipady = currentScreen.getHeight();
        cons.ipadx = currentScreen.getWidth();
        layout.setConstraints(currentScreen, cons);
        this.setLayout(layout);
        this.add(panel);
        this.repaint();
        this.validate();
    }

    public void closeScreen() {
        if (currentScreen != null) {
            this.remove(currentScreen);
        }
        if (loggedUser == null) {
            setContent(new NotAutorized(this));
        } else {

        }
        this.repaint();
        this.validate();
    }

    public void showMessage(String msg) {
        message.setText(msg);
        message.setVisible(true);
    }

    public void hideMessage() {
        message.setVisible(false);
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
        try {
            createMenu();
        } catch (ServiceException e) {
            Message.show(e);
        }
        this.repaint();
    }
}
