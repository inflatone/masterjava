package ru.javaops.masterjava.web;

import com.typesafe.config.Config;
import ru.javaops.masterjava.ExceptionType;
import ru.javaops.masterjava.config.Configs;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceFeature;
import java.net.URL;
import java.util.Map;

public class WsClient<T> {
    private static Config HOSTS;

    private final Class<T> serviceClass;
    private final Service service;
    private String endpointAddress;

    static {
        HOSTS = Configs.getConfig("hosts.conf", "hosts");
    }

    public WsClient(URL wsdUrl, QName qName, Class<T> serviceClass) {
        this.serviceClass = serviceClass;
        this.service = Service.create(wsdUrl, qName);
    }

    public void init(String host, String endpointAddress) {
        this.endpointAddress = HOSTS.getString(host) + endpointAddress;
    }

    // Post is not thread-safe (http://stackoverflow.com/a/10601916/548473)
    public T getPort(WebServiceFeature... features) {
        T port = service.getPort(serviceClass, features);
        BindingProvider provider = (BindingProvider) port;
        Map<String, Object> requestCtx = provider.getRequestContext();
        requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
        return port;
    }

    public static <T> void setAuth(T port, String user, String password) {
        Map<String, Object> reqCtx = ((BindingProvider) port).getRequestContext();
        reqCtx.put(BindingProvider.USERNAME_PROPERTY, user);
        reqCtx.put(BindingProvider.PASSWORD_PROPERTY, password);
    }

    public static WebStateException getWebStateException(Throwable t, ExceptionType type) {
        return (t instanceof WebStateException) ? (WebStateException) t : new WebStateException(t, type);
    }

}
