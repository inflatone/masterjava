package ru.javaops.masterjava.web;

import com.typesafe.config.Config;
import org.slf4j.event.Level;
import ru.javaops.masterjava.ExceptionType;
import ru.javaops.masterjava.config.Configs;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.Handler;
import java.net.URL;

import static ru.javaops.masterjava.web.handler.SoapLoggingHandlers.ClientHandler;

public class WsClient<T> {
    private static Config HOSTS;

    private final Class<T> serviceClass;
    private final Service service;
    private HostConfig hostConfig;

    public static class HostConfig {
        public final String endpoint;
        public final Level serverDebugLevel;
        public final String user;
        public final String password;
        public final String authHeader;
        public final ClientHandler clientLoggingHandler;

        public HostConfig(Config config, String endpointAdress) {
            endpoint = config.getString("endpoint") + endpointAdress;
            serverDebugLevel = config.getEnum(Level.class, "server.debugLevel");
            // https://github.com/typesafehub/config/issues/282

            if (!config.getIsNull("user") && !config.getIsNull("password")) {
                user = config.getString("user");
                password = config.getString("password");
                authHeader = AuthUtil.encodeBasicAuthHeader(user, password);
            } else {
                user = password = authHeader = null;
            }
            clientLoggingHandler = config.getIsNull("client.debugLevel") ? null
                    : new ClientHandler(config.getEnum(Level.class, "client.debugLevel"));
        }

        public boolean hasAuthorization() {
            return authHeader != null;
        }

        public boolean hasHandler() {
            return clientLoggingHandler != null;
        }
    }

    static {
        HOSTS = Configs.getConfig("hosts.conf", "hosts");
    }

    public WsClient(URL wsdUrl, QName qName, Class<T> serviceClass) {
        this.serviceClass = serviceClass;
        this.service = Service.create(wsdUrl, qName);
    }

    public void init(String host, String endpointAddress) {
        this.hostConfig = new HostConfig(
                HOSTS.getConfig(host).withFallback(Configs.getConfig("defaults.conf")), endpointAddress);
    }

    public HostConfig getHostConfig() {
        return hostConfig;
    }

    // Post is not thread-safe (http://stackoverflow.com/a/10601916/548473)
    public T getPort(WebServiceFeature... features) {
        T port = service.getPort(serviceClass, features);
        var provider = (BindingProvider) port;
        var requestContext = provider.getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, hostConfig.endpoint);
        if (hostConfig.hasAuthorization()) {
            setAuth(port, hostConfig.user, hostConfig.password);
        }
        if (hostConfig.hasHandler()) {
            setHandler(port, hostConfig.clientLoggingHandler);
        }
        return port;
    }

    public static <T> void setAuth(T port, String user, String password) {
        var requestContext = ((BindingProvider) port).getRequestContext();
        requestContext.put(BindingProvider.USERNAME_PROPERTY, user);
        requestContext.put(BindingProvider.PASSWORD_PROPERTY, password);
    }

    public static <T> void setHandler(T port, Handler handler) {
        var binding = ((BindingProvider) port).getBinding();
        var handlers = binding.getHandlerChain();
        handlers.add(handler);
        binding.setHandlerChain(handlers);
    }

    public static WebStateException getWebStateException(Throwable t, ExceptionType type) {
        return (t instanceof WebStateException) ? (WebStateException) t : new WebStateException(t, type);
    }
}
