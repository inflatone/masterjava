package ru.javaops.masterjava.web;


import com.typesafe.config.Config;
import ru.javaops.masterjava.ExceptionType;
import ru.javaops.masterjava.config.Configs;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import java.net.URL;

public class WsClient<T> {
    private static Config HOSTS;

    static {
        HOSTS = Configs.getConfig("hosts.conf", "hosts");
    }

    private final Class<T> serviceClass;
    private final Service service;
    private String endpointAddress;

    public WsClient(URL wsdlUrl, QName qname, Class<T> serviceClass) {
        this.serviceClass = serviceClass;
        this.service = Service.create(wsdlUrl, qname);
    }

    public static WebStateException getWebStateException(Throwable t, ExceptionType type) {
        return t instanceof WebStateException
                ? (WebStateException) t
                : new WebStateException(t, type);
    }

    public void init(String host, String endpointAddress) {
        this.endpointAddress = HOSTS.getString(host) + endpointAddress;
    }

    public T getPort() {
        T port = service.getPort(serviceClass);
        ((BindingProvider) port).getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointAddress
        );
        return port;
    }

}
