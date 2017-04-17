package ru.javaops.masterjava.service.mail;

import com.google.common.io.Resources;
import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.config.Configs;
import ru.javaops.masterjava.web.WsClient;

import javax.xml.namespace.QName;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

@Slf4j
public class MailWSClient {
    private static final WsClient<MailService> WS_CLIENT;

    static {
        File file = Configs.getFile("wsdl/mailService.wsdl");
        URL url;
        try {
            url = file.toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            url = Resources.getResource("wsdl/mailService.wsdl");
        }
        WS_CLIENT = new WsClient<MailService>(url,
                new QName("http://mail.javaops.ru/", "MailServiceImplService"),
                MailService.class);

        WS_CLIENT.init("mail", "/mail/mailService?wsdl");
    }


    public static void sendMail(final Set<Addressee> to, final Set<Addressee> cc, final String subject, final String body) {
        log.info("Send mail to '" + to + "' cc '" + cc + "' subject '" + subject + (log.isDebugEnabled() ? "\nbody=" + body : ""));
        WS_CLIENT.getPort().sendToGroup(to, cc, subject, body);
    }
}
