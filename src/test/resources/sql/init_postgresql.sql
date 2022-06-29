CREATE TABLE bar (
    foo VARCHAR(255)
);

INSERT INTO bar (foo) VALUES ('hello world');


CREATE TABLE passengers (
   id INT PRIMARY KEY NOT NULL,
   name VARCHAR (100) NOT NULL,
   email VARCHAR (255) UNIQUE NOT NULL,
   age INTEGER NOT NULL,
   travel_to VARCHAR (255) NOT NULL,
   payment INTEGER,
   active BOOLEAN,
   travel_date DATE
);

INSERT INTO passengers (id, name, email, age, travel_to, payment, active, travel_date)
VALUES (1, 'Jack', 'jack12@gmail.com', 20, 'Paris', 79000, true, '2018-1-1'),
        (2, 'Anna', 'anna@gmail.com', 19, 'NewYork', 405000, true, '2019-10-3'),
        (3, 'Wonder', 'wonder2@yahoo.com', 32, 'Sydney', 183000, false, '2012-8-5'),
        (4, 'Stacy', 'stacy78@hotmail.com', 28, 'Maldives', 29000, NULL, '2017-6-9'),
        (5, 'Stevie', 'stevie@gmail.com', 49, 'Greece', 56700, false, '2021-12-12'),
        (6, 'Harry', 'harry@gmail.com', 22, 'Hogwarts', 670000, false, '2020-1-17');