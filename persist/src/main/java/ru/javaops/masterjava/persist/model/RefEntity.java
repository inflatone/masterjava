package ru.javaops.masterjava.persist.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode

public abstract class RefEntity {
    @Getter
    @NonNull
    private String ref;
}
