--¬ыборка данных
--ќднотаблична€ выборка
--1.¬ывести товары, упор€дочив в алфавитном пор€дке по наименованию 
--и в обратном пор€дке по приоритету.
SELECT * FROM GOODS ORDER BY name;
SELECT * FROM GOODS ORDER BY priority DESC;
--2.ѕосчитать количество товаров в за€вках за заданную дату. 
SELECT good_count FROM sales WHERE (create_date = '15.02.2020')