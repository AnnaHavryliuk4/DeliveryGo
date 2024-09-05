CREATE TABLE menu_items
(
    id            SERIAL PRIMARY KEY,
    name          VARCHAR(255)   NOT NULL,
    price         DECIMAL(10, 2) NOT NULL,
    restaurant_id INT            NOT NULL,
    weight        INT            NOT NULL,
    serving       INT            NOT NULL,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id)
);
