BEGIN;
    INSERT INTO category(name, type) VALUES ('Investimentos', 'RECEITA'),
                                            ('Alimentação', 'DESPESA'),
                                            ('Salário', 'RECEITA'),
                                            ('Transporte', 'DESPESA');
COMMIT;