package ru.javaops.masterjava.service.mail;

import ru.javaops.masterjava.config.Configs;
import ru.javaops.masterjava.persist.DBITestProvider;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Endpoint;
import java.util.List;

public class MailServicePublisher {
    public static void main(String[] args) {
        DBITestProvider.initDBI();
//        Endpoint.publish("http://localhost:8080/mail/mailService", new MailServiceImpl());

        var endpoint = Endpoint.create(new MailServiceImpl());
        List<Source> metadata = List.of(
                new StreamSource(Configs.getConfigFile("wsdl/mailService.wsdl")),
                new StreamSource(Configs.getConfigFile("wsdl/common.xsd"))
        );
        endpoint.setMetadata(metadata);
        endpoint.publish("http://localhost:8080/mail/mailService");
    }
}