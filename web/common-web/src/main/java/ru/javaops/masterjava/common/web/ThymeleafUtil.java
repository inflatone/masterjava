package ru.javaops.masterjava.common.web;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;

public class ThymeleafUtil {
    private ThymeleafUtil() {
    }

    public static TemplateEngine getTemplateEngine(ServletContext context) {
        final var templateResolver = new ServletContextTemplateResolver(context);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheTTLMs(1000L);
        final var engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver);
        return engine;
    }
}
