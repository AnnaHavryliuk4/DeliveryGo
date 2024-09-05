UPDATE menu_items
SET name = CASE
               WHEN restaurant_id = 1 AND name = 'Філадельфія' THEN 'Philadelphia roll'
               WHEN restaurant_id = 1 AND name = 'Філадельфія Сяке' THEN 'Philadelphia salmon roll'
               WHEN restaurant_id = 1 AND name = 'Філадельфія Дабл' THEN 'Philadelphia double roll'
               WHEN restaurant_id = 2 AND name = 'Карамельний чізкейк' THEN 'Caramel cheesecake'
               WHEN restaurant_id = 2 AND name = 'Баскський чізкейк' THEN 'Basque cheesecake'
               WHEN restaurant_id = 2 AND name = 'Наполеон' THEN 'Napoleon Cake'
               WHEN restaurant_id = 3 AND name = 'Донер в лаваші M (курка)' THEN 'Doner M (Chicken) in pita'
               WHEN restaurant_id = 3 AND name = 'Донер в лаваші M (свинина)' THEN 'Doner M (Pork) in pita'
               WHEN restaurant_id = 3 AND name = 'Донер сирний' THEN 'Cheesy doner'
               WHEN restaurant_id = 4 AND name = 'Сирники з вершковим соусом' THEN 'Cottage cheese pancakes with cream sauce'
               WHEN restaurant_id = 4 AND name = 'Омлет з беконом' THEN 'Omelet with bacon'
               WHEN restaurant_id = 4 AND name = 'Салат Антицезар' THEN 'Anti-Caesar salad'
               WHEN restaurant_id = 5 AND name = 'Піца Чотири сири' THEN 'Four cheese pizza'
               WHEN restaurant_id = 5 AND name = 'Піца Баварська' THEN 'Bavarian pizza'
               WHEN restaurant_id = 5 AND name = 'Піца Папероні' THEN 'Pepperoni pizza'
               WHEN restaurant_id = 6 AND name = 'Круасан Львівський' THEN 'Lviv croissant'
               WHEN restaurant_id = 6 AND name = 'Круасан Галицький' THEN 'Galitsky croissant'
               WHEN restaurant_id = 6 AND name = 'Круасан Королівський' THEN 'Royal croissant'
END
WHERE name IN ('Філадельфія', 'Філадельфія Сяке', 'Філадельфія Дабл',
               'Карамельний чізкейк', 'Баскський чізкейк', 'Наполеон',
               'Донер в лаваші M (курка)', 'Донер в лаваші M (свинина)', 'Донер сирний',
               'Сирники з вершковим соусом', 'Омлет з беконом', 'Салат Антицезар',
               'Піца Чотири сири', 'Піца Баварська', 'Піца Папероні',
               'Круасан Львівський', 'Круасан Галицький', 'Круасан Королівський')



