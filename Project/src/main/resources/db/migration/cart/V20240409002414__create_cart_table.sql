CREATE TABLE cart
(
    id              SERIAL PRIMARY KEY,
    restaurant_id   INT            NOT NULL,
    restaurant_name VARCHAR(255)   NOT NULL,
    item_id         INT            NOT NULL,
    item_name       VARCHAR(255)   NOT NULL,
    item_price      DECIMAL(10, 2) NOT NULL,
    user_id         INT            NOT NULL,
    total_price     DECIMAL(10, 2) NOT NULL,
    quantity        INT            NOT NULL DEFAULT 1,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id),
    FOREIGN KEY (item_id) REFERENCES menu_items (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);
