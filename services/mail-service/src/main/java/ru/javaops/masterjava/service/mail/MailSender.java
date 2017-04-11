package ru.javaops.masterjava.service.mail;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.*;
import ru.javaops.masterjava.config.Configs;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.EmailDao;

import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * gkislin
 * 15.11.2016
 */
@Slf4j
public class MailSender {
    static {
        Config db = Configs.getConfig("persist.conf", "db");
        DBIProvider.init(() -> {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("PostgreSQL driver not found", e);
            }
            return DriverManager.getConnection(db.getString("url"), db.getString("user"), db.getString("password"));
//            return DriverManager.getConnection("jdbc:postgresql://localhost:5432/masterjava","user", "password");
        });
    }
    private static EmailDao emailDao = DBIProvider.getDao(EmailDao.class);

    static void sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body) {
        log.info("Send mail to \'" + to + "\' cc \'" + cc + "\' subject \'" + subject + (log.isDebugEnabled() ? "\nbody=" + body : ""));

        Config config = Configs.getConfig("service.conf", "mail");
        String host = config.getString("host");
        int port = config.getInt("port");
        String username = config.getString("username");
        String password = config.getString("password");
        boolean isSSL = config.getBoolean("useSSL");
        boolean isTLS = config.getBoolean("useTLS");
        boolean debug = config.getBoolean("debug");
        String fromName = config.getString("fromName");

        try {
            Email email = new HtmlEmail();
            email.setHostName(host);
            email.setSslSmtpPort(String.valueOf(port));
            email.setAuthenticator(new DefaultAuthenticator(username, password));
            email.setSSLOnConnect(isSSL);
            email.setStartTLSEnabled(isTLS);
            email.setFrom(username, fromName);
            email.setSubject(subject);
            email.setSSLCheckServerIdentity(isSSL);
            email.setDebug(debug);
            email.setMsg(body);
            for (Addressee addressee : to)
                email.addTo(addressee.getEmail());
            for (Addressee addressee : cc)
                email.addCc(addressee.getEmail());
            String result = email.send();
//            String result = "<1079754997.0.1491893820041.JavaMail.mikhail@developer3>";

            ru.javaops.masterjava.persist.model.Email emailToDB = new ru.javaops.masterjava.persist.model.Email(fromName, subject, body, result, LocalDateTime.now());
            log.info("Insert email: " + emailToDB);
            emailDao.insert(emailToDB);
        } catch (EmailException e) {
            log.error(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
