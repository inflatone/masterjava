package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.*;
import ru.javaops.masterjava.persist.model.type.UserFlag;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class User extends BaseEntity {
    @NonNull
    @Column("full_name")
    private
    String fullName;
    @NonNull
    private
    String email;
    @NonNull
    private
    UserFlag flag;
    @NonNull
    @Column("city_ref")
    private String cityRef;

    public User(Integer id, String fullName, String email, UserFlag flag, String cityRef) {
        this(fullName, email, flag, cityRef);
        this.id = id;
    }
}
