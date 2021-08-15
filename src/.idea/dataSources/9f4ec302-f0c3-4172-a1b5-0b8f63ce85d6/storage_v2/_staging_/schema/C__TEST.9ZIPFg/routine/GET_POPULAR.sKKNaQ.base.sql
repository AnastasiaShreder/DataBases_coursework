create PROCEDURE get_popular(date1 IN date, date2 IN date)
IS
BEGIN
SELECT * FROM
    (SELECT sales.good_id, goods.name, COUNT(sales.id)as requests_count
     FROM goods
              JOIN sales ON sales.good_id = goods.id
     WHERE (sales.create_date BETWEEN date1 AND date2)
     GROUP BY goods.name, sales.good_id
     ORDER BY COUNT(sales.id) DESC)
WHERE ROWNUM <= 5;
END;
/

