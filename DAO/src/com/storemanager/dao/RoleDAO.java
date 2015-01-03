package com.storemanager.dao;

import com.storemanager.models.Role;
import com.storemanager.util.DAOException;

import java.util.List;

public interface RoleDAO {

    public boolean store(Role role) throws DAOException;

    public boolean delete(Role role) throws DAOException;

    public List<Role> getRoles() throws DAOException;

    public Role loadRole(int roleId) throws DAOException;
}
