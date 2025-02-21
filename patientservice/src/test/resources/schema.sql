CREATE TABLE IF NOT EXISTS patient
(
    Id
    int
    NOT
    NULL
    AUTO_INCREMENT,
    first_name
    varchar
(
    35
) NOT NULL,
    last_name varchar
(
    35
) NOT NULL,
    birth_date date NOT NULL,
    genre varchar
(
    1
) NOT NULL,
    postal_address varchar
(
    90
) DEFAULT NULL,
    phone_number varchar
(
    16
) DEFAULT NULL,
    PRIMARY KEY
(
    Id
)
    );


