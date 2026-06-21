CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    uuid CHAR(36) UNIQUE,
    cpf VARCHAR(255) UNIQUE,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255)
);

CREATE TABLE bank (
    id SERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(255),
    type VARCHAR(255)
);

CREATE TABLE category (
    id SERIAL PRIMARY KEY NOT NULL,
    type VARCHAR(255),
    name VARCHAR(255),
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE user_bank (
    id SERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(255),
    initial_balance DECIMAL(15,2),
    user_id INT NOT NULL,
    bank_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (bank_id) REFERENCES bank(id)
);

CREATE TABLE revenues (
    id SERIAL PRIMARY KEY NOT NULL,
    description VARCHAR(255),
    value DECIMAL(15,2),
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    bank_id INT NOT NULL,
    receipt_date DATE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES category(id),
    FOREIGN KEY (bank_id) REFERENCES bank(id)
);

CREATE TABLE expenses (
    id SERIAL PRIMARY KEY NOT NULL,
    description VARCHAR(255),
    value DECIMAL(15,2),
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    bank_id INT NOT NULL,
    expense_date DATE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES category(id),
    FOREIGN KEY (bank_id) REFERENCES bank(id)
);