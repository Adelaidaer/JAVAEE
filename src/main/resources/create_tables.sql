-- Создание базы данных и пользователя (выполнить от имени postgres)
-- CREATE DATABASE kursach;
-- CREATE USER project_role WITH PASSWORD 'project_role';
-- GRANT ALL PRIVILEGES ON DATABASE kursach TO project_role;

-- Подключиться к базе kursach и выполнить следующие команды:

-- Создание таблиц для информационной подсистемы ведения клиентов - физических лиц

-- Таблица лиц
CREATE TABLE person (
    id BIGSERIAL PRIMARY KEY,
    shiffer VARCHAR(50) NOT NULL UNIQUE,
    inn VARCHAR(12) NOT NULL UNIQUE,
    type VARCHAR(100),
    date_created DATE NOT NULL
);

-- Таблица граждан
CREATE TABLE citizen (
    id BIGSERIAL PRIMARY KEY,
    person_id BIGINT NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    patronymic VARCHAR(100),
    personal_number VARCHAR(50) UNIQUE,
    FOREIGN KEY (person_id) REFERENCES person(id) ON DELETE CASCADE
);

-- Таблица документов
CREATE TABLE document (
    id BIGSERIAL PRIMARY KEY,
    citizen_id BIGINT NOT NULL,
    document_name VARCHAR(200),
    series VARCHAR(50),
    issuing_authority VARCHAR(500),
    issue_date DATE,
    FOREIGN KEY (citizen_id) REFERENCES citizen(id) ON DELETE CASCADE
);

-- Таблица пользователей
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash TEXT,
    email VARCHAR(100) UNIQUE,
    full_name VARCHAR(100),
    role VARCHAR(20),
    active BOOLEAN DEFAULT TRUE,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    login_attempts INT DEFAULT 0,
    account_locked BOOLEAN DEFAULT FALSE,
    password_change_required BOOLEAN DEFAULT FALSE,
    notes VARCHAR(500)
);

-- Таблица истории миграций
CREATE TABLE migration_history (
    id BIGSERIAL PRIMARY KEY,
    migration_version VARCHAR(50),
    migration_name VARCHAR(200),
    executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    execution_time_ms INT,
    success BOOLEAN DEFAULT TRUE,
    error_message TEXT
);

-- Вставка тестовых данных

-- Тестовые лица
INSERT INTO person (shiffer, inn, type, date_created) VALUES
('PER001', '123456789012', 'Физическое лицо', '2024-01-01'),
('PER002', '234567890123', 'Физическое лицо', '2024-01-02'),
('PER003', '345678901234', 'Физическое лицо', '2024-01-03');

-- Тестовые граждане
INSERT INTO citizen (person_id, first_name, last_name, patronymic, personal_number) VALUES
(1, 'Иван', 'Иванов', 'Иванович', 'PN001'),
(2, 'Петр', 'Петров', 'Петрович', 'PN002'),
(3, 'Сидор', 'Сидоров', 'Сидорович', 'PN003');

-- Тестовые документы
INSERT INTO document (citizen_id, document_name, series, issuing_authority, issue_date) VALUES
(1, 'Паспорт гражданина РФ', '1234', 'УФМС по Москве', '2020-01-15'),
(1, 'Водительское удостоверение', 'AB', 'ГИБДД Москвы', '2021-03-10'),
(2, 'Паспорт гражданина РФ', '5678', 'УФМС по СПб', '2019-05-20'),
(3, 'Паспорт гражданина РФ', '9012', 'УФМС по Казани', '2018-12-01');

-- Тестовые пользователи
INSERT INTO users (username, password_hash, email, full_name, role, active) VALUES
('admin', 'admin_hash', 'admin@example.com', 'Администратор Системы', 'ADMIN', TRUE),
('operator1', 'op1_hash', 'operator1@example.com', 'Оператор Первый', 'OPERATOR', TRUE),
('operator2', 'op2_hash', 'operator2@example.com', 'Оператор Второй', 'OPERATOR', TRUE);

-- Тестовая история миграций
INSERT INTO migration_history (migration_version, migration_name, executed_at, execution_time_ms, success) VALUES
('1.0.0', 'Создание начальной структуры таблиц', CURRENT_TIMESTAMP, 150, TRUE),
('1.0.1', 'Добавление тестовых данных', CURRENT_TIMESTAMP, 50, TRUE);

-- Предоставление прав пользователю
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO project_role;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO project_role; 