package ru.javaops.masterjava.persist.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public abstract class BaseEntity {
    protected Integer id;

    public boolean isNew() {
        return id == null;
    }
}
