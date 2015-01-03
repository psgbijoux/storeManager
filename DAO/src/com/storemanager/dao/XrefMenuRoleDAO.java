package com.storemanager.dao;

import com.storemanager.models.XrefMenuRole;
import com.storemanager.util.DAOException;

public interface XrefMenuRoleDAO {

    public boolean addNew(XrefMenuRole xrefMenuRole) throws DAOException;

    public boolean delete(XrefMenuRole xrefMenuRole) throws DAOException;

    public XrefMenuRole loadMenuRole(int id) throws DAOException;

    public XrefMenuRole loadMenuRole(int menuId, int roleId) throws DAOException;
}
