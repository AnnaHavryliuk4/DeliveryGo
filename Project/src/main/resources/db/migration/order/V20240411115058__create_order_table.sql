CREATE TABLE orders
(
    id              SERIAL PRIMARY KEY,
    delivery_method VARCHAR(10)    NOT NULL,
    address         VARCHAR(30)    NOT NULL,
    address_notes   VARCHAR(30),
    shipping_cost   DECIMAL(10, 2) NOT NULL,
    time            TIMESTAMP      NOT NULL,
    customer_name   VARCHAR(100)   NOT NULL,
    phone_number    VARCHAR(20)    NOT NULL,
    notes           TEXT,
    payment_method  VARCHAR(10)    NOT NULL,
    user_id         INT            NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);
