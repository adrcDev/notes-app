package com.awesometodo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringApplicationContext implements ApplicationContextAware {
    private static ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        applicationContext=ctx;
    }

    public static <T> T getBean(Class<T> type) {
        return applicationContext.getBean(type);
    }
}
