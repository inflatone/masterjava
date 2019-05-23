package ru.javaops.masterjava.service.mail.rest;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.validator.constraints.NotBlank;
import ru.javaops.masterjava.service.mail.Attachment;
import ru.javaops.masterjava.service.mail.GroupResult;
import ru.javaops.masterjava.service.mail.MailServiceExecutor;
import ru.javaops.masterjava.service.mail.MailWSClient;
import ru.javaops.masterjava.service.mail.util.Attachments;
import ru.javaops.masterjava.web.WebStateException;

import javax.activation.DataHandler;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

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
            @NotBlank @FormDataParam("users") String users,
            @FormDataParam("subject") String subject,
            @NotBlank @FormDataParam("body") String body,
            @FormDataParam("attach") FormDataBodyPart attachBodyPart
    ) throws WebStateException {
        return MailServiceExecutor.sendBulk(MailWSClient.split(users), subject, body, prepareAttachments(attachBodyPart));
    }

    private List<Attachment> prepareAttachments(FormDataBodyPart attachBodyPart) {
        if (attachBodyPart == null) {
            return ImmutableList.of();
        }
        String attachName = attachBodyPart.getContentDisposition().getFileName();
        // UTF-8 encoding workaround: https://java.net/jira/browse/JERSEY-3032
        String utf8Name = new String(attachName.getBytes(Charsets.ISO_8859_1), Charsets.UTF_8);
        BodyPartEntity entity = ((BodyPartEntity) attachBodyPart.getEntity());
        return ImmutableList.of(new Attachment(utf8Name, new DataHandler((Attachments.ProxyDataSource) entity::getInputStream)));
    }
}
