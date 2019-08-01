package ru.javaops.masterjava.service.mail.rest;

import com.google.common.collect.ImmutableList;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import ru.javaops.masterjava.service.mail.Attachment;
import ru.javaops.masterjava.service.mail.GroupResult;
import ru.javaops.masterjava.service.mail.MailServiceExecutor;
import ru.javaops.masterjava.service.mail.MailWSClient;
import ru.javaops.masterjava.service.mail.util.Attachments.ProxyDataSource;
import ru.javaops.masterjava.web.WebStateException;

import javax.activation.DataHandler;
import javax.validation.constraints.NotBlank;
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
    public GroupResult send(@NotBlank @FormDataParam("users") String users,
                            @FormDataParam("subject") String subject,
                            @NotBlank @FormDataParam("body") String body,
                            @FormDataParam("attach") FormDataBodyPart attachBodyPart) throws WebStateException {
        final List<Attachment> attachments;
        if (attachBodyPart == null) {
            attachments = ImmutableList.of();
        } else {
            String attachName = attachBodyPart.getContentDisposition().getFileName();
            // https://stackoverflow.com/questions/546365/utf-8-text-is-garbled-when-form-is-posted-as-multipart-form-data
            String utf8name = new String(attachName.getBytes(ISO_8859_1), UTF_8);
            BodyPartEntity bodyPartEntity = (BodyPartEntity) attachBodyPart.getEntity();
            attachments = ImmutableList.of(new Attachment(utf8name, new DataHandler((ProxyDataSource) bodyPartEntity::getInputStream)));
        }
        return MailServiceExecutor.sendBulk(MailWSClient.split(users), subject, body, attachments);
    }


}
