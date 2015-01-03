package com.storemanager.dao.impl;

import com.storemanager.dao.DAO;
import com.storemanager.dao.LogDAO;
import com.storemanager.models.Log;
import com.storemanager.util.LogEnum;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Date;

public class LogDAOImpl extends DAO implements LogDAO {

    public LogDAOImpl() {
    }

    public void logError(String username, LogEnum level, String app, String message, Date addDate) {
        final Session session = getSessionFactory().openSession();
        try {
            Transaction tx = session.beginTransaction();
            Log log = new Log();
            log.setAddDate(addDate);
            log.setApp(app);
            log.setMessage(message);
            log.setType(level.toString());
            log.setUsername(username);
            session.save(log);
            tx.commit();
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public void error(String username, String app, String message) {
        logError(username, LogEnum.ERROR, app, message, new Date());
    }

    public void error(String app, String message) {
        logError("unknown", LogEnum.ERROR, app, message, new Date());
    }

    public void info(String username, String app, String message) {
        logError(username, LogEnum.INFO, app, message, new Date());
    }

    public void info(String app, String message) {
        logError("unknown", LogEnum.INFO, app, message, new Date());
    }
}
