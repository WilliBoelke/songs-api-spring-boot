drop table if exists users;

create table users
(
    userid    varchar(100) primary key,
    password  varchar(100),
    firstname varchar(100),
    lastname  varchar(100),
    token     varchar(100)
);

insert into users(userid, password, firstname, lastname, token)
VALUES ('mmuster', 'pass1234', 'Maxime', 'Muster', null);

insert into users(userid, password, firstname, lastname, token)
VALUES ('userTwo', 'pass1234', 'User', 'Two', null)