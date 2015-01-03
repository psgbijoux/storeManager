package com.storemanager.service;

import com.storemanager.dao.SettingsDAO;
import com.storemanager.dao.impl.SettingsDAOImpl;
import com.storemanager.models.Settings;
import com.storemanager.util.DAOException;
import com.storemanager.util.ServiceException;
import com.storemanager.util.SettingsEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingsService implements StoreService {
    private SettingsDAO dao;

    @Autowired
    public SettingsService(SettingsDAOImpl dao) throws ServiceException {
        this.dao = dao;
    }

    public boolean addNew(Settings settings) throws ServiceException {
        try {
            return dao.store(settings);
        } catch (DAOException e) {
            throw new ServiceException("Error adding setting: " + settings.getName());
        }
    }

    public boolean update(Settings settings) throws ServiceException {
        try {
            return dao.store(settings);
        } catch (DAOException e) {
            throw new ServiceException("Error updating setting: " + settings.getName());
        }
    }

    public boolean delete(Settings settings) throws ServiceException {
        try {
            return dao.delete(settings);
        } catch (DAOException e) {
            throw new ServiceException("Error deleting setting: " + settings.getName());
        }
    }

    public List<Settings> getSettings() throws ServiceException {
        try {
            return dao.getSettings();
        } catch (DAOException e) {
            throw new ServiceException("Error loading settings.");
        }
    }

    public Settings loadSettings(int id) throws ServiceException {
        try {
            return dao.loadSettings(id);
        } catch (DAOException e) {
            throw new ServiceException("Error loading setting with id: " + id);
        }
    }

    public Settings loadSettings(String name) throws ServiceException {
        try {
            return dao.loadSettings(name);
        } catch (DAOException e) {
            throw new ServiceException("Error loading setting: " + name);
        }
    }

    public Settings loadSettings(SettingsEnum settingsEnum) throws ServiceException {
        try {
            return dao.loadSettings(settingsEnum.name());
        } catch (DAOException e) {
            throw new ServiceException("Error loading setting: " + settingsEnum.name());
        }
    }
}
