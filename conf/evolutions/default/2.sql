# --- default dataset
# ---!Ups
		
insert into	"ACCOUNT"("ACC_ID", "EMAIL" ,"PASSWORD", "PERMISSION") values (1, 'robert@example.com', 'secret', 'Administrator');
insert into "ACCOUNT"("ACC_ID", "EMAIL", "PASSWORD", "PERMISSION") values (2, 'marcin@example.com', 'secret', 'NormalUser');
insert into "ACCOUNT"("ACC_ID", "EMAIL", "PASSWORD", "PERMISSION") values (3, 'przemek@example.com', 'secret', 'NormalUser');


insert into "COMPANIES"("COMP_ID", "COMP_NAME", "CITY", "ORDERS") values (101, 'Small', 'Koło', '0');
insert into "COMPANIES"("COMP_ID", "COMP_NAME", "CITY", "ORDERS") values (49, 'Big', 'Poznań', '0');
insert into "COMPANIES"("COMP_ID", "COMP_NAME", "CITY", "ORDERS") values (150, 'Hudge', 'New York', '0');


insert into	"USERS"("USER_NAME", "ACC_ID", "COMP_ID", "POSSITION", "DONE_PARTS", "SETUP") values ('Robert Adamski', 1, 101, 'pilarz', 0, 0);
insert into "USERS"("USER_NAME", "ACC_ID", "COMP_ID", "POSSITION", "DONE_PARTS", "SETUP") values ('Marcin Król', 2, 49, 'tokarz', 0, 0);
insert into	"USERS"("USER_NAME", "ACC_ID", "COMP_ID", "POSSITION", "DONE_PARTS", "SETUP") values ('Przemek Sobota', 3, 49, 'frezer', 0, 0); 

# --- !Downs

delete from "ACCOUNT";
delete from "COMPANIES";
delete from "USERS";
