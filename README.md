# Library-management
Built a Library Management System in Java using Object-Oriented Programming principles and ArrayList collections. Implemented CRUD operations, book issue/return tracking, and search functionality through a menu-driven console application.

Project Structure
```
LibraryManagementSystem/
├── Main.java              # Entry point, all console menus
├── Book.java              # Book entity (Encapsulation)
├── User.java              # User entity with Role enum
├── LibraryService.java    # All business logic & JDBC operations
├── DatabaseConnection.java# Singleton DB connection
└── SQLQueries.sql         # Schema + seed data
```
---
Setup Instructions
1. Create the Database
```bash
mysql -u root -p < SQLQueries.sql
```
2. Configure Credentials
Open `DatabaseConnection.java` and update:
```java
private static final String URL      = "jdbc:mysql://localhost:3306/library_db";
private static final String USER     = "root";
private static final String PASSWORD = "your_password";   // ← change this
```
3. Add the MySQL JDBC Driver
Download `mysql-connector-j-x.x.x.jar` from https://dev.mysql.com/downloads/connector/j/
and place it in a `lib/` folder.
4. Compile
```bash
javac -cp ".;lib/mysql-connector-j-8.x.x.jar" *.java          # Windows
javac -cp ".:lib/mysql-connector-j-8.x.x.jar" *.java          # Mac/Linux
```
5. Run
```bash
java -cp ".;lib/mysql-connector-j-8.x.x.jar" Main             # Windows
java -cp ".:lib/mysql-connector-j-8.x.x.jar" Main             # Mac/Linux
```
OOP Concepts Demonstrated
Concept	Where
Classes & Objects	`Book`, `User`, `LibraryService`, `DatabaseConnection`
Encapsulation	Private fields + getters/setters in `Book`, `User`
Enum (Inheritance)	`User.Role` enum (ADMIN / MEMBER)
Exception Handling	`try-catch-finally` in all DB methods
Collections	`List<Book>`, `List<User>` via `ArrayList`
JDBC	`Connection`, `PreparedStatement`, `ResultSet`
Transactions	`issueBook()` and `returnBook()` use commit/rollback
Singleton Pattern	`DatabaseConnection.getConnection()`
SQL	CRUD, JOINs, FOR UPDATE locking
