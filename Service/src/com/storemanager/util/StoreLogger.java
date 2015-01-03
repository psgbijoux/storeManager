package com.storemanager.util;

import com.storemanager.dao.impl.LogDAOImpl;

public class StoreLogger {
    private static StoreLogger logger;
    private String username;
    private String app;
    private LogDAOImpl daoImpl = new LogDAOImpl();

    private StoreLogger(String username, String app) {
        this.username = username;
        this.app = app;
    }

    public static StoreLogger getInstance(String username, String app) {
        if (logger == null) {
            logger = new StoreLogger(username, app);
            return logger;
        } else {
            return logger;
        }
    }

    public static StoreLogger getInstance(String app) {
        return getInstance("unknown_service", app);
    }

    public static StoreLogger getInstance(Class className) {
        return getInstance("unknown_service", className.getName());
    }

    public static StoreLogger getInstance(String username, Class className) {
        return getInstance(username, className.getName());
    }

    public void error(String app, String message) {
        daoImpl.error(username, app, message);
    }

    public void error(String message) {
        daoImpl.error(username, app, message);
    }

    public void info(String app, String message) {
        daoImpl.info(username, app, message);
    }

    public void info(String message) {
        daoImpl.info(username, app, message);
    }
}
