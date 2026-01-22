# SQL-сущности базы данных системы Edge POS/WMS по микросервисам с комментариями

> Документ описывает таблицы каждой схемы микросервисов с подробными комментариями и использованием SQL формата. Все документы наследуют абстрактную AuditableCustom через соответствующие поля.

---

## 1. Catalog Service (Справочники)
**Схема:** catalog

```sql
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
```

---

## 2. POS Service (Документы продаж)
**Схема:** pos

```sql
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
```

---

## 3. WMS Service (Документы склада)
**Схема:** wms

```sql
-- Таблица приёмки товара
CREATE TABLE wms.goods_receipt (
    id SERIAL PRIMARY KEY,
    warehouse_id INT NOT NULL REFERENCES catalog.warehouse(id),
    store_id INT REFERENCES catalog.store(id),
    received_by_id INT REFERENCES catalog.person(id),
    total_items INT,
    created_by VARCHAR(100),
    created_date TIMESTAMP DEFAULT now(),
    last_modified_by VARCHAR(100),
    last_modified_date TIMESTAMP
);

CREATE TABLE wms.goods_receipt_item (
    id SERIAL PRIMARY KEY,
    goods_receipt_id INT NOT NULL REFERENCES wms.goods_receipt(id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES catalog.product(id),
    quantity NUMERIC(15,2),
    unit_price NUMERIC(15,2),
    total_price NUMERIC(15,2),
    lot_number VARCHAR(100)
);

-- Таблица списания товара
CREATE TABLE wms.goods_writeoff (
    id SERIAL PRIMARY KEY,
    warehouse_id INT NOT NULL REFERENCES catalog.warehouse(id),
    store_id INT REFERENCES catalog.store(id),
    written_off_by_id INT REFERENCES catalog.person(id),
    reason TEXT,
    total_items INT,
    created_by VARCHAR(100),
    created_date TIMESTAMP DEFAULT now(),
    last_modified_by VARCHAR(100),
    last_modified_date TIMESTAMP
);

CREATE TABLE wms.goods_writeoff_item (
    id SERIAL PRIMARY KEY,
    goods_writeoff_id INT NOT NULL REFERENCES wms.goods_writeoff(id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES catalog.product(id),
    quantity NUMERIC(15,2),
    unit_price NUMERIC(15,2),
    total_price NUMERIC(15,2)
);

-- Таблица перемещения товара
CREATE TABLE wms.goods_movement (
    id SERIAL PRIMARY KEY,
    source_warehouse_id INT NOT NULL REFERENCES catalog.warehouse(id),
    target_warehouse_id INT NOT NULL REFERENCES catalog.warehouse(id),
    moved_by_id INT REFERENCES catalog.person(id),
    total_items INT,
    created_by VARCHAR(100),
    created_date TIMESTAMP DEFAULT now(),
    last_modified_by VARCHAR(100),
    last_modified_date TIMESTAMP
);

CREATE TABLE wms.goods_movement_item (
    id SERIAL PRIMARY KEY,
    goods_movement_id INT NOT NULL REFERENCES wms.goods_movement(id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES catalog.product(id),
    quantity NUMERIC(15,2),
    unit_price NUMERIC(15,2),
    total_price NUMERIC(15,2)
);

-- Таблица группового обновления цен
CREATE TABLE wms.price_batch_update (
    id SERIAL PRIMARY KEY,
    store_id INT REFERENCES catalog.store(id),
    updated_by_id INT REFERENCES catalog.person(id),
    effective_date TIMESTAMP,
    created_by VARCHAR(100),
    created_date TIMESTAMP DEFAULT now(),
    last_modified_by VARCHAR(100),
    last_modified_date TIMESTAMP
);

CREATE TABLE wms.price_batch_update_item (
    id SERIAL PRIMARY KEY,
    price_batch_update_id INT NOT NULL REFERENCES wms.price_batch_update(id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES catalog.product(id),
    old_price NUMERIC(15,2),
    new_price NUMERIC(15,2)
);
```

