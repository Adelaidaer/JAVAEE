# Информационная подсистема ведения клиентов - физических лиц

Веб-приложение для управления информацией о клиентах-физических лицах, их документах и пользователях системы.

## Описание

Система предназначена для ведения учета клиентов - физических лиц и включает следующий функционал:

- **Управление лицами (Person)** - учет физических лиц с уникальными шифрами и ИНН
- **Управление гражданами (Citizen)** - личные данные граждан, привязанные к лицам  
- **Управление документами (Document)** - документы граждан (паспорта, водительские удостоверения и т.д.)
- **Управление пользователями (Users)** - пользователи системы с ролями и правами доступа
- **История миграций (Migration History)** - отслеживание изменений структуры базы данных

## Технологический стек

- **Java 17**
- **Jakarta EE 9** (Jakarta Servlet, JSF, CDI, JPA)
- **Hibernate 6.0.2** (JPA implementation)
- **PostgreSQL** (база данных)
- **PrimeFaces 12.0** (UI компоненты)
- **Maven** (сборка проекта)
- **Apache Tomcat** (веб-сервер)

## Установка окружения

### 1. Установка Homebrew (macOS)

```bash
# Установка Homebrew
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Добавить в PATH (для Apple Silicon)
echo 'eval "$(/opt/homebrew/bin/brew shellenv)"' >> ~/.zshrc
source ~/.zshrc

# Для Intel Mac добавить в PATH
echo 'eval "$(/usr/local/bin/brew shellenv)"' >> ~/.zshrc
source ~/.zshrc
```

### 2. Установка Java

```bash
# Установка OpenJDK 17
brew install openjdk@17

# Добавить в PATH
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Проверка установки
java -version
```

### 3. Установка Maven

```bash
# Установка Maven
brew install maven

# Проверка установки  
mvn -version
```

### 4. Установка PostgreSQL

```bash
# Установка PostgreSQL
brew install postgresql@14

# Запуск службы PostgreSQL
brew services start postgresql@14

# Добавить в PATH
echo 'export PATH="/opt/homebrew/opt/postgresql@14/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

### 5. Установка Apache Tomcat

```bash
# Установка Tomcat
brew install tomcat

# Запуск Tomcat
brew services start tomcat

# Tomcat будет доступен по адресу: http://localhost:8080
```

## Настройка базы данных

### 1. Создание базы данных и пользователя

```bash
# Подключение к PostgreSQL как superuser
psql postgres

# В psql выполнить:
CREATE DATABASE kursach;
CREATE USER project_role WITH PASSWORD 'project_role';
GRANT ALL PRIVILEGES ON DATABASE kursach TO project_role;

# Подключение к созданной базе
\c kursach

# Предоставление дополнительных прав
GRANT ALL PRIVILEGES ON SCHEMA public TO project_role;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO project_role;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO project_role;

# Выход из psql
\q
```

### 2. Создание таблиц и тестовых данных

```bash
# Выполнение SQL-скрипта для создания таблиц
psql -h localhost -U project_role -d kursach -f src/main/resources/create_tables.sql
```

## Сборка и развертывание

### 1. Сборка проекта

```bash
# Сборка проекта
mvn clean package
```

### 2. Развертывание на Tomcat

```bash
# Перезапуск Tomcat
brew services restart tomcat

# Копирование WAR-файла в webapps Tomcat
cp ./target/kursach.war /usr/local/opt/tomcat/libexec/webapps/

# Или для Apple Silicon:
cp ./target/kursach.war /opt/homebrew/var/lib/tomcat/webapps/
```

### 3. Проверка развертывания

1. Открыть браузер и перейти по адресу: http://localhost:8080/kursach
2. Должна отобразиться главная страница приложения

## Структура проекта

```
src/
├── main/
│   ├── java/org/kursach/kursach/
│   │   ├── config/           # Конфигурация CDI, JPA, безопасности
│   │   ├── controller/       # JSF контроллеры (ViewScoped beans)
│   │   ├── converter/        # JSF конвертеры для entity объектов
│   │   ├── model/           # JPA сущности
│   │   ├── repository/      # Data Access Layer (Repository pattern)
│   │   ├── security/        # Аутентификация и авторизация
│   │   └── service/         # Бизнес-логика
│   ├── resources/
│   │   ├── create_tables.sql # SQL-скрипт создания БД
│   │   └── META-INF/
│   │       └── persistence.xml # JPA конфигурация
│   └── webapp/
│       ├── *.xhtml          # JSF страницы
│       ├── WEB-INF/         # Веб-конфигурация
│       └── resources/       # CSS, JS, изображения
└── test/                    # Модульные тесты
```

## Основные функции

### Управление лицами
- Создание, редактирование, удаление записей о лицах
- Поиск по шифру и ИНН
- Привязка граждан к лицам

### Управление гражданами  
- Ведение личных данных граждан
- Связь с документами
- Поиск по персональному номеру

### Управление документами
- Регистрация документов граждан
- Информация о серии, органе выдачи, дате выдачи
- Привязка к конкретному гражданину

### Управление пользователями
- Регистрация пользователей системы
- Управление ролями и правами доступа
- Отслеживание активности и блокировка

## Полезные команды

```bash
# Просмотр логов Tomcat
tail -f /usr/local/opt/tomcat/libexec/logs/catalina.out

# Остановка Tomcat
brew services stop tomcat

# Перезапуск PostgreSQL
brew services restart postgresql@14

# Подключение к базе данных
psql -h localhost -U project_role -d kursach

# Очистка и пересборка проекта
mvn clean install

# Запуск тестов
mvn test
```

## Устранение неполадок

### Проблемы с подключением к БД
1. Проверить, что PostgreSQL запущен: `brew services list | grep postgresql`
2. Проверить настройки в `persistence.xml`
3. Убедиться, что пользователь `project_role` имеет необходимые права

### Проблемы с Tomcat
1. Проверить логи: `/usr/local/opt/tomcat/libexec/logs/catalina.out`
2. Убедиться, что порт 8080 свободен: `lsof -i :8080`
3. Перезапустить Tomcat: `brew services restart tomcat`

### Проблемы сборки
1. Проверить версию Java: `java -version` (должна быть 17+)
2. Очистить кэш Maven: `mvn clean`
3. Проверить доступность зависимостей: `mvn dependency:resolve`

## Автор

Проект разработан в рамках курсовой работы по Java EE. 