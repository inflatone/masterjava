package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGroup {
    @Column("user_id")
    private @NonNull Integer userId;

    @Column("group_id")
    private @NonNull Integer groupId;
}
