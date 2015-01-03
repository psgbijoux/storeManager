package com.storemanager.service;

import com.storemanager.dao.XrefMenuRoleDAO;
import com.storemanager.dao.impl.XrefMenuRoleDAOImpl;
import com.storemanager.models.XrefMenuRole;
import com.storemanager.util.DAOException;
import com.storemanager.util.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class XrefMenuRoleService implements StoreService {
    private XrefMenuRoleDAO daoImpl;

    @Autowired
    public XrefMenuRoleService(XrefMenuRoleDAOImpl daoImpl) {
        this.daoImpl = daoImpl;
    }

    public boolean add(XrefMenuRole xrefMenuRole) throws ServiceException {
        try {
            return daoImpl.addNew(xrefMenuRole);
        } catch (DAOException e) {
            throw new ServiceException("Error adding role-menu cross-reference: " + xrefMenuRole);
        }
    }

    public boolean delete(XrefMenuRole xrefMenuRole) throws ServiceException {
        try {
            return daoImpl.delete(xrefMenuRole);
        } catch (DAOException e) {
            throw new ServiceException("Error deleting role-menu cross-reference: " + xrefMenuRole);
        }
    }

    public XrefMenuRole load(int menuId, int roleId) throws ServiceException {
        try {
            return daoImpl.loadMenuRole(menuId, roleId);
        } catch (DAOException e) {
            throw new ServiceException("Error loading role-menu cross-reference: menuId=" + menuId + ", roleId=" + roleId);
        }
    }

    public XrefMenuRole load(int id) throws ServiceException {
        try {
            return daoImpl.loadMenuRole(id);
        } catch (DAOException e) {
            throw new ServiceException("Error adding role-menu cross-reference with id: " + id);
        }
    }
}
