# 🏢 Employee Payroll System — Java + JDBC + MySQL

A terminal-based Employee Payroll System built with **Java**, **JDBC**, and **MySQL**. Demonstrates core OOP principles and DBMS concepts including database normalization (1NF, 2NF, 3NF).

---

## 🔍 Features

- ✅ **JDBC Integration** — connects Java to a real MySQL database
- ✅ **Normalized Database** — tables designed in 3NF (Third Normal Form)
- ✅ **OOP Principles** — Abstraction, Encapsulation, Inheritance, Polymorphism
- ✅ **CRUD Operations** — Add, View, Search, and Remove employees
- ✅ **Interactive Menu** — easy terminal interface

---

## 📁 Project Structure

```
src/
├── Main.java                 ← Entry point (interactive menu)
├── Employee.java             ← Abstract base class
├── FullTimeEmployee.java     ← Full-time employee (fixed monthly salary)
├── PartTimeEmployee.java     ← Part-time employee (hours × rate)
├── PayrollSystem.java        ← All JDBC/SQL operations
└── DatabaseConnection.java   ← Singleton DB connection manager

database/
└── setup.sql                 ← Run this first! Creates DB + sample data

lib/
└── mysql-connector-j.jar     ← Download separately (see Setup below)
```

---

## 🗄️ Database Design (Normalization)

| Table | Purpose | Normal Form |
|---|---|---|
| `Department` | Stores department info | 3NF |
| `Employee` | Employee identity + type + FK to dept | 3NF |
| `FullTimeSalary` | Monthly salary for full-time employees | 3NF |
| `PartTimeSalary` | Hours & hourly rate for part-time employees | 3NF |

---

## 🚀 Setup & Run

### Step 1 — Prerequisites
- Java JDK 8+
- MySQL Server running locally
- Download MySQL JDBC Driver JAR:
  👉 https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar
  → Place it in the `lib/` folder

### Step 2 — Create the Database
Open **MySQL Workbench**, run `database/setup.sql`

### Step 3 — Update Your Password
In `src/DatabaseConnection.java`, set your MySQL password:
```java
private static final String DB_PASSWORD = "your_password_here";
```

### Step 4 — Compile & Run
```bash
# Compile
javac -cp "lib/mysql-connector-j-8.3.0.jar" -d bin src/*.java

# Run
java -cp "bin;lib/mysql-connector-j-8.3.0.jar" Main
```

---

## 🌟 OOP Concepts Demonstrated

| Concept | Where Used |
|---|---|
| **Abstraction** | `Employee` abstract class with `calculateSalary()` |
| **Inheritance** | `FullTimeEmployee` and `PartTimeEmployee` extend `Employee` |
| **Polymorphism** | Overloaded `addEmployee()` method |
| **Encapsulation** | Private fields with public getters |
