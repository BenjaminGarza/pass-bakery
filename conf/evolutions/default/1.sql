-- Product schema

-- !Ups

 CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

 CREATE TABLE Product (
 id uuid DEFAULT uuid_generate_v4 (),
 name VARCHAR NOT NULL,
 quantity INT NOT NULL,
 price NUMERIC(6, 2) NOT NULL CHECK (price >= 0),
 created_at TIMESTAMP,
 updated_at TIMESTAMP,
 PRIMARY KEY (id)
);

-- !Downs

DROP TABLE Product;
