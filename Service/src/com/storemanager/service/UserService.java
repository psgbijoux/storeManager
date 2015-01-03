package com.storemanager.service;

import com.storemanager.dao.UserDAO;
import com.storemanager.dao.impl.UserDAOImpl;
import com.storemanager.models.User;
import com.storemanager.util.DAOException;
import com.storemanager.util.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements StoreService {
    private UserDAO dao;

    @Autowired
    public UserService(UserDAOImpl userDAO) {
        this.dao = userDAO;
    }

    public boolean addUser(User user) throws ServiceException {
        try {
            return dao.store(user);
        } catch (DAOException e) {
            throw new ServiceException("Error adding user: " + user.getUsername());
        }
    }

    public boolean deleteUser(User user) throws ServiceException {
        try {
            return dao.delete(user);
        } catch (DAOException e) {
            throw new ServiceException("Error deleting user: " + user.getUsername());
        }
    }

    public boolean updateUser(User user) throws ServiceException {
        try {
            return dao.store(user);
        } catch (DAOException e) {
            throw new ServiceException("Error updating user: " + user.getUsername());
        }
    }

    public User login(String username, String password) throws ServiceException {
        try {
            return dao.login(username, password);
        } catch (DAOException e) {
            throw new ServiceException("Error logging in user: " + username);
        }
    }

    public List<User> getUserList() throws ServiceException {
        try {
            return dao.getUserList();
        } catch (DAOException e) {
            throw new ServiceException("Error loading user list.");
        }
    }

    public User load(int userId) throws ServiceException {
        try {
            return dao.load(userId);
        } catch (DAOException e) {
            throw new ServiceException("Error loading user with id: " + userId);
        }
    }
}
