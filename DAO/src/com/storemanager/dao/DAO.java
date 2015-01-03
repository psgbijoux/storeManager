package com.storemanager.dao;

import com.storemanager.dao.impl.LogDAOImpl;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public abstract class DAO {
    protected static final LogDAOImpl LOGGER = new LogDAOImpl();
    private static Configuration configuration;
    private static SessionFactory sessionFactory = null;
    private static ServiceRegistry serviceRegistry = null;
    private static boolean newSessionFactory = true;

    static {
        String ip = null;
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(new File("settings.properties")));
            ip = properties.getProperty("IP");
        } catch (IOException e) {
            ip = "192.168.5.151";
        }
        try {
            configuration = new Configuration();
            configuration.setProperty("hibernate.connection.url", "jdbc:mysql://" + ip + ":3306/storemanager");
            configuration.setProperty("hibernate.connection.password", "P@ssW0rd");
            configuration.configure();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }


    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null || sessionFactory.isClosed() || newSessionFactory) {
            try {
                serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Throwable ex) {
                System.err.println("Failed to create sessionFactory object." + ex);
                throw new ExceptionInInitializerError(ex);
            }
        }
        return sessionFactory;
    }

    public static void closeSessionFactory() {
        if (newSessionFactory) {
            sessionFactory.close();
        }
    }

    public static void enableNewSessionFactory() {
        newSessionFactory = true;
    }

    public static void disableNewSessionFactory() {
        newSessionFactory = false;
    }
}
