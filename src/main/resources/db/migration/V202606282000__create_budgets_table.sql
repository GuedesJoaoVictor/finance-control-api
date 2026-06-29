BEGIN;
    CREATE TABLE IF NOT EXISTS budgets (
        id SERIAL PRIMARY KEY NOT NULL,
        category_id INT NOT NULL,
        user_id INT NOT NULL,
        month INT NOT NULL,
        year INT NOT NULL,
        limit_amount DECIMAL(15,2) NOT NULL,
        FOREIGN KEY (category_id) REFERENCES category(id),
        FOREIGN KEY (user_id) REFERENCES users(id),
        CONSTRAINT unique_budget UNIQUE (category_id, user_id, month, year)
    );
COMMIT;