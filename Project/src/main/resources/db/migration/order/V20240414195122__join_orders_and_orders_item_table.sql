SELECT o.user_id, o.shipping_cost, o.receipt_time, oi.restaurant_name, oi.order_id, oi.menu_item_name
FROM orders o
         JOIN order_items oi ON o.id = oi.order_id;
