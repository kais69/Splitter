create table if not exists PERSON
(
    person_id   INTEGER auto_increment primary key,
    git_hub_name varchar(40) unique
);


create table if not exists GRUPPE
(
    gruppe_id  INTEGER auto_increment primary key,
    name varchar(40) ,
    geschlossen boolean
);

create table if not exists GRUPPE_PERSON
(
    gruppe Integer references GRUPPE(gruppe_id),
    person Integer references PERSON(person_id),
    primary key (gruppe,person)
)
;

create table if not exists TRANSAKTION
(
    id INTEGER auto_increment primary key,
    gruppe Integer references GRUPPE (gruppe_id),
    beschreibung varchar(40),
    betrag double,
    sender varchar(40),
    empfaenger varchar(40)

);





