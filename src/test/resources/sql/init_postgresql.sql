CREATE TABLE bar (
    foo VARCHAR(255)
);

INSERT INTO bar (foo) VALUES ('hello world');


CREATE TABLE passengers (
   id INT PRIMARY KEY NOT NULL,
   "name" VARCHAR (100) NOT NULL,
   "Email" VARCHAR (255) UNIQUE NOT NULL,
   "Age" INTEGER NOT NULL,
   "Travel_to" VARCHAR (255) NOT NULL,
   "Payment" INTEGER,
   "Travel_date" DATE
);

INSERT INTO passengers (id, "name", "Email", "Age", "Travel_to", "Payment", "Travel_date")
VALUES (1, 'Jack', 'jack12@gmail.com', 20, 'Paris', 79000, '2018-1-1'),
        (2, 'Anna', 'anna@gmail.com', 19, 'NewYork', 405000, '2019-10-3'),
        (3, 'Wonder', 'wonder2@yahoo.com', 32, 'Sydney', 183000, '2012-8-5'),
        (4, 'Stacy', 'stacy78@hotmail.com', 28, 'Maldives', 29000, '2017-6-9'),
        (5, 'Stevie', 'stevie@gmail.com', 49, 'Greece', 56700, '2021-12-12'),
        (6, 'Harry', 'harry@gmail.com', 22, 'Hogwarts', 670000, '2020-1-17');