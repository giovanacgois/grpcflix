DROP TABLE IF EXISTS movie;
CREATE TABLE movie AS SELECT * from CSVREAD('classpath:movie.csv');