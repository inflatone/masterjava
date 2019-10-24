package ru.javaops.masterjava.service.mail.persist;

import com.bertoncelj.jdbi.entitymapper.Column;
import com.google.common.base.Joiner;
import lombok.*;
import ru.javaops.masterjava.persist.model.BaseEntity;
import ru.javaops.masterjava.service.mail.Addressee;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false) // compare without id
@ToString
public class MailCase extends BaseEntity {
    private static final Joiner JOINER = Joiner.on(", ");  // joiner instances are always immutable, so they're thread-safe

    @Column("list_to")
    private String listTo;

    @Column("list_cc")
    private String listCc;

    private String subject;

    private String state;

    private Date dateTime;

    public static MailCase of(List<Addressee> to, List<Addressee> cc, String subject, String state) {
        return new MailCase(JOINER.join(to), JOINER.join(cc), subject, state, new Date());
    }
}
