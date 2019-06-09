drop table if exists row cascade;
drop sequence if exists row_seq;
create sequence row_seq start 1000 increment 1000;
create table row (id int8 not null, date1 timestamp, date2 timestamp, date3 timestamp, date4 timestamp, long1 int8, long2 int8, long3 int8, long4 int8, long5 int8, str1 varchar(255), str2 varchar(255), str3 varchar(255), str4 varchar(255), str5 varchar(255), str6 varchar(255), str7 varchar(255), str8 varchar(255), str9 varchar(255), primary key (id));
