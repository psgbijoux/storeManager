package com.storemanager.service;

import com.storemanager.dao.MenuDAO;
import com.storemanager.dao.impl.MenuDAOImpl;
import com.storemanager.models.Menu;
import com.storemanager.models.User;
import com.storemanager.util.DAOException;
import com.storemanager.util.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService implements StoreService {
    private MenuDAO daoImpl;

    @Autowired
    public MenuService(MenuDAOImpl daoImpl) {
        this.daoImpl = daoImpl;
    }

    public Menu loadFileMenu() throws ServiceException {
        try {
            return daoImpl.getFileMenu();
        } catch (DAOException e) {
            throw new ServiceException("Error loading file menu.");
        }
    }

    public List<Menu> getMenuList(User user) throws ServiceException {
        try {
            return daoImpl.getMenuList(user.getRoleId());
        } catch (DAOException e) {
            throw new ServiceException("Error load menu list for user: " + user.getUsername());
        }
    }

    public List<Menu> getMenuList(int roleId) throws ServiceException {
        try {
            return daoImpl.getMenuList(roleId);
        } catch (DAOException e) {
            throw new ServiceException("Error loading menu list for role: " + roleId);
        }
    }

    public List<Menu> getMenuList() throws ServiceException {
        try {
            return daoImpl.getMenuList();
        } catch (DAOException e) {
            throw new ServiceException("Error loading menu list.");
        }
    }

    public List<Menu> getSubMenuList(Menu menu) throws ServiceException {
        try {
            return daoImpl.getSubMenuList(menu.getId());
        } catch (DAOException e) {
            throw new ServiceException("Error loading submenu list for menu: " + menu.getName());
        }
    }

    public boolean add(Menu menu) throws ServiceException {
        try {
            return daoImpl.store(menu);
        } catch (DAOException e) {
            throw new ServiceException("Error adding menu: " + menu.getName());
        }
    }

    public boolean update(Menu menu) throws ServiceException {
        try {
            return daoImpl.store(menu);
        } catch (DAOException e) {
            throw new ServiceException("Error updating menu: " + menu.getName());
        }
    }

    public boolean delete(Menu menu) throws ServiceException {
        try {
            return daoImpl.delete(menu);
        } catch (DAOException e) {
            throw new ServiceException("Error deleting menu: " + menu.getName());
        }
    }

    public Menu load(int menuId) throws ServiceException {
        try {
            return daoImpl.loadMenu(menuId);
        } catch (DAOException e) {
            throw new ServiceException("Error loading menu with id: " + menuId);
        }
    }
}
