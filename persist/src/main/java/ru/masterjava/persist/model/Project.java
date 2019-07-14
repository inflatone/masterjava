package ru.masterjava.persist.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

public class Project extends BaseEntity {
    private @NonNull String name;
    private @NonNull String description;
}
