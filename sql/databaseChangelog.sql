--liquibase formatted sql

--changeset inflaton:1
CREATE TYPE user_flag AS ENUM ('active', 'deleted', 'superuser');

CREATE SEQUENCE user_seq START 100000;

CREATE TABLE users
(
    id        INTEGER PRIMARY KEY DEFAULT nextval('user_seq'),
    full_name TEXT      NOT NULL,
    email     TEXT      NOT NULL,
    flag      user_flag NOT NULL
);

CREATE UNIQUE INDEX email_idx ON users (email);


--changeset inflaton:2
CREATE SEQUENCE common_seq START 100000;

CREATE TABLE city
(
    ref  TEXT PRIMARY KEY,
    name TEXT NOT NULL
);

ALTER TABLE users
    ADD COLUMN city_ref TEXT REFERENCES city (ref) ON UPDATE CASCADE;

--changeset inflaton:3
CREATE TABLE project
(
    id          INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
    name        TEXT UNIQUE NOT NULL,
    description TEXT
);

CREATE TYPE GROUP_TYPE AS ENUM ('REGISTERING', 'CURRENT', 'FINISHED');

CREATE TABLE groups
(
    id         INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
    name       TEXT UNIQUE NOT NULL,
    type       GROUP_TYPE  NOT NULL,
    project_id INTEGER     NOT NULL REFERENCES project (id)
);

CREATE TABLE user_group
(
    user_id  INTEGER NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    group_id INTEGER NOT NULL REFERENCES groups (id),
    CONSTRAINT users_group_idx UNIQUE (user_id, group_id)
);

--changeset inflaton:4
CREATE TABLE mail_history
(
    id       SERIAL PRIMARY KEY,
    list_to  TEXT      NULL,
    list_cc  TEXT      NULL,
    subject  TEXT      NULL,
    state    TEXT      NOT NULL,
    datetime TIMESTAMP NOT NULL
);

COMMENT ON TABLE mail_history IS 'История отправки email';
COMMENT ON COLUMN mail_history.datetime IS 'Время отправки';