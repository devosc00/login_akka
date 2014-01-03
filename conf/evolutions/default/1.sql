# --- !Ups

create table "ACCOUNT" ("ACC_ID" BIGSERIAL NOT NULL PRIMARY KEY,"EMAIL" VARCHAR(254) NOT NULL,"PASSWORD" VARCHAR(254) NOT NULL,"NAME" VARCHAR(254) NOT NULL,"COMP_ID" BIGINT NOT NULL,"POSITION" VARCHAR(254) NOT NULL,"PERMISSION" VARCHAR(254) NOT NULL);
create table "COMPANIES" ("COMP_ID" BIGSERIAL NOT NULL PRIMARY KEY,"COMP_NAME" VARCHAR(254) NOT NULL,"CITY" VARCHAR(254) NOT NULL,"ORDERS" INTEGER NOT NULL);


alter table "ACCOUNT" add constraint "COMP_FK" foreign key("COMP_ID") references "COMPANIES"("COMP_ID") on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table "ACCOUNT" drop constraint "COMP_FK";
drop table "ACCOUNT";
drop table "COMPANIES";


