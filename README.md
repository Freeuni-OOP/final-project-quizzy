[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/skmUAHf8)
# Quizzy — OOP Final Project

## Build & Run

```bash
# Build (produces target/quizzy.war)
mvn clean package

# Run all tests (39 tests, 0 failures)
mvn test
```

**Requirements**:
- Java 11+ (JDK)
- Apache Tomcat 9 (Servlet 4.0 / `javax.servlet`; Tomcat 10+ uses `jakarta.servlet` and is incompatible)
- MySQL 8+

**Setup**:
1. Create the database: `mysql -u root -p < database/schema.sql`
2. Configure `src/main/resources/hibernate.cfg.xml` with your MySQL credentials
3. Build: `mvn clean package`
4. Deploy `target/quizzy.war` to Tomcat's `webapps/` directory
5. Start Tomcat — the app is available at `http://localhost:8080/quizzy`

