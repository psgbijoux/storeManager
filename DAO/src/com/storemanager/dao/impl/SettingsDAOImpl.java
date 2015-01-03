package com.storemanager.dao.impl;

import com.storemanager.dao.DAO;
import com.storemanager.dao.SettingsDAO;
import com.storemanager.models.Settings;
import com.storemanager.util.DAOException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class SettingsDAOImpl extends DAO implements SettingsDAO {

    public boolean store(Settings settings) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Transaction tx = session.beginTransaction();
            session.update(settings);
            tx.commit();
            //log event
            LOGGER.info(this.getClass().getName(), "Updated setting: " + settings.getName());
            return true;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    @Override
    public boolean delete(Settings settings) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Transaction tx = session.beginTransaction();
            session.delete(settings);
            tx.commit();
            //log event
            LOGGER.info(this.getClass().getName(), "Deleted setting: " + settings.getId());
            return true;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    @Override
    public List<Settings> getSettings() throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            List<Settings> result = (List<Settings>) session.createCriteria(Settings.class).list();
            return result;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    @Override
    public Settings loadSettings(int id) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Settings settings = (Settings) session.createCriteria(Settings.class).add(Restrictions.eq("id", id)).uniqueResult();
            return settings;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    @Override
    public Settings loadSettings(String name) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Settings settings = (Settings) session.createCriteria(Settings.class).add(Restrictions.eq("name", name)).uniqueResult();
            return settings;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }
}
