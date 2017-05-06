package ru.javaops.web.handler;

import com.sun.xml.ws.api.handler.MessageHandlerContext;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.web.Statistics;

/**
 * Created by Mikhail on 05.05.2017.
 */

@Slf4j
public class SoapStatisticHandler extends SoapBaseHandler {

    private static final String PAYLOAD = "payload";
    private static final String START_TIME = "start_time";

    @Override
    public boolean handleMessage(MessageHandlerContext context) {
        if(!isOutbound(context)) {
            String payloadLocalPart = context.getMessage().getPayloadLocalPart();
            context.put(PAYLOAD, payloadLocalPart);
            context.put(START_TIME, System.currentTimeMillis());
        } else {
            Statistics.count(String.valueOf(context.get(PAYLOAD)), (Long) context.get(START_TIME), Statistics.RESULT.SUCCESS);
        }
        return true;
    }

    @Override
    public boolean handleFault(MessageHandlerContext context) {
        Statistics.count(String.valueOf(context.get(PAYLOAD)), (Long) context.get(START_TIME), Statistics.RESULT.FAIL);
        return true;
    }
}
