package ru.javaops.masterjava.service.mail;

import ru.javaops.masterjava.web.WebStateException;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;
import java.util.List;
import java.util.Set;

@WebService(endpointInterface = "ru.javaops.masterjava.service.mail.MailService", targetNamespace = "http://mail.javaops.ru/"
        //       , wsdlLocation = "WEB-INF/wsdl/mailService.wsdl"
)
@MTOM
// @StreamingAttachment(parseEagerly=true, memoryThreshold=40000L)
@HandlerChain(file = "mailWsHandlers.xml")
public class MailServiceImpl implements MailService {

//    @Resource
//    private WebServiceContext wsContext;

    @Override
    public String sendToGroup(Set<Addressee> to, Set<Addressee> cc, String subject, String body, List<Attachment> attachments) throws WebStateException {
/*
        MessageContext messageCtx = wsContext.getMessageContext();
        Map<String, List<String>> headers = (Map<String, List<String>>) messageCtx.get(MessageContext.HTTP_REQUEST_HEADERS);

        int code = AuthUtil.checkBasicAuth(headers, MailWSClient.AUTH_HEADER);
        if (code != 0) {
            messageCtx.put(MessageContext.HTTP_RESPONSE_CODE, code);
            throw new SecurityException();
        }
*/
        return MailSender.sendToGroup(to, cc, subject, body, attachments);
    }

    @Override
    public GroupResult sendBulk(Set<Addressee> to, String subject, String body, List<Attachment> attachments) throws WebStateException {
        return MailServiceExecutor.sendBulk(to, subject, body, attachments);
    }
}
