package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    @NonNull
    @Column("full_name")
    private String fullName;
    @NonNull
    private String email;
    @NonNull
    private UserFlag flag;

    public User(Integer id, String fullName, String email, UserFlag flag) {
        this(fullName, email, flag);
        this.id = id;
    }
}