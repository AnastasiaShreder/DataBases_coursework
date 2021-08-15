create or replace trigger CHECK_GOODS_DELETE
    before delete
    on GOODS
    for each row
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
      RAISE_APPLICATION_ERROR(-20111,  'Товар есть на складе или в заявках'); 
    END IF;
END;


