package com.storemanager.dao.impl;

import com.storemanager.dao.DAO;
import com.storemanager.dao.UserDAO;
import com.storemanager.models.User;
import com.storemanager.util.DAOException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.Date;
import java.util.List;

public class UserDAOImpl extends DAO implements UserDAO {

    public UserDAOImpl() {
    }

    public boolean store(User user) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Transaction tx = session.beginTransaction();
            session.saveOrUpdate(user);
            tx.commit();
            //log event
            LOGGER.info(this.getClass().getName(), "Updated user: " + user.getUsername());
            return true;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public boolean delete(User user) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Transaction tx = session.beginTransaction();
            session.delete(user);
            tx.commit();
            //log event
            LOGGER.info(this.getClass().getName(), "Deleted userId: " + user.getId());
            return true;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public User login(String username, String password) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            User rez = (User) session.createCriteria(User.class).add(Restrictions.eq("username", username)).add(Restrictions.eq("password", password)).uniqueResult();
            LOGGER.info(this.getClass().getName(), "Login successful for username: " + username + ", at " + new Date());
            return rez;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public List<User> getUserList() throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            List<User> result = (List<User>) session.createCriteria(User.class).list();
            return result;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public User load(int userId) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            User user = (User) session.createCriteria(User.class).add(Restrictions.eq("id", userId)).uniqueResult();
            return user;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }
}
