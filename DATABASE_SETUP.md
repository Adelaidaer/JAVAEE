# Настройка базы данных для проекта Kursach

## Требования
- PostgreSQL 12 или выше
- Права администратора для создания базы данных и пользователей

## Шаги настройки

### 1. Подключение к PostgreSQL от имени суперпользователя
```bash
sudo -u postgres psql
```

### 2. Создание базы данных и пользователя
```sql
-- Создание базы данных
CREATE DATABASE kursach;

-- Создание пользователя
CREATE USER project_role WITH PASSWORD 'project_role';

-- Предоставление прав на базу данных
GRANT ALL PRIVILEGES ON DATABASE kursach TO project_role;

-- Выход из psql
\q
```

### 3. Подключение к базе данных kursach
```bash
psql -h localhost -U project_role -d kursach
```

### 4. Выполнение SQL-скрипта для создания таблиц
```bash
psql -h localhost -U project_role -d kursach -f src/main/resources/create_tables.sql
```

Или выполните SQL-команды вручную из файла `src/main/resources/create_tables.sql`

### 5. Проверка созданных таблиц
```sql
-- Список всех таблиц
\dt

-- Проверка структуры таблицы faculty
\d faculty

-- Проверка данных в таблице faculty
SELECT * FROM faculty;
```

## Альтернативный способ (автоматическое создание таблиц через Hibernate)

Если PostgreSQL настроен правильно, Hibernate должен автоматически создать таблицы при первом запуске приложения благодаря настройке `hibernate.hbm2ddl.auto=update` в `persistence.xml`.

## Диагностика проблем

### 1. Проверка подключения к базе данных
```bash
pg_isready -h localhost -p 5432
```

### 2. Проверка, что PostgreSQL запущен
```bash
sudo systemctl status postgresql
```

### 3. Проверка логов PostgreSQL
```bash
sudo tail -f /var/log/postgresql/postgresql-*.log
```

### 4. Тестирование подключения из приложения
Откройте страницу `http://localhost:8080/kursach/db-test.xhtml` для диагностики подключения.

## Возможные проблемы

### Ошибка: "database does not exist"
Выполните команды из пункта 2.

### Ошибка: "role does not exist"
Выполните команды создания пользователя из пункта 2.

### Ошибка: "permission denied"
Убедитесь, что пользователь `project_role` имеет необходимые права:
```sql
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO project_role;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO project_role;
```

### Hibernate не создает таблицы
Выполните SQL-скрипт вручную (пункт 4).

## Параметры подключения
- Host: localhost
- Port: 5432
- Database: kursach
- Username: project_role
- Password: project_role 