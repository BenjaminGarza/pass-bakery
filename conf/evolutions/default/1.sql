-- Product schema

-- !Ups

 CREATE TABLE Product (
 id UUID NOT NULL PRIMARY KEY,
 name VARCHAR NOT NULL,
 quantity INT NOT NULL,
 price DECIMAL NOT NULL CHECK (price >= 0),
 created_at TIMESTAMP NOT NULL,
 updated_at TIMESTAMP NOT NULL
);

-- !Downs

DROP TABLE Product;
