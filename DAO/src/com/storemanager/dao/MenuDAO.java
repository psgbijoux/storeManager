package com.storemanager.dao;

import com.storemanager.models.Menu;
import com.storemanager.util.DAOException;

import java.util.List;

public interface MenuDAO {
    public boolean store(Menu menu) throws DAOException;

    public boolean delete(Menu menu) throws DAOException;

    public Menu getFileMenu() throws DAOException;

    public List<Menu> getMenuList(int roleId) throws DAOException;

    public List<Menu> getMenuList() throws DAOException;

    public List<Menu> getSubMenuList(int menuId) throws DAOException;

    public Menu loadMenu(int menuId) throws DAOException;
}
