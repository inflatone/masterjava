package ru.javaops.masterjava.web.handler;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;
import com.sun.xml.ws.api.handler.MessageHandlerContext;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.streaming.XMLStreamWriterFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;

import java.io.ByteArrayOutputStream;
import java.util.EnumMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Refactored from:
 *
 * @see {http://weblogs.java.net/blog/ramapulavarthi/archive/2007/12/extend_your_web.html
 * http://fisheye5.cenqua.com/browse/jax-ws-sources/jaxws-ri/samples/efficient_handler/src/efficient_handler/common/LoggingHandler.java?r=MAIN}
 * <p/>
 * This simple LoggingHandler will log the contents of incoming
 * and outgoing messages. This is implemented as a MessageHandler
 * for better performance over SOAPHandler.
 */
@Slf4j
public abstract class SoapLoggingHandlers extends SoapBaseHandler {
    private final Level loggingLevel;

    protected SoapLoggingHandlers(Level loggingLevel) {
        this.loggingLevel = loggingLevel;
    }

    private static final Map<Level, Handler> HANDLER_MAP = new EnumMap<>(Level.class) {
        {
            put(Level.TRACE, Handler.DEBUG);
            put(Level.DEBUG, Handler.DEBUG);
            put(Level.INFO, Handler.INFO);
            put(Level.WARN, Handler.ERROR);
            put(Level.ERROR, Handler.ERROR);
        }
    };

    protected enum Handler {
        NONE {
            @Override
            public void handleMessage(MessageHandlerContext context, boolean isRequest) {
                // do nothing
            }

            @Override
            public void handleFault(MessageHandlerContext context) {
                // do nothing
            }
        },

        INFO {
            @Override
            public void handleMessage(MessageHandlerContext context, boolean isRequest) {
                ERROR.handleMessage(context, isRequest);
                log.info((isRequest ? "SOAP request: " : "SOAP response: ") + context.getMessage().getPayloadLocalPart());
            }

            @Override
            public void handleFault(MessageHandlerContext context) {
                ERROR.handleFault(context);
            }
        },

        DEBUG {
            @Override
            public void handleMessage(MessageHandlerContext context, boolean isRequest) {
                log.info((isRequest ? "SOAP request:\n" : "SOAP response:\n") + getMessageText(context.getMessage().copy()));
            }

            @Override
            public void handleFault(MessageHandlerContext context) {
                log.error("Fault SOAP message:\n" + getMessageText(context.getMessage().copy()));
            }
        },

        ERROR {
            private static final String REQUEST_MESSAGE = "REQUEST_MSG";

            @Override
            public void handleMessage(MessageHandlerContext context, boolean isRequest) {
                if (isRequest) {
                    context.put(REQUEST_MESSAGE, context.getMessage().copy());
                }
            }

            @Override
            public void handleFault(MessageHandlerContext context) {
                log.error("Fault SOAP request:\n" + getMessageText(((Message) context.get(REQUEST_MESSAGE))));
            }
        };

        public abstract void handleMessage(MessageHandlerContext context, boolean isRequest);

        public abstract void handleFault(MessageHandlerContext context);

        protected static String getMessageText(Message message) {
            try {
                var out = new ByteArrayOutputStream();
                var xmlWriter = new IndentingXMLStreamWriter(XMLStreamWriterFactory.create(out, UTF_8.name()));
                message.writeTo(xmlWriter);
                return out.toString(UTF_8);

            } catch (Exception e) {
                log.warn("Couldn't get SOAP message for logging", e);
                return null;
            }
        }
    }

    protected abstract boolean isRequest(boolean isOutbound);

    @Override
    public boolean handleMessage(MessageHandlerContext context) {
        HANDLER_MAP.get(loggingLevel).handleMessage(context, isRequest(isOutbound(context)));
        return true;
    }

    @Override
    public boolean handleFault(MessageHandlerContext context) {
        HANDLER_MAP.get(loggingLevel).handleFault(context);
        return true;
    }

    public static class ClientHandler extends SoapLoggingHandlers {
        public ClientHandler(Level loggingLevel) {
            super(loggingLevel);
        }

        @Override
        protected boolean isRequest(boolean isOutbound) {
            return isOutbound;
        }
    }

    public static class ServerHandler extends SoapLoggingHandlers {
        public ServerHandler(Level loggingLevel) {
            super(loggingLevel);
        }

        @Override
        protected boolean isRequest(boolean isOutbound) {
            return !isOutbound;
        }
    }
}
