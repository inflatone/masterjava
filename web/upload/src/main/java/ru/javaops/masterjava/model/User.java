package ru.javaops.masterjava.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class User {
    private final Integer id;
    private final String fullName;
    private final String email;
    private final UserFlag flag;

    public User(String fullName, String email, UserFlag flag) {
        this(null, fullName, email, flag);
    }

    public User(Integer id, String fullName, String email, UserFlag flag) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.flag = flag;
    }

    public Integer getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public UserFlag getFlag() {
        return flag;
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
