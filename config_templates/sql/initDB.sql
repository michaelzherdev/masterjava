DROP TABLE IF EXISTS user_groups;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS cities;
DROP TABLE IF EXISTS groups;
DROP TABLE IF EXISTS projects;
DROP SEQUENCE IF EXISTS user_seq;
DROP SEQUENCE IF EXISTS city_seq;
DROP SEQUENCE IF EXISTS group_seq;
DROP SEQUENCE IF EXISTS proj_seq;
DROP TYPE IF EXISTS user_flag;
DROP TYPE IF EXISTS group_type;

CREATE TYPE user_flag AS ENUM ('active', 'deleted', 'superuser');

CREATE SEQUENCE user_seq START 100000;
CREATE SEQUENCE group_seq START 10000;
CREATE SEQUENCE proj_seq START 1000;
CREATE SEQUENCE city_seq START 1000;


CREATE TABLE projects (
  id  INTEGER PRIMARY KEY DEFAULT nextval('proj_seq'),
  name TEXT NOT NULL,
  description TEXT
);

CREATE UNIQUE INDEX p_name_idx ON projects (name);

CREATE TYPE group_type AS ENUM ('REGISTERING', 'CURRENT', 'FINISHED');

CREATE TABLE groups (
  id   INTEGER PRIMARY KEY DEFAULT nextval('group_seq'),
  name TEXT NOT NULL,
  type group_type NOT NULL
);

CREATE UNIQUE INDEX g_name_idx ON groups (name);

CREATE TABLE cities (
  id      INTEGER PRIMARY KEY DEFAULT nextval('city_seq'),
  id_str  TEXT NOT NULL,
  value   TEXT NOT NULL
);

CREATE UNIQUE INDEX id_str_idx ON cities (id_str);

CREATE TABLE users (
  id        INTEGER PRIMARY KEY DEFAULT nextval('user_seq'),
  full_name TEXT NOT NULL,
  email     TEXT NOT NULL,
  flag      user_flag NOT NULL,
  city_id INTEGER REFERENCES cities(id)
);

CREATE UNIQUE INDEX email_idx ON users (email);

CREATE TABLE user_groups (
  user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  group_id INTEGER NOT NULL REFERENCES groups(id) ON DELETE CASCADE
);