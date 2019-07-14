package ru.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.*;
import ru.masterjava.persist.model.type.GroupType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Group extends BaseEntity {
    private @NonNull String name;
    private @NonNull GroupType type;

    @Column("project_id")
    private @NonNull int projectId;
}
