-- Таблица кассовых смен
CREATE TABLE pos.cash_shift (
                                id SERIAL PRIMARY KEY,
                                pos_id INT NOT NULL REFERENCES catalog.pos(id),
                                salesman_id INT REFERENCES catalog.salesman(id),
                                opened_at TIMESTAMP NOT NULL,
                                closed_at TIMESTAMP,
                                status VARCHAR(50) DEFAULT 'open',
                                total_sum NUMERIC(15,2) DEFAULT 0,
                                created_by VARCHAR(100),
                                created_date TIMESTAMP DEFAULT now(),
                                last_modified_by VARCHAR(100),
                                last_modified_date TIMESTAMP
);

-- Таблица продаж
CREATE TABLE pos.sale (
                          id SERIAL PRIMARY KEY,
                          cash_shift_id INT REFERENCES pos.cash_shift(id),
                          pos_id INT NOT NULL REFERENCES catalog.pos(id),
                          store_id INT NOT NULL REFERENCES catalog.store(id),
                          salesman_id INT REFERENCES catalog.salesman(id),
                          total_amount NUMERIC(15,2),
                          payment_type VARCHAR(50),
                          created_by VARCHAR(100),
                          created_date TIMESTAMP DEFAULT now(),
                          last_modified_by VARCHAR(100),
                          last_modified_date TIMESTAMP
);

-- Таблица items продажи
CREATE TABLE pos.sale_item (
                               id SERIAL PRIMARY KEY,
                               sale_id INT NOT NULL REFERENCES pos.sale(id) ON DELETE CASCADE,
                               product_id INT NOT NULL REFERENCES catalog.product(id),
                               quantity NUMERIC(15,2) NOT NULL,
                               price NUMERIC(15,2) NOT NULL,
                               discount NUMERIC(15,2) DEFAULT 0,
                               sum NUMERIC(15,2) NOT NULL
);

-- Таблица фискальных чеков
CREATE TABLE pos.fiscal_receipt (
                                    id SERIAL PRIMARY KEY,
                                    sale_id INT NOT NULL REFERENCES pos.sale(id),
                                    printer_id INT NOT NULL REFERENCES catalog.printer(id),
                                    receipt_number VARCHAR(100),
                                    fiscal_data TEXT,
                                    status VARCHAR(50) DEFAULT 'pending',
                                    created_by VARCHAR(100),
                                    created_date TIMESTAMP DEFAULT now(),
                                    last_modified_by VARCHAR(100),
                                    last_modified_date TIMESTAMP
);

-- Таблица items фискального чека (копия продажи для печати/ОФД)
CREATE TABLE pos.fiscal_receipt_item (
                                         id SERIAL PRIMARY KEY,
                                         fiscal_receipt_id INT NOT NULL REFERENCES pos.fiscal_receipt(id) ON DELETE CASCADE,
                                         product_id INT NOT NULL REFERENCES catalog.product(id),
                                         quantity NUMERIC(15,2),
                                         price NUMERIC(15,2),
                                         discount NUMERIC(15,2),
                                         sum NUMERIC(15,2)
);