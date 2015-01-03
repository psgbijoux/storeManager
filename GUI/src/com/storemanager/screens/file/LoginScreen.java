package com.storemanager.screens.file;

import com.storemanager.components.AbstractPanel;
import com.storemanager.components.ImageButton;
import com.storemanager.components.Window;
import com.storemanager.models.User;
import com.storemanager.service.ServiceLocator;
import com.storemanager.service.ServiceName;
import com.storemanager.service.UserService;
import com.storemanager.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

public class LoginScreen extends AbstractPanel {
    private StoreLogger logger = StoreLogger.getInstance(LoginScreen.class);
    private Window baseWindow;
    private JTextField username;
    private JPasswordField password;
    private ImageButton loginBtn, cancel;

    public LoginScreen(Window baseWindow) {
        this.baseWindow = baseWindow;
        this.setSize(400, 200);
        this.setBackground(Color.gray);
        this.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(135, 50, 100, 30);
        this.add(userLabel);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(135, 80, 100, 30);
        this.add(passwordLabel);

        username = new JTextField("");
        username.setBounds(205, 50, 150, 25);
        username.addKeyListener(this);
        this.add(username);

        password = new JPasswordField("");
        password.setBounds(205, 80, 150, 25);
        password.addKeyListener(this);
        this.add(password);

        loginBtn = new ImageButton(ButtonEnum.LOGIN, this);
        loginBtn.setLocation(150, 130);
        loginBtn.setSize(ButtonSizeEnum.DEFAULT.getSize());
        this.add(loginBtn);

        cancel = new ImageButton(ButtonEnum.CANCEL, this);
        cancel.setLocation(270, 130);
        cancel.setSize(ButtonSizeEnum.DEFAULT.getSize());
        this.add(cancel);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            File img = new File(ImagePath.LOGIN_LOGO.getImagePath());
            BufferedImage image = ImageIO.read(img);
            g.drawImage(image, 10, 50, null);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ImageButton trigger = (ImageButton) e.getSource();
        if (trigger.getCommand().equals(ButtonEnum.CANCEL.getCommand())) {
            baseWindow.closeScreen();
        }
        if (trigger.getCommand().equals(ButtonEnum.LOGIN.getCommand())) {
            StringBuilder sb = new StringBuilder();
            sb.append(FieldValidator.validateStringField("Username", username.getText()));
            sb.append(FieldValidator.validateStringField("Password", password.getText()));
            if (sb.length() > 0) {
                JOptionPane.showMessageDialog(null, sb.toString(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            //Do login here
            baseWindow.showMessage("Logging in...");
            UserService service = ServiceLocator.getService(ServiceName.USER_SERVICE);

            User user = null;
            try {
                user = service.login(username.getText(), password.getText());
            } catch (ServiceException e1) {
                Message.show(e1);
            }
            baseWindow.setLoggedUser(user);
            baseWindow.hideMessage();
            baseWindow.closeScreen();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            loginBtn.doClick();
        }
    }
}
