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