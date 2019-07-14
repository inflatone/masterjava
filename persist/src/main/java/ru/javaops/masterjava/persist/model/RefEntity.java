package ru.javaops.masterjava.persist.model;

import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public abstract class RefEntity {
    @Getter
    private @NonNull String ref;
}
