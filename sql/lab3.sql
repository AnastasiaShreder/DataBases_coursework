--�������������
--1.	������� �������������, ������������ ��� ������, ����� ������� �� ������� 1� ����� ���������� �����.
CREATE VIEW goods_warehouse1 AS
SELECT goods.id, goods.name, warehouse1.good_count FROM warehouse1
INNER JOIN goods ON warehouse1.good_id = goods.id WHERE warehouse1.good_count < 10;
--2.	������� �������������, ������������ 5 ����� ���������� ������� �� �������� �����.
CREATE VIEW goods_popular AS
SELECT * FROM 
(SELECT sales.good_id, goods.name, COUNT(sales.id)as requests_count 
FROM goods 
JOIN sales ON sales.good_id = goods.id 
WHERE (sales.create_date BETWEEN '01.02.21' AND '30.02.21') 
GROUP BY goods.name, sales.good_id 
ORDER BY COUNT(sales.id) DESC) 
WHERE ROWNUM <= 5;

DROP VIEW goods_warehouse1;
DROP VIEW goods_popular;
--�������� ���������
--�	��� ����������:
--1.+++	������� �������� ���������, ��������� ������ ������� ��� �������� � ��� ���������� �������� �������� ��������� �����������.
CREATE GLOBAL TEMPORARY TABLE OUTPUT (
    id_ NUMBER,
    name_ VARCHAR2(50),
    count_goods INT,
    priority_ INT
)

CREATE OR REPLACE PROCEDURE get_transportation_list
IS
BEGIN
    DELETE FROM OUTPUT; 
    
    INSERT INTO OUTPUT(
     id_ ,
     name_,
     count_goods,
     priority_
    )

    SELECT * FROM
    ((SELECT goods.id as id_, goods.name as name_, (warehouse2.good_count - warehouse1.good_count) AS count_goods, goods.priority as priority_ 
    FROM warehouse2 
    INNER JOIN goods ON warehouse2.good_id = goods.id INNER JOIN warehouse1 ON warehouse2.good_id = warehouse1.good_id) 
    MINUS 
    (SELECT goods.id as id_, goods.name as name_, (warehouse2.good_count - warehouse1.good_count) AS count_goods, goods.priority as priority_ FROM warehouse2 
    INNER JOIN goods ON warehouse2.good_id = goods.id INNER JOIN warehouse1 ON warehouse2.good_id = warehouse1.good_id 
    WHERE (warehouse2.good_count - warehouse1.good_count) = '0'))
    ORDER BY priority_;
END;
EXEC get_transportation_list;
SELECT * FROM OUTPUT;
DROP TABLE OUTPUT;
--�	� �������� �����������:
--1.+++	������� �������� ��������� � ���������� ����������� ������������ ������ �� ��������� ���� � ��������� ��� ������, ������� ���������� ��������, � �� ����������.
CREATE GLOBAL TEMPORARY TABLE OUTPUT (
    id_ NUMBER,
    name_ VARCHAR2(50),
    count_goods INT,
    priority_ INT
)
CREATE OR REPLACE PROCEDURE get_transportation_list_with_count(count_goods_ IN int)
IS
BEGIN
    DELETE FROM OUTPUT; 
    
    INSERT INTO OUTPUT(
     id_ ,
     name_,
     count_goods,
     priority_
    )
    
    SELECT * FROM
    (
    (SELECT goods.id as id_, goods.name as name_, (warehouse2.good_count - warehouse1.good_count) AS count_goods, goods.priority as priority_ FROM warehouse2 
    INNER JOIN goods ON warehouse2.good_id = goods.id INNER JOIN warehouse1 ON warehouse2.good_id = warehouse1.good_id) 
    MINUS 
    (SELECT goods.id as id_, goods.name as name_, (warehouse2.good_count - warehouse1.good_count) AS count_goods, goods.priority as priority_ FROM warehouse2 
    INNER JOIN goods ON warehouse2.good_id = goods.id INNER JOIN warehouse1 ON warehouse2.good_id = warehouse1.good_id 
    WHERE (warehouse2.good_count - warehouse1.good_count) = '0')
    )
    WHERE ROWNUM <= count_goods_ ORDER BY priority_;
