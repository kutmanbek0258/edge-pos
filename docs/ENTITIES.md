# Сущности базы данных системы Edge POS/WMS по сервисам (без Auth Service)

> Документ описывает ключевые сущности базы данных для каждого микросервиса с разделением по сервисам, сохранением логики справочников и документов, включая items. Цель — логичная структуризация ER-модели под микросервисную архитектуру.

---

## 1. Принципы проектирования по сервисам

- Каждый микросервис использует **отдельную схему** в PostgreSQL для изоляции.
- Справочники и документы распределяются так, чтобы минимизировать прямые зависимости между сервисами.
- Документы содержат **items** для детализации транзакций.
- Взаимодействие между сервисами через API и Event Bus, прямые связи в БД исключены.

---

## 2. Catalog Service (Справочники)

**Схема:** catalog

### 2.1 Company (Компания)
- id, name, tax_number, address, created_at, updated_at

### 2.2 Warehouse (Склад)
- id, company_id, name, location, created_at, updated_at

### 2.3 Store (Магазин)
- id, company_id, warehouse_id, name, address, created_at, updated_at

### 2.4 CashRegister (Касса)
- id, store_id, fiscal_device_id, code, name, created_at, updated_at

### 2.5 FiscalDevice (Фискальный регистратор)
- id, store_id, serial_number, type, status, created_at, updated_at

### 2.6 Person (Сотрудник / Клиент)
- id, first_name, last_name, middle_name, email, phone, created_at, updated_at

### 2.7 Salesman (Продавец)
- id, person_id, store_id, code, created_at, updated_at

### 2.8 Product (Товар)
- id, company_id, name, description, unit, category, price, created_at, updated_at

### 2.9 Barcode (Штрихкод)
- id, product_id, code, type, created_at, updated_at

> При необходимости можно добавлять новые справочники (поставщики, категории скидок, группы клиентов).

---

## 3. POS Service (Документы продаж)

**Схема:** pos

### 3.1 CashShift (Кассовая смена)
- id, cash_register_id, salesman_id, opened_at, closed_at, status, total_sum, created_at, updated_at
- items: список продаж в смене (sale_id, sum, timestamp)

### 3.2 Sale (Продажа)
- id, cash_shift_id, store_id, salesman_id, total_amount, payment_type, created_at, updated_at
- items: product_id, quantity, price, discount, sum

### 3.3 FiscalReceipt (Фискальный чек)
- id, sale_id, fiscal_device_id, receipt_number, fiscal_data, status, created_at, updated_at
- items: повторяют позиции продажи для печати и отправки в ОФД

---

## 4. WMS Service (Документы склада)

**Схема:** wms

### 4.1 GoodsReceipt (Приём товара)
- id, warehouse_id, store_id, received_by_id, total_items, created_at, updated_at
- items: product_id, quantity, unit_price, total_price, lot_number

### 4.2 GoodsWriteOff (Списание товара)
- id, warehouse_id, store_id, written_off_by_id, reason, total_items, created_at, updated_at
- items: product_id, quantity, unit_price, total_price

### 4.3 GoodsMovement (Перемещение товара)
- id, source_warehouse_id, target_warehouse_id, moved_by_id, total_items, created_at, updated_at
- items: product_id, quantity, unit_price, total_price

### 4.4 PriceBatchUpdate (Групповая установка цен)
- id, store_id, updated_by_id, effective_date, created_at, updated_at
- items: product_id, old_price, new_price

> По мере логики можно добавлять документы: возвраты, инвентаризация, корректировки остатков.

---

## 4.5 Search Service (Индекс поиска)

**Источник:** Elasticsearch

`search-service` использует Elasticsearch для хранения денормализованных данных о товарах, что обеспечивает быстрый и эффективный полнотекстовый поиск. Индекс обновляется асинхронно на основе событий из Apache Kafka, которые генерирует `catalog-service`.

### Индекс: `products`

- **id**: Уникальный идентификатор товара.
- **name**: Наименование товара (текстовое поле для поиска).
- **description**: Описание товара (текстовое поле).
- **category**: Категория товара (ключевое слово для фильтрации).
- **price**: Цена (числовое значение).
- **barcodes**: Список штрихкодов, связанных с товаром (вложенный объект или массив строк).

---

## 5. Printer Service

**Схема:** printer
- Хранение логов печати (event logs), не хранит бизнес-данные
- items: данные чеков для печати

---

## 6. OFD Sync Service

**Схема:** ofd
- Хранение логов отправки чеков в ОФД
- items: статусы отправки по каждому receipt_id

---

## 7. License Service

**Схема:** license
- Статус лицензии, hardware fingerprint, даты активации и окончания

---

## 8. Связи и изоляция

- Все документы ссылаются на справочники **своего сервиса** и через ID на внешние справочники при необходимости
- Документы содержат массив items, каждый item ссылается на продукт (product_id)
- Event-driven интеграция между микросервисами вместо прямых join-ов

| Микросервис | Схема | Основные сущности |
|-------------|-------|-----------------|
| CatalogService | catalog | Company, Store, Warehouse, CashRegister, FiscalDevice, Person, Salesman, Product, Barcode |
| POSService | pos | CashShift, Sale, FiscalReceipt |
| WMSService | wms | GoodsReceipt, GoodsWriteOff, GoodsMovement, PriceBatchUpdate |
| SearchService | search (Elasticsearch) | индекс товаров |
| PrinterService | printer | печатные логи / events |
| OFDSyncService | ofd | лог отправки чеков |
| LicenseService | license | статус лицензии |

---

## 9. Итог

- Сущности перераспределены по сервисам без нарушения логики
- Разделение схем повышает изоляцию и безопасность
- Справочники находятся в CatalogService, остальные документы — в своих бизнес-сервисах
- Система готова к расширению и интеграции новых документов и справочников без нарушения архитектуры

---

**Конец перераспределённого описания сущностей БД по сервисам**

