package com.storemanager.service;

import com.storemanager.util.StoreLogger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServiceLocator {
    private static final StoreLogger logger = StoreLogger.getInstance(ServiceLocator.class);

    public static <T extends StoreService> T getService(ServiceName serviceName) {
        ApplicationContext context = new ClassPathXmlApplicationContext("/Service-config.xml");
        StoreService service = (StoreService) context.getBean(serviceName.getBeanName());
        return (T) service;
    }
}
