import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Main - Entry point for the Employee Payroll System.
 *
 * Features:
 *  - Connects to MySQL database via JDBC
 *  - Interactive menu-driven console application
 *  - Add Full-Time / Part-Time employees
 *  - View all employees (with department and salary)
 *  - Search employee by ID
 *  - Remove employee by ID
 */
public class Main {

    private static PayrollSystem payrollSystem;
    private static Scanner       scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║     EMPLOYEE PAYROLL SYSTEM (JDBC)       ║");
        System.out.println("║     Connected to MySQL Database          ║");
        System.out.println("╚══════════════════════════════════════════╝\n");

        // Initialize DB connection
        try {
            payrollSystem = new PayrollSystem();
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to database: " + e.getMessage());
            System.err.println("\n💡 Possible fixes:");
            System.err.println("   1. Make sure MySQL is running.");
            System.err.println("   2. Run database/setup.sql in MySQL Workbench first.");
            System.err.println("   3. Check DB_USER and DB_PASSWORD in DatabaseConnection.java");
            System.err.println("   4. Make sure mysql-connector-j.jar is added to your project.");
            return;
        }

        // Main menu loop
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1: addFullTimeEmployee();  break;
                case 2: addPartTimeEmployee();  break;
                case 3: payrollSystem.displayEmployees(); break;
                case 4: searchEmployee();       break;
                case 5: removeEmployee();       break;
                case 6:
                    System.out.println("\nGoodbye! 👋");
                    try {
                        DatabaseConnection.getInstance().closeConnection();
                    } catch (SQLException ignored) {}
                    running = false;
                    break;
                default:
                    System.out.println("⚠️  Invalid choice. Please enter 1-6.");
            }
        }

        scanner.close();
    }

    // =========================================================
    // MENU
    // =========================================================

    private static void printMenu() {
        System.out.println("\n┌─────────────────────────────────┐");
        System.out.println("│           MAIN MENU             │");
        System.out.println("├─────────────────────────────────┤");
        System.out.println("│  1. Add Full-Time Employee      │");
        System.out.println("│  2. Add Part-Time Employee      │");
        System.out.println("│  3. Display All Employees       │");
        System.out.println("│  4. Search Employee by ID       │");
        System.out.println("│  5. Remove Employee             │");
        System.out.println("│  6. Exit                        │");
        System.out.println("└─────────────────────────────────┘");
    }

    // =========================================================
    // ADD FULL-TIME EMPLOYEE
    // =========================================================

    private static void addFullTimeEmployee() {
        System.out.println("\n--- Add Full-Time Employee ---");
        int    id     = readInt("Employee ID   : ");
        String name   = readString("Employee Name : ");
        double salary = readDouble("Monthly Salary: ₹");
        int    deptId = chooseDepartment();

        FullTimeEmployee emp = new FullTimeEmployee(name, id, salary);
        payrollSystem.addEmployee(emp, deptId);
    }

    // =========================================================
    // ADD PART-TIME EMPLOYEE
    // =========================================================

    private static void addPartTimeEmployee() {
        System.out.println("\n--- Add Part-Time Employee ---");
        int    id    = readInt("Employee ID   : ");
        String name  = readString("Employee Name : ");
        int    hours = readInt("Hours Worked  : ");
        double rate  = readDouble("Hourly Rate   : ₹");
        int    deptId = chooseDepartment();

        PartTimeEmployee emp = new PartTimeEmployee(name, id, hours, rate);
        payrollSystem.addEmployee(emp, deptId);
    }

    // =========================================================
    // SEARCH EMPLOYEE
    // =========================================================

    private static void searchEmployee() {
        int id = readInt("\nEnter Employee ID to search: ");
        payrollSystem.searchEmployee(id);
    }

    // =========================================================
    // REMOVE EMPLOYEE
    // =========================================================

    private static void removeEmployee() {
        int id = readInt("\nEnter Employee ID to remove: ");
        payrollSystem.removeEmployee(id);
    }

    // =========================================================
    // CHOOSE DEPARTMENT HELPER
    // =========================================================

    private static int chooseDepartment() {
        List<String[]> depts = payrollSystem.getAllDepartments();
        System.out.println("\nAvailable Departments:");
        for (String[] d : depts) {
            System.out.println("  [" + d[0] + "] " + d[1]);
        }
        return readInt("Select Department ID: ");
    }

    // =========================================================
    // INPUT HELPER METHODS
    // =========================================================

    private static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  ⚠️  Please enter a valid integer.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  ⚠️  Please enter a valid number.");
            }
        }
    }

    private static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
