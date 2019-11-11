package ru.javaops.masterjava.service.mail.rest;

import org.hibernate.validator.constraints.NotBlank;
import ru.javaops.masterjava.service.mail.GroupResult;
import ru.javaops.masterjava.service.mail.MailServiceExecutor;
import ru.javaops.masterjava.service.mail.MailWSClient;
import ru.javaops.masterjava.web.WebStateException;

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
    public GroupResult send(
            @FormParam("users") @NotBlank String users,
            @FormParam("subject") String subject,
            @FormParam("body") @NotBlank String body
    ) throws WebStateException {
        return MailServiceExecutor.sendBulk(MailWSClient.split(users), subject, body, List.of());
    }
}
