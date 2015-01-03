package com.storemanager.dao.impl;

import com.storemanager.dao.DAO;
import com.storemanager.dao.RoleDAO;
import com.storemanager.models.Role;
import com.storemanager.util.DAOException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class RoleDAOImpl extends DAO implements RoleDAO {

    public RoleDAOImpl() {
    }

    public boolean store(Role role) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Transaction tx = session.beginTransaction();
            session.saveOrUpdate(role);
            tx.commit();
            //log event
            LOGGER.info(this.getClass().getName(), "Updated role: " + role.getName());
            return true;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public boolean delete(Role role) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Transaction tx = session.beginTransaction();
            session.delete(role);
            tx.commit();
            //log event
            LOGGER.info(this.getClass().getName(), "Deleted roleId: " + role.getId());
            return true;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public List<Role> getRoles() throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            List<Role> result = (List<Role>) session.createCriteria(Role.class).list();
            return result;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public Role loadRole(int roleId) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Role result = (Role) session.createCriteria(Role.class).add(Restrictions.eq("id", roleId)).uniqueResult();
            return result;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }
}
