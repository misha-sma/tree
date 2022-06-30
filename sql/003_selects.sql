// селект корневых файлов и папок
SELECT * FROM files WHERE id_parent IS NULL;

// селект полного пути до файла  
WITH RECURSIVE ppp(id_file, id_parent, file_name) AS (
    SELECT id_file, id_parent, file_name FROM files WHERE id_file = 6
  UNION
    SELECT files.id_file, files.id_parent, files.file_name
    FROM ppp, files
    WHERE files.id_file = ppp.id_parent
  )  
  SELECT * FROM ppp;

// селект полного пути до файла с аггегацией
select array_agg(id_file) from 
(WITH RECURSIVE ppp(id_file, id_parent, file_name) AS (
    SELECT id_file, id_parent, file_name FROM files WHERE id_file = 6
  UNION
    SELECT files.id_file, files.id_parent, files.file_name
    FROM ppp, files
    WHERE files.id_file = ppp.id_parent
  )  
  SELECT * FROM ppp) as t;

// селект подпапок и файлов 
select * from files where id_parent=1;

// селект селект всех подпапок и файлов  
WITH RECURSIVE ppp(id_file, id_parent, file_name) AS (
    SELECT id_file, id_parent, file_name FROM files WHERE id_file = 1
  UNION
    SELECT files.id_file, files.id_parent, files.file_name
    FROM ppp, files
    WHERE files.id_parent = ppp.id_file
  )  
  SELECT * FROM ppp;
  
// перемещение папки или файла
update files set id_parent=2 where id_file=6;
откат
update files set id_parent=4 where id_file=6;

// удаление папки
delete from files where id_file=1;

// контроль целостности---------------------------------------------
// проверка что у всех папок file_value=null
// должен быть 0
select count(1) from files where id_type=2 and not(file_value is null);

// проверка что все id_type валидные
// должен быть 0
select count(1) from files where id_type is null;

// проверка что все файлнеймы валидные
// должен быть 0
select count(1) from files where file_name is null or file_name='';

// проверка на зацикливание
// для одного файла или папки
select count(1) from  
(WITH RECURSIVE ppp(id_file, id_parent, file_name) AS (
    SELECT id_file, id_parent, file_name FROM files WHERE id_file = 6
  UNION
    SELECT files.id_file, files.id_parent, files.file_name
    FROM ppp, files
    WHERE files.id_file = ppp.id_parent
  )  
  SELECT * FROM ppp) as t1 where t1.id_parent is null;

// должно быть 1