END;
EXEC get_transportation_list_with_count('5');
SELECT * FROM OUTPUT;
--2.+++	������� �������� ���������, ������� ��� ��������� ������1� � ������2�. ��� ������ ���������� ����, ����� � ������� �� 
--������1� ��� ������ ��� �� ������2�. ���� � �����-���� ���� ���� �� ������� �� ����������, ����� ���� �� ���������������.
CREATE GLOBAL TEMPORARY TABLE OUTPUT (
    the_best_date date,
    good_id_1 number,
    count_1 int,
    good_id_2 number, 
    count_2 int
)
CREATE OR REPLACE PROCEDURE get_date_the_best_good(good_1 IN VARCHAR, good_2 IN VARCHAR)
IS
BEGIN
    DELETE FROM OUTPUT;
    
    INSERT INTO OUTPUT(the_best_date, good_id_1, count_1, good_id_2, count_2)
	SELECT DISTINCT table_1.create_date, table_1.good_id, table_1.cnt_1, table_2.good_id, table_2.cnt_2 FROM
    (SELECT sales.create_date, sales.good_id, COUNT(goods.name) as cnt_1 FROM sales JOIN goods ON sales.good_id = goods.id 
    WHERE goods.name = good_1 GROUP BY sales.create_date, sales.good_id) table_1
    JOIN  
    (SELECT sales.create_date, sales.good_id, COUNT(goods.name) as cnt_2 FROM sales JOIN goods ON sales.good_id = goods.id 
    WHERE goods.name = good_2 GROUP BY sales.create_date, sales.good_id) table_2
    ON 
    table_1.create_date = table_2.create_date
    WHERE table_1.good_id IS NOT NULL AND table_2.good_id IS NOT NULL AND
    table_1.cnt_1 > table_2.cnt_2;
END;
EXEC get_date_the_best_good('������� �������', '�������')
SELECT * FROM OUTPUT;
DROP TABLE OUTPUT;
--�	� ��������� �����������:
--1.+++	������� �������� ��������� � �������� �����������, ��������� �������� �������, � �������� � ��������������� ������. 
--��������� ������ ���������� ����� � ������������ ��������� ������.
CREATE OR REPLACE PROCEDURE get_the_best_good(date_1 IN date, date_2 IN date, id_good OUT NUMBER)
IS
BEGIN
    SELECT goods.id INTO id_good FROM goods 
    INNER JOIN sales ON sales.good_id = goods.id
    WHERE sales.create_date BETWEEN date_1 AND date_2
    GROUP BY goods.id, goods.name
    ORDER BY COUNT(sales.good_id) DESC 
    FETCH FIRST 1 ROWS ONLY;
END;
VARIABLE id_good NUMBER;
EXEC get_the_best_good('01.02.21', '10.02.21', :id_good);
print id_good;
DROP TABLE OUTPUT;
--��������
--�	�������� �� �������:
--1.+++	������� �������, ������� �� ��������� �������� ������ �� �����, 
--����� �������� �� ����� ������� ������ ���������� � ������.
CREATE OR REPLACE TRIGGER CHECK_SALES_COUNT
BEFORE INSERT OR UPDATE
    ON sales
    FOR EACH ROW
DECLARE 
    count1 INT;
    count2 INT;
BEGIN
    SELECT warehouse1.good_count, warehouse2.good_count INTO count1, count2 
    FROM warehouse1, warehouse2
    WHERE warehouse1.good_id = :NEW.good_id 
    AND warehouse2.good_id = :NEW.good_id;
    
    IF (count1 + count2 < :NEW.good_count)
    THEN
        RAISE_APPLICATION_ERROR(-20001,  '������������ ������� �� ������');
    END IF;
END;

INSERT INTO sales(good_id, good_count, create_date) VALUES ('7', '100', '01.02.2021');
--2.+++	������� �������, ������� �� ��������� �������� ������ c ������ ������ ������ 1.
CREATE OR REPLACE TRIGGER CHECK_SALES_ZERO
BEFORE INSERT OR UPDATE
    ON sales
    FOR EACH ROW
BEGIN
    IF (:NEW.good_count < 1)
    THEN 
        RAISE_APPLICATION_ERROR(-20001,  '� ������ ������ ���� ���� �� ���� �����');
    END IF;
END;

INSERT INTO sales(good_id, good_count, create_date) VALUES ('7', '0', '01.02.2021');
--�	�������� �� �����������:
--1.+++	������� �������, ������� �� ��������� ��������� ����� ������ �� 
--������� 2� ��� ������� ����� ������ �� ������� 1�.
CREATE OR REPLACE TRIGGER CHECK_WAREHOUSE_REDUCING
BEFORE INSERT OR UPDATE
    ON warehouse2
    FOR EACH ROW
DECLARE 
    count1 INT;
BEGIN
    SELECT warehouse1.good_count INTO count1 
    FROM warehouse1
    WHERE warehouse1.good_id = :NEW.good_id ;
    
    IF (count1 > 0)
    THEN
        RAISE_APPLICATION_ERROR(-20001,  '�� ������ 1 ���� � ������� ������ �����');
    END IF;
END;

UPDATE warehouse2
    SET warehouse2.good_count = 1
    WHERE warehouse2.good_id = 1;
