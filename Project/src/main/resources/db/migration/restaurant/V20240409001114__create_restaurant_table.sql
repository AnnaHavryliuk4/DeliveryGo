CREATE TABLE restaurants
(
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    delivery_method VARCHAR(255) NOT NULL
);
