-- ============================================================
--  Employee Payroll System - Database Setup Script
--  Normalization Level: 3NF (Third Normal Form)
-- ============================================================

-- Step 1: Create and select the database
CREATE DATABASE IF NOT EXISTS payroll_db;
USE payroll_db;

-- ============================================================
-- NORMALIZATION EXPLANATION:
--
-- 1NF: Each column has atomic values. No repeating groups.
-- 2NF: No partial dependencies (all non-key cols depend on full PK).
-- 3NF: No transitive dependencies (non-key col -> non-key col).
--
-- We split into 4 tables to satisfy all three normal forms:
--   Department    -> stores department info (no transitive dep.)
--   Employee      -> stores only employee identity + type + dept FK
--   FullTimeSalary  -> salary details only for full-time employees
--   PartTimeSalary  -> salary details only for part-time employees
-- ============================================================

-- Step 2: Drop tables if they exist (for re-runs)
DROP TABLE IF EXISTS FullTimeSalary;
DROP TABLE IF EXISTS PartTimeSalary;
DROP TABLE IF EXISTS Employee;
DROP TABLE IF EXISTS Department;

-- ============================================================
-- TABLE 1: Department (1NF, 2NF, 3NF satisfied)
--   dept_id   -> dept_name (no transitive dependency)
-- ============================================================
CREATE TABLE Department (
    dept_id    INT          PRIMARY KEY AUTO_INCREMENT,
    dept_name  VARCHAR(100) NOT NULL UNIQUE
);

-- ============================================================
-- TABLE 2: Employee (1NF, 2NF, 3NF satisfied)
--   emp_id -> emp_name, emp_type, dept_id
--   dept_id is a FK - no transitive dependency
-- ============================================================
CREATE TABLE Employee (
    emp_id     INT          PRIMARY KEY,
    emp_name   VARCHAR(100) NOT NULL,
    emp_type   ENUM('FULL_TIME', 'PART_TIME') NOT NULL,
    dept_id    INT,
    FOREIGN KEY (dept_id) REFERENCES Department(dept_id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

-- ============================================================
-- TABLE 3: FullTimeSalary (1NF, 2NF, 3NF satisfied)
--   emp_id -> monthly_salary (only full-time salary data here)
--   Avoids NULL columns and mixing salary types in one table
-- ============================================================
CREATE TABLE FullTimeSalary (
    emp_id          INT    PRIMARY KEY,
    monthly_salary  DOUBLE NOT NULL,
    FOREIGN KEY (emp_id) REFERENCES Employee(emp_id)
        ON DELETE CASCADE
);

-- ============================================================
-- TABLE 4: PartTimeSalary (1NF, 2NF, 3NF satisfied)
--   emp_id -> hours_worked, hourly_rate
--   Separate from FullTimeSalary to prevent NULL pollution
-- ============================================================
CREATE TABLE PartTimeSalary (
    emp_id        INT    PRIMARY KEY,
    hours_worked  INT    NOT NULL,
    hourly_rate   DOUBLE NOT NULL,
    FOREIGN KEY (emp_id) REFERENCES Employee(emp_id)
        ON DELETE CASCADE
);

-- ============================================================
-- SAMPLE DATA
-- ============================================================

-- Insert departments
INSERT INTO Department (dept_name) VALUES ('Engineering');
INSERT INTO Department (dept_name) VALUES ('Human Resources');
INSERT INTO Department (dept_name) VALUES ('Finance');
INSERT INTO Department (dept_name) VALUES ('Marketing');

-- Insert full-time employees
INSERT INTO Employee (emp_id, emp_name, emp_type, dept_id) VALUES (101, 'John Doe',    'FULL_TIME',  1);
INSERT INTO Employee (emp_id, emp_name, emp_type, dept_id) VALUES (103, 'Alice Brown', 'FULL_TIME',  3);
INSERT INTO Employee (emp_id, emp_name, emp_type, dept_id) VALUES (104, 'Bob Wilson',  'FULL_TIME',  2);

-- Insert part-time employees
INSERT INTO Employee (emp_id, emp_name, emp_type, dept_id) VALUES (102, 'Jane Smith',  'PART_TIME',  2);
INSERT INTO Employee (emp_id, emp_name, emp_type, dept_id) VALUES (105, 'Carol Davis', 'PART_TIME',  4);

-- Insert full-time salary details
INSERT INTO FullTimeSalary (emp_id, monthly_salary) VALUES (101, 5000.00);
INSERT INTO FullTimeSalary (emp_id, monthly_salary) VALUES (103, 7500.00);
INSERT INTO FullTimeSalary (emp_id, monthly_salary) VALUES (104, 6200.00);

-- Insert part-time salary details
INSERT INTO PartTimeSalary (emp_id, hours_worked, hourly_rate) VALUES (102, 30, 15.00);
INSERT INTO PartTimeSalary (emp_id, hours_worked, hourly_rate) VALUES (105, 20, 12.50);

-- ============================================================
-- VERIFY: View all employees with calculated salary
-- ============================================================
SELECT 
    e.emp_id,
    e.emp_name,
    e.emp_type,
    d.dept_name,
    CASE 
        WHEN e.emp_type = 'FULL_TIME' THEN fts.monthly_salary
        ELSE pts.hours_worked * pts.hourly_rate
    END AS calculated_salary
FROM Employee e
LEFT JOIN Department     d   ON e.dept_id = d.dept_id
LEFT JOIN FullTimeSalary fts ON e.emp_id  = fts.emp_id
LEFT JOIN PartTimeSalary pts ON e.emp_id  = pts.emp_id
ORDER BY e.emp_id;