--�	�������� �� ��������:
--1.+++	������� �������, ������� ��� �������� ������ 
--� ������ ������� �� ���� ������ ���������� ����������.
CREATE OR REPLACE TRIGGER CHECK_GOODS_DELETE
BEFORE DELETE
    ON goods
    FOR EACH ROW
DECLARE 
    count_s INT;
    count_w1 INT;
    count_w2 INT;
BEGIN
    SELECT COUNT(sales.good_id) INTO count_s FROM sales
    WHERE sales.good_id = :NEW.id;
    
    SELECT COUNT(warehouse1.good_id) INTO count_w1 FROM warehouse1
    WHERE warehouse1.good_id = :NEW.id;
    
    SELECT COUNT(warehouse2.good_id) INTO count_w2 FROM warehouse2
    WHERE warehouse2.good_id = :NEW.id;
    
    IF (count_s > 0 OR count_w1 > 0 OR count_w2 > 0)
    THEN
      RAISE_APPLICATION_ERROR(-20001,  '����� ���� �� ������ ��� � �������'); 
    END IF;
END;

DELETE FROM goods WHERE id=2;

--�������
--�	�������� ��������� ��� �������� ������ �� �������� �����:
--���������� ����������� �������� ���������, �������������� �������������� ����� �� 7 ���� �� ��������� �����. 
--�������� ��������� ������ ����� ��� ������� ���������, �������� �������� ������� ��� ������� ��������� ������, 
--� ��������, �������� ������������� �����.

--������������ ��������: � ������� ������� ��������� ��������� �������, ���������� ����� ��� � ��������������� ��������� 
--� ����� ����������� ������. 
--������������ ����� ��������� � ����������

--���������� ������, ������������ ��������������� ������ ��������� �������, ������������� �� ������ �����, 
--� ����������� ������� �������� ����� 
--(�� ������ �������� ����� ���������� �� 1 ������). 
--���������� ������� �������� ������ ����, �������� �������� ����������� �� ������ �����. ��������� ����� ���������. 
--������ ������� ����������� �� ��� ���, ���� � ������� �� ��������� 2 �����. 
--����� �������, ��� ������� ������ � ���� ���� ������ ����� ������� ������ ����� �������������� 
--�� ��������� ���� ������� � ������� �� �������� ����� ���������� ���. 
--����� �������, �� ����� �������� �������� ��������������� ������ (������������� ������� ����������� ��������).
--Hints: CURSOR, %NOTFOUND, FETCH

CREATE GLOBAL TEMPORARY TABLE dataset (
    date_ date,
    count_req INT
)

CREATE OR REPLACE PROCEDURE create_dataset(date_begin IN date, date_end IN date, good IN NUMBER)
IS
BEGIN
    DELETE FROM dataset;
    INSERT INTO dataset(date_, count_req)
        SELECT sales.create_date as date_, COUNT(goods.id) as count_req FROM sales JOIN goods ON sales.good_id = goods.id 
        WHERE goods.id = good AND sales.create_date BETWEEN date_begin AND date_end 
        GROUP BY sales.create_date, sales.good_id 
        ORDER BY sales.create_date;
END;
EXEC create_dataset(TO_DATE('01.02.2021', 'dd.mm.yyyy'), TO_DATE('25.02.2021', 'dd.mm.yyyy'), 8);

CREATE OR REPLACE PROCEDURE get_dataset (date_begin IN date, date_end IN date, good IN NUMBER)
IS
    date1 date;
    date2 date;
    count1 number;
    count2 number;
    cnt INT;
     
    CURSOR cur IS SELECT * FROM dataset;
BEGIN 
    OPEN cur;
    SELECT COUNT(dataset.date_) INTO cnt FROM dataset;
    IF cnt <= 2
    THEN
      RETURN;
    END IF;
    FETCH cur INTO date1, count1;
    
    WHILE cnt > 2
    LOOP
        FETCH cur INTO date2, count2;
        count1 := (count1 + count2)/2;
        DELETE FROM dataset WHERE dataset.date_ = date2;
        cnt := cnt - 1;
    END LOOP;
    CLOSE cur;
    UPDATE dataset
    SET dataset.count_req = count1 WHERE dataset.date_ = date1;
END;

EXEC get_dataset(TO_DATE('01.02.2021', 'dd.mm.yyyy'), TO_DATE('25.02.2021', 'dd.mm.yyyy'), 8);
SELECT * FROM dataset;

CREATE OR REPLACE PROCEDURE get_prediction (date_begin IN date, date_end IN date, good IN NUMBER)
IS
    CURSOR cur IS
        SELECT (warehouse1.good_count + warehouse2.good_count) FROM warehouse1, warehouse2 
        WHERE warehouse1.good_id = 1 AND warehouse2.good_id = 1;
        UNION ALL
        SELECT sales.create_date
        