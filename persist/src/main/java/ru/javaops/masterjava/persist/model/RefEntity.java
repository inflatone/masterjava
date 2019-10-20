package ru.javaops.masterjava.persist.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class RefEntity {
    @Getter
    @NonNull
    private String ref;
}
