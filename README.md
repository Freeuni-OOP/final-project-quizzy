[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/skmUAHf8)
# Quizzy — OOP Final Project


## Team Members

| Role | Member | Responsibilities |
|------|-----|-----------------|
| M1 | Lalita Gigaia | Quiz Engine, Scoring, DB Schema, Question hierarchy |
| M2 | Davit Chubabria | Authentication, Users, Friends, Mail, Session management |
| M3 | Nanuka Altunashvili | Frontend, JSPs, CSS, UI Design |
| **M4** | **Giorgi Megreli** | **Achievements, Administration, Site Statistics, Reporting System** |



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


```bash
mvn tomcat7:run
```

Once started, the app is available at **http://localhost:8080/**.

To stop the server, press `Ctrl+C`.


---

## Running Tests

```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=AchievementServiceTest
```


## First-Time Login

When you first start the app, you'll need to register an account. To create an admin account, register normally then promote the user via MySQL:

```sql
UPDATE users SET is_admin = TRUE WHERE username = 'your_username';
```

