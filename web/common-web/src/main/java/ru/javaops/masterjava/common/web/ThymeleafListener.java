package ru.javaops.masterjava.common.web;

import org.thymeleaf.TemplateEngine;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import static ru.javaops.masterjava.common.web.ThymeleafUtil.getTemplateEngine;

@WebListener
public class ThymeleafListener implements ServletContextListener {
    public static TemplateEngine engine;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        engine = getTemplateEngine(event.getServletContext());
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {

    }
}
