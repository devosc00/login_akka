# --- default dataset
# ---!Ups
		
insert into	ACCOUNT(ACC_ID, EMAIL ,PASSWORD, PERMISSION) values (1, 'robert@example.com', 'secret', 'Administrator');
insert into ACCOUNT(ACC_ID, EMAIL, PASSWORD, PERMISSION) values (2, 'marcin@example.com', 'secret', 'NormalUser');
insert into ACCOUNT(ACC_ID, EMAIL, PASSWORD, PERMISSION) values (3, 'przemek@example.com', 'secret', 'NormalUser');


insert into COMPANIES(COMP_ID, COMP_NAME, STREET, CITY, STATE, ZIP) values (101, 'Acme, Inc.', '99 Market Street', 'Groundsville', 'CA', '95199');
insert into COMPANIES(COMP_ID, COMP_NAME, STREET, CITY, STATE, ZIP) values (49, 'Superior Coffee', '1 Party Place', 'Mendocino', 'CA', '95460');
insert into COMPANIES(COMP_ID, COMP_NAME, STREET, CITY, STATE, ZIP) values (150, 'The High Ground', '100 Coffee Lane', 'Meadows', 'CA', '93966');


insert into	USERS(USER_NAME, ACC_ID, COMP_ID, POSSITION, DONE_PARTS, SETUP) values ('Robert Adamski', 1, 101, 'pilarz', 0, 0);
insert into USERS(USER_NAME, ACC_ID, COMP_ID, POSSITION, DONE_PARTS, SETUP) values ('Marcin Król', 2, 49, 'tokarz', 0, 0);
insert into	USERS(USER_NAME, ACC_ID, COMP_ID, POSSITION, DONE_PARTS, SETUP) values ('Przemek Sobota', 2, 49, 'frezer', 0, 0); 

# --- !Downs

delete from ACCOUNT;
delete from COMPANIES;
delete from USERS;
