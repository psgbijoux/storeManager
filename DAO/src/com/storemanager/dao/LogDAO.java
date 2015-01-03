package com.storemanager.dao;

public interface LogDAO {

    public void error(String username, String app, String message);

    public void error(String app, String message);

    public void info(String username, String app, String message);

    public void info(String app, String message);
}
