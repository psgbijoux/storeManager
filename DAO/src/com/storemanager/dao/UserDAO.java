package com.storemanager.dao;

import com.storemanager.models.User;
import com.storemanager.util.DAOException;

import java.util.List;

public interface UserDAO {

    public boolean store(User user) throws DAOException;

    public boolean delete(User user) throws DAOException;

    public User login(String username, String password) throws DAOException;

    public List<User> getUserList() throws DAOException;

    public User load(int userId) throws DAOException;
}
