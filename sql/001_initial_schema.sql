create table file_types(id_type int primary key, name varchar(250));

create table files(id_file bigserial primary key, file_name varchar(250), id_type int, file_value text, id_parent bigint);
alter table files add constraint fk_id_type foreign key (id_type) references file_types(id_type);
alter table files add constraint fk_id_parent foreign key (id_parent) references files(id_file) on delete cascade;

insert into file_types(id_type, name) values(1, 'file');
insert into file_types(id_type, name) values(2, 'folder');
