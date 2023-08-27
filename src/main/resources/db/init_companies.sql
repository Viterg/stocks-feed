CREATE TABLE IF NOT EXISTS public.companies
(
    id     SMALLSERIAL PRIMARY KEY,
    ticker VARCHAR(10) NOT NULL UNIQUE
);

INSERT INTO companies
VALUES (1, 'AAPL'),
       (2, 'MSFT'),
       (3, 'META'),
       (4, 'AMZN');