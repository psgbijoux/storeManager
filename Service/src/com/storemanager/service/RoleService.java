package com.storemanager.service;

import com.storemanager.dao.RoleDAO;
import com.storemanager.dao.impl.RoleDAOImpl;
import com.storemanager.models.Role;
import com.storemanager.util.DAOException;
import com.storemanager.util.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService implements StoreService {
    private RoleDAO daoImpl;

    @Autowired
    public RoleService(RoleDAOImpl daoImpl) throws ServiceException {
        this.daoImpl = daoImpl;
    }

    public boolean add(Role role) throws ServiceException {
        try {
            return daoImpl.store(role);
        } catch (DAOException e) {
            throw new ServiceException("Error adding role: " + role.getName());
        }
    }

    public boolean update(Role role) throws ServiceException {
        try {
            return daoImpl.store(role);
        } catch (DAOException e) {
            throw new ServiceException("Error updating role: " + role.getName());
        }
    }

    public boolean delete(Role role) throws ServiceException {
        try {
            return daoImpl.delete(role);
        } catch (DAOException e) {
            throw new ServiceException("Error deleting role: " + role.getName());
        }
    }

    public Role load(int roleId) throws ServiceException {
        try {
            return daoImpl.loadRole(roleId);
        } catch (DAOException e) {
            throw new ServiceException("Error loading role with id: " + roleId);
        }
    }

    public List<Role> getRoles() throws ServiceException {
        try {
            return daoImpl.getRoles();
        } catch (DAOException e) {
            throw new ServiceException("Error loading roles.");
        }
    }
}
