package ru.javaops.masterjava.web.handler;

import com.sun.xml.ws.api.handler.MessageHandlerContext;
import ru.javaops.masterjava.web.Statistics;

public class SoapStatisticHandler extends SoapBaseHandler {
    private static final String PAYLOAD = "PAYLOAD";
    private static final String START_TIME = "START_TIME";

    @Override
    public boolean handleMessage(MessageHandlerContext context) {
        if (isOutBound(context)) {
            count(context, Statistics.Result.SUCCESS);
        } else {
            context.put(PAYLOAD, context.getMessage().getPayloadLocalPart());
            context.put(START_TIME, System.currentTimeMillis());
        }
        return true;
    }

    @Override
    public boolean handleFault(MessageHandlerContext context) {
        count(context, Statistics.Result.FAIL);
        return true;
    }

    private void count(MessageHandlerContext context, Statistics.Result result) {
        Statistics.count((String) context.get(PAYLOAD), (Long) context.get(START_TIME), result);
    }
}
