--������� ������
--������������� �������
--1.������� ������, ���������� � ���������� ������� �� ������������ 
--� � �������� ������� �� ����������.
SELECT * FROM GOODS ORDER BY name;
SELECT * FROM GOODS ORDER BY priority DESC;
--2.��������� ���������� ������� � ������� �� �������� ����. 
SELECT good_count FROM sales WHERE (create_date = '15.02.2020')