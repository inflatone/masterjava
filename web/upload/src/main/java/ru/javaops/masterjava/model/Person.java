package ru.javaops.masterjava.model;

import java.util.Objects;

public class Person {
    private final Integer id;
    private final String fullName;
    private final String email;
    private final PersonFlag flag;

    public Person(Integer id, String fullName, String email, PersonFlag flag) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.flag = flag;
    }

    public Person(String fullName, String email, PersonFlag flag) {
        this(null, fullName, email, flag);
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

    public PersonFlag getFlag() {
        return flag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id) &&
                Objects.equals(fullName, person.fullName) &&
                Objects.equals(email, person.email) &&
                flag == person.flag;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, email, flag);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", flag=" + flag +
                '}';
    }
}
