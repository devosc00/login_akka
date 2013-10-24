# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "ACCOUNT" ("ACC_ID" INTEGER NOT NULL PRIMARY KEY,"EMAIL" VARCHAR NOT NULL,"PASSWORD" VARCHAR NOT NULL,"PERMISSION" VARCHAR NOT NULL);
create table "COMPANIES" ("COMP_ID" INTEGER NOT NULL PRIMARY KEY,"COMP_NAME" VARCHAR NOT NULL,"STREET" VARCHAR NOT NULL,"CITY" VARCHAR NOT NULL,"STATE" VARCHAR NOT NULL,"ZIP" VARCHAR NOT NULL);
create table "USERS" ("USER_NAME" VARCHAR NOT NULL PRIMARY KEY,"ACC_ID" INTEGER NOT NULL,"COMP_ID" INTEGER NOT NULL,"POSSITION" VARCHAR NOT NULL,"DONE_PARTS" INTEGER NOT NULL,"SETUP" INTEGER NOT NULL);
alter table "USERS" add constraint "ACC_FK" foreign key("ACC_ID") references "ACCOUNT"("ACC_ID") on update NO ACTION on delete NO ACTION;
alter table "USERS" add constraint "COMP_FK" foreign key("COMP_ID") references "COMPANIES"("COMP_ID") on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table "USERS" drop constraint "ACC_FK";
alter table "USERS" drop constraint "COMP_FK";
drop table "ACCOUNT";
drop table "COMPANIES";
drop table "USERS";

