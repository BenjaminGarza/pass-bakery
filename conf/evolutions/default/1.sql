-- Product schema

-- !Ups

 CREATE TABLE Product (
 id uuid  NOT NULL PRIMARY KEY,
 name VARCHAR NOT NULL,
 quantity INT NOT NULL,
 price NUMERIC(6, 2) NOT NULL CHECK (price >= 0),
 created_at TIMESTAMP NOT NULL,
 updated_at TIMESTAMP NOT NULL,
);

-- !Downs

DROP TABLE Product;
