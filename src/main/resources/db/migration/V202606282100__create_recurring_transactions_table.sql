BEGIN;
    CREATE TABLE IF NOT EXISTS recurring_transactions (
        id SERIAL PRIMARY KEY NOT NULL,
        description VARCHAR(255) NOT NULL,
        value DECIMAL(15,2) NOT NULL,
        type VARCHAR(20) NOT NULL,
        day INT NOT NULL,
        active BOOLEAN DEFAULT true,
        category_id INT NOT NULL,
        bank_id INT NOT NULL,
        user_id INT NOT NULL,
        FOREIGN KEY (category_id) REFERENCES category(id),
        FOREIGN KEY (bank_id) REFERENCES bank(id),
        FOREIGN KEY (user_id) REFERENCES users(id)
    );
COMMIT;