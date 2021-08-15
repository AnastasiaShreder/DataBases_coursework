# Курсовая работа по дисциплине "Системы управления базами данных" 

## Описание задания
Необходимо автоматизировать работу оптовой фирмы. Для этого в рамках базы данных ORACLE необходимо создать объекты в схеме пользователя и написать клиентское приложение на базе компонентов JDBC с использованием JavaFX.

Организация занимается оптовыми и мелкооптовыми поставками различных товаров в магазины Санкт-Петербурга. Требуется автоматизировать рабочее место менеджера по распределению товаров между двумя складами организации. 
Первый склад исполняет функции КЭШа множества самых популярных товаров. Задачей менеджера по распределению является изучение спроса и выделения множества приоритетных товаров, которые будут завезены на первый склад. Менеджер должен иметь доступ к состоянию обоих складов, справочнику товаров и журналу реализации. При появлении заявки на определенный товар, менеджер заносит заявку в журнал и списывает необходимое количество товара с первого склада. В случае, если на первом складе товара недостаточно – остаток списывается со второго. Такой вариант приносит лишние расходы фирме, так как в этом случае она занимается доставкой товара на территорию заказчика, чтобы не потерять клиента. Завоз товаров на первый склад производится ночью каждого дня в соответствии с приоритетами, выставленными менеджером. Второй склад содержит достаточное число товаров, и его работа выходит за рамки данного проекта.

## Схема базы данных

![схема](/readme_img/scheme.png)

## Структура приложения

### Models

1.	Good – сущность товара с полями Наименование, Приоритет, Количество.
2.	Request – сущность заявки с полями Номер заявки, id товара, Количество товара, Дата создания.
3.	Analytics – сущность данных аналитики, используется для сохранения необходимой информации.
4.	User – сущность пользователя с полями Логин, Пароль, используется для авторизации.

### Controllers и соответствующие FXML-файлы
1.	GoodsController – контроллер интерфейса, предназначенного для работы с товарами. Включает в себя реализации операций вывода, добавления, удаления и модификации товаров. (goodsOperations.fxml)
2.	RequestController – контроллер, управляющий работой с заявками: добавление, модификация, удаление и вывод.(requestsOperations.fxml)
3.	JournalController – контроллер, реализующий интерфейс главного меню и журнала обоих складов.(journal.fxml)
4.	AnalyticsController – контроллер, реализующий возможность просмотреть изменение спроса заданного товара(графически и таблицей), получить список самых популярных товаров за период и сформировать в текстовом виде список для перевоза товаров.(analytics.fxml)
5.	PopularGoodsController - контроллер интерфейса для вывода самых популярных товаров. (popularGoods.fxml)
6.	AuthController – контроллер, реализующий интерфейс входа в систему.(auth.fxml)

## Описание работы программы

После запуска программы пользователь должен войти в систему, введя логин и пароль.

![scr1](/readme_img/screen1.png)

Главное меню представляет собой одновременно навигацию и журнал менеджера складов. Таблицы показывают, какие товары находятся на складах и их текущее количество.
Таблицы в журнале обновляются в процессе работы с товарами или заявками.

![scr2](/readme_img/screen2.png)

При нажатии кнопки «Работа с товарами» пользователь попадает в окно, позволяющее проводить операции с товарами. Для удобства главное меню остается открытым, чтобы пользователь мог просматривать наличие товара на складе.

![scr3](/readme_img/screen3.png)

При нажатии кнопки «Работа с заявками» пользователь аналогично попадает в окно, позволяющее проводить операции с заявками.

![scr4](/readme_img/screen4.png)

«Аналитика» - перевод пользователя на окно, представляющее интерфейс для просмотра аналитических данных на основании сделанных заявок. При выборе товара и вводе дат пользователь получает информацию об изменении спроса на данный товар в виде таблицы и в виде графика. Также пользователь может получить лист из пяти самых популярных товаров за выбранный период и сформировать список для перевоза со склада 2 на склад 1.

![scr5](/readme_img/screen5.png)
