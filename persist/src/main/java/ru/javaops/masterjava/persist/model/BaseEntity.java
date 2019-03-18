package ru.javaops.masterjava.persist.model;

import com.google.common.base.Objects;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public abstract class BaseEntity {
    @Getter
    @Setter
    protected Integer id;

    public boolean isNew() {
        return id == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id;
    }
}
