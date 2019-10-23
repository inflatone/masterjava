package ru.javaops.masterjava.service.mail;

import com.typesafe.config.Config;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import ru.javaops.masterjava.config.Configs;

import javax.mail.Authenticator;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MailConfig {
    private static final MailConfig INSTANCE = new MailConfig(Configs.getConfig("mail.conf", "mail"));

    private final String host;
    private final int port;
    private final boolean useSSL;
    private final boolean useTLS;
    private final boolean debug;
    private final String username;
    private final Authenticator auth;
    private final String fromName;

    private MailConfig(Config config) {
        this.host = config.getString("host");
        this.port = config.getInt("port");
        this.useSSL = config.getBoolean("useSSL");
        this.useTLS = config.getBoolean("useTLS");
        this.debug = config.getBoolean("debug");
        this.username = config.getString("username");
        this.auth = new DefaultAuthenticator(username, config.getString("password"));
        this.fromName = config.getString("fromName");
    }

    public <T extends Email> T prepareEmail(T email) throws EmailException {
        email.setFrom(username, fromName);
        email.setHostName(host);
        if (useSSL) {
            email.setSslSmtpPort(String.valueOf(port));
        } else {
            email.setSmtpPort(port);
        }
        email.setSSLOnConnect(useSSL);
        email.setStartTLSEnabled(useTLS);
        email.setDebug(debug);
        email.setAuthenticator(auth);
        email.setCharset(UTF_8.name());
        return email;
    }

    public static HtmlEmail createHtmlEmail() throws EmailException {
        return INSTANCE.prepareEmail(new HtmlEmail());
    }

    @Override
    public String toString() {
        return "\nhost='" + host + '\'' +
                "\nport=" + port +
                "\nuseSSL=" + useSSL +
                "\nuseTLS=" + useTLS +
                "\ndebug=" + debug +
                "\nusername='" + username + '\'' +
                "\nfromName='" + fromName + '\'';
    }


}
