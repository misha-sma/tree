--root
insert into files(file_name, id_type, file_value, id_parent) values('folder_1', 2, null, null); -- 1
insert into files(file_name, id_type, file_value, id_parent) values('folder_2', 2, null, null); -- 2
insert into files(file_name, id_type, file_value, id_parent) values('file_4', 1, 'file_4_value', null); -- 3

insert into files(file_name, id_type, file_value, id_parent) values('folder_3', 2, null, 1); -- 4
insert into files(file_name, id_type, file_value, id_parent) values('file_3', 1, 'file_3_value', 1); -- 5

insert into files(file_name, id_type, file_value, id_parent) values('file_1', 1, 'file_1_value', 4); -- 6
insert into files(file_name, id_type, file_value, id_parent) values('file_2', 1, 'file_2_value', 4); -- 7
