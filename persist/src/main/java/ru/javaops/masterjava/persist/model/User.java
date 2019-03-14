package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class User extends BaseEntity {
    @Column("full_name")
    private String fullName;
    @Column
    private String email;
    @Column
    private UserFlag flag;

    public User() {
    }

    public User(String fullName, String email, UserFlag flag) {
        this(null, fullName, email, flag);
    }

    public User(Integer id, String fullName, String email, UserFlag flag) {
        super(id);
        this.fullName = fullName;
        this.email = email;
        this.flag = flag;
    }

    public String getFullName() {
        return fullName;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserFlag getFlag() {
        return flag;
    }

    public void setFlag(UserFlag flag) {
        this.flag = flag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equal(id, user.id) &&
                Objects.equal(fullName, user.fullName) &&
                Objects.equal(email, user.email) &&
                flag == user.flag;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, fullName, email, flag);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("fullName", fullName)
                .add("email", email)
                .add("flag", flag)
                .toString();
    }
}
