[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/skmUAHf8)
# Quizzy — OOP Final Project


## Prerequisites

Before you can run Quizzy, make sure you have the following installed:

| Tool | Minimum Version | How to Check |
|------|----------------|--------------|
| **Java JDK** | 11+ | `java -version` |
| **Apache Maven** | 3.6+ | `mvn --version` |
| **MySQL** | 8.0+ | `mysql --version` |
| **Apache Tomcat** | 9.x | Required only for production deployment (see [Option B](#option-b-deploy-to-external-tomcat)) |

> **⚠️ Important:** The project uses `javax.servlet` (Servlet 4.0). Tomcat 10+ uses `jakarta.servlet` and is **incompatible**. You must use Tomcat 9.x.

---

## Step-by-Step Setup

### 1. Start MySQL

Make sure the MySQL server is running before you proceed:

**macOS (Homebrew):**
```bash
brew services start mysql
```

**Linux (systemd):**
```bash
sudo systemctl start mysql
```

**Windows:**
```
net start MySQL80
```

### 2. Create the Database

Apply the schema file to create the `quizzy` database and all 10 tables:

```bash
mysql -u root -p < database/schema.sql
```

If your MySQL root user has no password, omit `-p`:
```bash
mysql -u root < database/schema.sql
```

Verify the tables were created:
```bash
mysql -u root quizzy -e "SHOW TABLES;"
```

Expected output:
```
announcements
answers
friendships
messages
questions
quiz_attempts
quizzes
reported_quizzes
user_achievements
users
```

### 3. Configure Database Credentials

Edit `src/main/resources/hibernate.cfg.xml` and set your MySQL username and password:

```xml
<property name="hibernate.connection.username">root</property>
<property name="hibernate.connection.password">your_password_here</property>
```

If your MySQL runs on a non-standard port or host, update the connection URL:
```xml
<property name="hibernate.connection.url">
    jdbc:mysql://localhost:3306/quizzy?serverTimezone=UTC
</property>
```

### 4. Build the Project

```bash
mvn clean package
```

This compiles all Java sources, runs the test suite, and produces `target/quizzy.war`.

---

## Running the Application

You have two options: the quick embedded server (recommended for development) or an external Tomcat installation.

### Option A: Embedded Server (Quick Start)

The project includes the `tomcat7-maven-plugin` which runs a self-contained Tomcat 7 instance directly from Maven. No Tomcat installation required.

```bash
mvn tomcat7:run
```

Once started, the app is available at **http://localhost:8080/**.

To stop the server, press `Ctrl+C`.

> **Note:** Tomcat 7 supports Servlet 3.0. The project code uses Servlet 4.0 APIs but is compatible with 3.0 at runtime (all Filter implementations include explicit `init()` and `destroy()` stubs).

### Option B: Deploy to External Tomcat

If you prefer a full Tomcat 9 installation:

1. **Download and install Tomcat 9** from [https://tomcat.apache.org/download-90.cgi](https://tomcat.apache.org/download-90.cgi)

2. **Build the WAR:**
   ```bash
   mvn clean package
   ```

3. **Deploy** — copy the WAR to Tomcat's `webapps/` directory:
   ```bash
   cp target/quizzy.war /path/to/tomcat/webapps/
   ```

4. **Start Tomcat:**
   ```bash
   # macOS/Linux
   /path/to/tomcat/bin/startup.sh

   # Windows
   C:\path\to\tomcat\bin\startup.bat
   ```

5. **Access the app** at `http://localhost:8080/quizzy`

   (If you want the app at the root context, rename the WAR to `ROOT.war` before deploying.)

---

## Running Tests

```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=AchievementServiceTest
```

The test suite uses **JUnit 4.13.2** with an **H2 in-memory database** — no MySQL connection is needed for tests. Hibernate is configured separately for tests in `src/test/resources/hibernate.cfg.xml`.

```
Tests run: 62, Failures: 0, Errors: 0, Skipped: 0
- AchievementServiceTest:     18 tests (award, dedup, thresholds, author & taker checks)
- AchievementEdgeCaseTest:     6 tests (duplicate prevention, boundary conditions)
- AuthServiceTest:             12 tests (registration, login, validation)
- PasswordUtilTest:             9 tests (hashing, salting, verification)
- ReportedQuizServiceTest:      8 tests (report creation, status transitions)
- AnnouncementServiceTest:      9 tests (CRUD, ordering, creator filtering)
```

---

## First-Time Login

When you first start the app, you'll need to register an account. To create an admin account, register normally then promote the user via MySQL:

```sql
UPDATE users SET is_admin = TRUE WHERE username = 'your_username';
```

---

## Troubleshooting

### "Can't connect to MySQL server"
MySQL isn't running. Start it with `brew services start mysql` (macOS) or `sudo systemctl start mysql` (Linux).

### "Access denied for user 'root'@'localhost'"
Your MySQL password is set but `hibernate.cfg.xml` has an empty password. Update the config file with your actual password.

### "Table 'quizzy.users' doesn't exist"
The database exists but the schema wasn't applied. Run `mysql -u root < database/schema.sql`.

### "A child container failed during start" (Tomcat)
This usually means a Filter doesn't implement `init(FilterConfig)` and `destroy()`. All filters have been updated with these stubs, but if you add a new Filter, make sure to include both methods.

### Port 8080 already in use
```bash
# Find what's using port 8080
lsof -i :8080
```
Change the port in `pom.xml` under the `tomcat7-maven-plugin` configuration, or kill the existing process.

### Hibernate `hbm2ddl.auto=validate` errors
The database schema doesn't match the entity mappings. Drop and recreate the database:
```bash
mysql -u root -e "DROP DATABASE quizzy; CREATE DATABASE quizzy;"
mysql -u root quizzy < database/schema.sql
```

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Server | Apache Tomcat 9 (Servlet 4.0, JSP 2.3) |
| Language | Java 11 (source/target level) |
| Database | MySQL 8 (InnoDB) |
| ORM | Hibernate 5.6.15 (native API, not JPA) |
| Connection Pool | HikariCP 5.1.0 |
| Testing | JUnit 4.13.2 + H2 in-memory database |
| Build | Maven (WAR packaging) |
| Client | HTML, CSS, Google Fonts (Fraunces, IBM Plex Sans), vanilla JS |

**Notable**: No Spring Framework. No JPA `EntityManager`. No embedded server. Pure Servlets + JSPs.

