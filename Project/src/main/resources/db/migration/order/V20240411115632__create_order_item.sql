CREATE TABLE order_items
(
    order_id     INT NOT NULL,
    item_id INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders (id),
    FOREIGN KEY (item_id) REFERENCES menu_items (id)
);
