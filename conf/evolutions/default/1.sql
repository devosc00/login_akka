# --- !Ups

create table "ACCOUNT" ("ACC_ID" BIGSERIAL NOT NULL PRIMARY KEY,"EMAIL" VARCHAR(254) NOT NULL,"PASSWORD" VARCHAR(254) NOT NULL,"PERMISSION" VARCHAR(254) NOT NULL);
create table "COMPANIES" ("COMP_ID" BIGSERIAL NOT NULL PRIMARY KEY,"COMP_NAME" VARCHAR(254) NOT NULL,"CITY" VARCHAR(254) NOT NULL,"ORDERS" INTEGER NOT NULL);
create table "USERS" ("USER_NAME" VARCHAR(254) NOT NULL PRIMARY KEY,"ACC_ID" BIGINT NOT NULL,"COMP_ID" BIGINT NOT NULL,"POSSITION" VARCHAR(254) NOT NULL,"DONE_PARTS" INTEGER NOT NULL,"SETUP" INTEGER NOT NULL);
alter table "USERS" add constraint "ACC_FK" foreign key("ACC_ID") references "ACCOUNT"("ACC_ID") on update NO ACTION on delete NO ACTION;
alter table "USERS" add constraint "COMP_FK" foreign key("COMP_ID") references "COMPANIES"("COMP_ID") on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table "USERS" drop constraint "ACC_FK";
alter table "USERS" drop constraint "COMP_FK";
drop table "ACCOUNT";
drop table "COMPANIES";
drop table "USERS";

