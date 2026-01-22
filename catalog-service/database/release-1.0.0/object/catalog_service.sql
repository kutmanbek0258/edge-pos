-- Таблица компании
CREATE TABLE catalog.company (
                                 id SERIAL PRIMARY KEY,
                                 name VARCHAR(255) NOT NULL,
                                 tax_number VARCHAR(50),
                                 address TEXT,
                                 created_at TIMESTAMP DEFAULT now(),
                                 updated_at TIMESTAMP DEFAULT now()
);

-- Таблица складов
CREATE TABLE catalog.warehouse (
                                   id SERIAL PRIMARY KEY,
                                   company_id INT NOT NULL REFERENCES catalog.company(id),
                                   name VARCHAR(255) NOT NULL,
                                   location TEXT,
                                   created_at TIMESTAMP DEFAULT now(),
                                   updated_at TIMESTAMP DEFAULT now()
);

-- Таблица магазинов
CREATE TABLE catalog.store (
                               id SERIAL PRIMARY KEY,
                               company_id INT NOT NULL REFERENCES catalog.company(id),
                               warehouse_id INT REFERENCES catalog.warehouse(id),
                               name VARCHAR(255) NOT NULL,
                               address TEXT,
                               created_at TIMESTAMP DEFAULT now(),
                               updated_at TIMESTAMP DEFAULT now()
);

-- Таблица POS (точка продаж)
CREATE TABLE catalog.pos (
                             id SERIAL PRIMARY KEY,
                             store_id INT NOT NULL REFERENCES catalog.store(id),
                             code VARCHAR(50) UNIQUE NOT NULL,
                             name VARCHAR(255),
                             active_printer_id INT REFERENCES catalog.printer(id),
                             status VARCHAR(50) DEFAULT 'active',
                             created_at TIMESTAMP DEFAULT now(),
                             updated_at TIMESTAMP DEFAULT now()
);

-- Таблица Printer (ESC/POS)
CREATE TABLE catalog.printer (
                                 id SERIAL PRIMARY KEY,
                                 store_id INT NOT NULL REFERENCES catalog.store(id),
                                 serial_number VARCHAR(100) UNIQUE NOT NULL,
                                 type VARCHAR(50),
                                 network_address VARCHAR(100),
                                 supports_fiscalization BOOLEAN DEFAULT TRUE,
                                 status VARCHAR(50) DEFAULT 'online',
                                 created_at TIMESTAMP DEFAULT now(),
                                 updated_at TIMESTAMP DEFAULT now()
);

-- Таблица сотрудников/клиентов
CREATE TABLE catalog.person (
                                id SERIAL PRIMARY KEY,
                                first_name VARCHAR(100),
                                last_name VARCHAR(100),
                                middle_name VARCHAR(100),
                                email VARCHAR(100),
                                phone VARCHAR(50),
                                created_at TIMESTAMP DEFAULT now(),
                                updated_at TIMESTAMP DEFAULT now()
);

-- Таблица продавцов
CREATE TABLE catalog.salesman (
                                  id SERIAL PRIMARY KEY,
                                  person_id INT NOT NULL REFERENCES catalog.person(id),
                                  store_id INT NOT NULL REFERENCES catalog.store(id),
                                  code VARCHAR(50),
                                  created_at TIMESTAMP DEFAULT now(),
                                  updated_at TIMESTAMP DEFAULT now()
);

-- Таблица товаров
CREATE TABLE catalog.product (
                                 id SERIAL PRIMARY KEY,
                                 company_id INT NOT NULL REFERENCES catalog.company(id),
                                 name VARCHAR(255) NOT NULL,
                                 description TEXT,
                                 unit VARCHAR(50),
                                 category VARCHAR(100),
                                 price NUMERIC(15,2) NOT NULL,
                                 created_at TIMESTAMP DEFAULT now(),
                                 updated_at TIMESTAMP DEFAULT now()
);

-- Таблица штрихкодов
CREATE TABLE catalog.barcode (
                                 id SERIAL PRIMARY KEY,
                                 product_id INT NOT NULL REFERENCES catalog.product(id),
                                 code VARCHAR(100) NOT NULL,
                                 type VARCHAR(50),
                                 created_at TIMESTAMP DEFAULT now(),
                                 updated_at TIMESTAMP DEFAULT now()
);