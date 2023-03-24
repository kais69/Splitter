create table if not exists "PERSON"
(
    "PERSON_ID"   serial primary key,
    "GIT_HUB_NAME" text unique
);


create table if not exists "GRUPPE"
(
    "GRUPPE_ID"  serial primary key,
    "NAME" text ,
    "GESCHLOSSEN" boolean
);

create table if not exists "GRUPPE_PERSON"
(
    "GRUPPE" Integer references "GRUPPE"("GRUPPE_ID"),
    "PERSON" Integer references "PERSON"("PERSON_ID"),
    primary key ("GRUPPE","PERSON")
)
;

create table if not exists "TRANSAKTION"
(
    "ID"     serial primary key,
    "GRUPPE" Integer references "GRUPPE" ("GRUPPE_ID"),
    "BESCHREIBUNG" text,
    "BETRAG" numeric,
    "SENDER" text,
    "EMPFAENGER" text

);





