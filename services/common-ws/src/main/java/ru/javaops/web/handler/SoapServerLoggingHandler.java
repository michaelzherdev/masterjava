package ru.javaops.web.handler;

import com.typesafe.config.Config;
import org.slf4j.event.Level;
import ru.javaops.masterjava.config.Configs;

public class SoapServerLoggingHandler extends SoapLoggingHandler {

    private static Config config = Configs.getConfig("hosts.conf", "mail");

    public SoapServerLoggingHandler() {
        super(Level.valueOf(config.getString("debug.server")));
    }

    @Override
    protected boolean isRequest(boolean isOutbound) {
        return !isOutbound;
    }
}