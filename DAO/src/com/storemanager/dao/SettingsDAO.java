package com.storemanager.dao;

import com.storemanager.models.Settings;
import com.storemanager.util.DAOException;

import java.util.List;

public interface SettingsDAO {
    public boolean store(Settings settings) throws DAOException;

    public boolean delete(Settings settings) throws DAOException;

    public List<Settings> getSettings() throws DAOException;

    public Settings loadSettings(int id) throws DAOException;

    public Settings loadSettings(String name) throws DAOException;
}
