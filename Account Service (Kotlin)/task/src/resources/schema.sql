CREATE TABLE IF NOT EXISTS users (
id          IDENTITY NOT NULL PRIMARY KEY,
name        VARCHAR     NOT NULL,
lastname    VARCHAR     NOT NULL,
email       VARCHAR     NOT NULL,
password       VARCHAR     NOT NULL
);
CREATE TABLE IF NOT EXISTS payments (
employee      varchar  not null,
period        VARCHAR  NOT NULL,
salary        INTEGER  NOT NULL,
PRIMARY key(employee, period)
);