package com.storemanager.dao.impl;

import com.storemanager.dao.DAO;
import com.storemanager.dao.XrefMenuRoleDAO;
import com.storemanager.models.XrefMenuRole;
import com.storemanager.util.DAOException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

public class XrefMenuRoleDAOImpl extends DAO implements XrefMenuRoleDAO {

    public XrefMenuRoleDAOImpl() {
    }

    public boolean addNew(XrefMenuRole xrefMenuRole) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Transaction tx = session.beginTransaction();
            session.save(xrefMenuRole);
            tx.commit();
            //log event
            LOGGER.info(this.getClass().getName(), "Created xrefMenuRole");
            return true;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public boolean delete(XrefMenuRole xrefMenuRole) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Transaction tx = session.beginTransaction();
            session.delete(xrefMenuRole);
            tx.commit();
            //log event
            LOGGER.info(this.getClass().getName(), "Deleted xrefMenuRole: " + xrefMenuRole.getId());
            return true;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public XrefMenuRole loadMenuRole(int id) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            XrefMenuRole result = (XrefMenuRole) session.createCriteria(XrefMenuRole.class).add(Restrictions.eq("id", id)).uniqueResult();
            return result;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public XrefMenuRole loadMenuRole(int menuId, int roleId) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            XrefMenuRole result = (XrefMenuRole) session.createCriteria(XrefMenuRole.class).add(Restrictions.eq("menuId", menuId)).add(Restrictions.eq("roleId", roleId)).uniqueResult();
            return result;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }
}
