package ru.javaops.masterjava.service.mail.rest;

import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.validator.constraints.NotBlank;
import ru.javaops.masterjava.service.mail.Attachment;
import ru.javaops.masterjava.service.mail.GroupResult;
import ru.javaops.masterjava.service.mail.MailServiceExecutor;
import ru.javaops.masterjava.service.mail.MailWSClient;
import ru.javaops.masterjava.service.mail.util.Attachments.ProxyDataSource;
import ru.javaops.masterjava.web.WebStateException;

import javax.activation.DataHandler;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

@Path("/")
public class MailRS {
    @GET
    @Path("test")
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "Test";
    }

    @POST
    @Path("send")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public GroupResult send(
            @FormDataParam("users") @NotBlank String users,
            @FormDataParam("subject") String subject,
            @FormDataParam("body") @NotBlank String body,
            @FormDataParam("attach") FormDataBodyPart attachBodyPart
    ) throws WebStateException {
        final List<Attachment> attachments = attachBodyPart == null ? List.of()
                : List.of(parseAttach(attachBodyPart));
        return MailServiceExecutor.sendBulk(MailWSClient.split(users), subject, body, attachments);
    }

    private Attachment parseAttach(FormDataBodyPart attachBodyPart) {
        var attachName = attachBodyPart.getContentDisposition().getFileName();
        // UTF-8 encoding workaround: https://java.net/jira/browse/JERSEY-3032
        var utf8name = new String(attachName.getBytes(ISO_8859_1), UTF_8);
        var bodyPartEntity = (BodyPartEntity) attachBodyPart.getEntity();
        return new Attachment(utf8name, new DataHandler((ProxyDataSource) bodyPartEntity::getInputStream));
    }
}
