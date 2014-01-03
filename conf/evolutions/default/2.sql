# --- default dataset
# ---!Ups

insert into "COMPANIES"("COMP_ID", "COMP_NAME", "CITY", "ORDERS") values (101, 'Small', 'Koło', '0');
insert into "COMPANIES"("COMP_ID", "COMP_NAME", "CITY", "ORDERS") values (49, 'Big', 'Poznań', '0');
insert into "COMPANIES"("COMP_ID", "COMP_NAME", "CITY", "ORDERS") values (150, 'Hudge', 'New York', '0');

		
insert into	"ACCOUNT"("ACC_ID", "EMAIL" ,"PASSWORD", "NAME", "COMP_ID", "POSITION", "PERMISSION") values (10000, 'robert@example.com', 'secret', 
	'Robert Adamski', 101, 'admin', 'Administrator');
insert into "ACCOUNT"("ACC_ID", "EMAIL", "PASSWORD", "NAME", "COMP_ID", "POSITION", "PERMISSION") values (20000, 'marcin@example.com', 'secret',
	'Marcin Król', 49, 'sprzedaż', 'NormalUser');
insert into "ACCOUNT"("ACC_ID", "EMAIL", "PASSWORD", "NAME", "COMP_ID", "POSITION", "PERMISSION") values (30000, 'przemek@example.com', 'secret', 
	'Przemek Nowak', 49, 'produkcja', 'NormalUser');




# --- !Downs

delete from "ACCOUNT";
delete from "COMPANIES";

