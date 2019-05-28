package ru.javaops.masterjava.webapp.akka;

import ru.javaops.masterjava.akka.AkkaActivator;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AkkaWebappListener implements ServletContextListener {
    public static AkkaActivator akkaActivator;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        akkaActivator = AkkaActivator.start("WebApp", "webapp");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        akkaActivator.shutdown();
    }
}
