CREATE TABLE IF NOT EXISTS users (
id          IDENTITY NOT NULL PRIMARY KEY,
name        VARCHAR     NOT NULL,
lastname    VARCHAR     NOT NULL,
email       VARCHAR     NOT NULL,
password       VARCHAR     NOT NULL
);

create table if not exists roles (
id identity not null primary key,
user_id integer not null,
role varchar not null,
foreign key (user_id) references users(id)
);

CREATE TABLE IF NOT EXISTS payments (
employee      varchar  not null,
period        VARCHAR  NOT NULL,
salary        INTEGER  NOT NULL,
PRIMARY key(employee, period)
);