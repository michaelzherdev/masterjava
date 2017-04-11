CREATE TABLE emails (
  id          INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
  from_name        TEXT NOT NULL ,
  subject TEXT,
  body TEXT,
  result TEXT,
  date TIMESTAMP NOT NULL DEFAULT now()
);