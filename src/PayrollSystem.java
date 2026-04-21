import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PayrollSystem - Handles all JDBC database operations.
 *
 * Operations:
 *   addEmployee()      -> INSERT into Employee + FullTimeSalary or PartTimeSalary
 *   removeEmployee()   -> DELETE from Employee (cascades to salary tables)
 *   displayEmployees() -> SELECT with JOINs across all 4 normalized tables
 *   getAllDepartments() -> SELECT from Department table
 */
public class PayrollSystem {

    private final Connection conn;

    public PayrollSystem() throws SQLException {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    // =========================================================
    // ADD EMPLOYEE
    // =========================================================

    /**
     * Adds a FullTimeEmployee to the database.
     * Inserts into Employee table, then FullTimeSalary table.
     */
    public void addEmployee(FullTimeEmployee emp, int deptId) {
        String empSQL    = "INSERT INTO Employee (emp_id, emp_name, emp_type, dept_id) VALUES (?, ?, 'FULL_TIME', ?)";
        String salarySQL = "INSERT INTO FullTimeSalary (emp_id, monthly_salary) VALUES (?, ?)";

        try {
            conn.setAutoCommit(false); // Begin transaction

            // Insert into Employee table
            try (PreparedStatement ps1 = conn.prepareStatement(empSQL)) {
                ps1.setInt(1, emp.getId());
                ps1.setString(2, emp.getName());
                ps1.setInt(3, deptId);
                ps1.executeUpdate();
            }

            // Insert into FullTimeSalary table
            try (PreparedStatement ps2 = conn.prepareStatement(salarySQL)) {
                ps2.setInt(1, emp.getId());
                ps2.setDouble(2, emp.calculateSalary());
                ps2.executeUpdate();
            }

            conn.commit(); // Commit transaction
            System.out.println("✅ Full-Time Employee added: " + emp.getName());

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { /* ignore */ }
            System.err.println("❌ Error adding full-time employee: " + e.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { /* ignore */ }
        }
    }

    /**
     * Adds a PartTimeEmployee to the database.
     * Inserts into Employee table, then PartTimeSalary table.
     */
    public void addEmployee(PartTimeEmployee emp, int deptId) {
        String empSQL    = "INSERT INTO Employee (emp_id, emp_name, emp_type, dept_id) VALUES (?, ?, 'PART_TIME', ?)";
        String salarySQL = "INSERT INTO PartTimeSalary (emp_id, hours_worked, hourly_rate) VALUES (?, ?, ?)";

        try {
            conn.setAutoCommit(false); // Begin transaction

            // Insert into Employee table
            try (PreparedStatement ps1 = conn.prepareStatement(empSQL)) {
                ps1.setInt(1, emp.getId());
                ps1.setString(2, emp.getName());
                ps1.setInt(3, deptId);
                ps1.executeUpdate();
            }

            // Insert into PartTimeSalary table
            try (PreparedStatement ps2 = conn.prepareStatement(salarySQL)) {
                ps2.setInt(1, emp.getId());
                ps2.setInt(2, emp.getHoursWorked());
                ps2.setDouble(3, emp.getHourlyRate());
                ps2.executeUpdate();
            }

            conn.commit(); // Commit transaction
            System.out.println("✅ Part-Time Employee added: " + emp.getName());

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { /* ignore */ }
            System.err.println("❌ Error adding part-time employee: " + e.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { /* ignore */ }
        }
    }

    // =========================================================
    // REMOVE EMPLOYEE
    // =========================================================

    /**
     * Removes an employee by ID.
     * Salary records are deleted automatically via ON DELETE CASCADE.
     */
    public void removeEmployee(int id) {
        String sql = "DELETE FROM Employee WHERE emp_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Employee with ID " + id + " removed successfully.");
            } else {
                System.out.println("⚠️  No employee found with ID " + id + ".");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error removing employee: " + e.getMessage());
        }
    }

    // =========================================================
    // DISPLAY ALL EMPLOYEES (JOIN across all 4 normalized tables)
    // =========================================================

    /**
     * Fetches and displays all employees with their salary and department.
     * Uses LEFT JOINs to combine data from all 4 normalized tables.
     */
    public void displayEmployees() {
        String sql =
            "SELECT e.emp_id, e.emp_name, e.emp_type, d.dept_name, " +
            "       CASE WHEN e.emp_type = 'FULL_TIME' " +
            "            THEN fts.monthly_salary " +
            "            ELSE pts.hours_worked * pts.hourly_rate " +
            "       END AS calculated_salary, " +
            "       fts.monthly_salary, pts.hours_worked, pts.hourly_rate " +
            "FROM Employee e " +
            "LEFT JOIN Department      d   ON e.dept_id  = d.dept_id  " +
            "LEFT JOIN FullTimeSalary  fts ON e.emp_id   = fts.emp_id " +
            "LEFT JOIN PartTimeSalary  pts ON e.emp_id   = pts.emp_id " +
            "ORDER BY e.emp_id";

        try (Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {

            System.out.println("\n" + "=".repeat(75));
            System.out.printf("%-6s %-20s %-12s %-18s %-15s%n",
                    "ID", "Name", "Type", "Department", "Salary (INR)");
            System.out.println("=".repeat(75));

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                int    id         = rs.getInt("emp_id");
                String name       = rs.getString("emp_name");
                String type       = rs.getString("emp_type");
                String dept       = rs.getString("dept_name");
                double salary     = rs.getDouble("calculated_salary");

                if (dept == null) dept = "N/A";

                String typeLabel = type.equals("FULL_TIME") ? "Full-Time" : "Part-Time";
                System.out.printf("%-6d %-20s %-12s %-18s ₹%-14.2f%n",
                        id, name, typeLabel, dept, salary);

                // Show salary breakdown
                if (type.equals("PART_TIME")) {
                    int    hours = rs.getInt("hours_worked");
                    double rate  = rs.getDouble("hourly_rate");
                    System.out.printf("       %s hrs x ₹%.2f/hr%n", hours, rate);
                }
            }

            if (!hasData) {
                System.out.println("  (No employees found in database)");
            }
            System.out.println("=".repeat(75) + "\n");

        } catch (SQLException e) {
            System.err.println("❌ Error displaying employees: " + e.getMessage());
        }
    }

    // =========================================================
    // SEARCH EMPLOYEE BY ID
    // =========================================================

    public void searchEmployee(int id) {
        String sql =
            "SELECT e.emp_id, e.emp_name, e.emp_type, d.dept_name, " +
            "       CASE WHEN e.emp_type = 'FULL_TIME' " +
            "            THEN fts.monthly_salary " +
            "            ELSE pts.hours_worked * pts.hourly_rate " +
            "       END AS calculated_salary " +
            "FROM Employee e " +
            "LEFT JOIN Department      d   ON e.dept_id = d.dept_id  " +
            "LEFT JOIN FullTimeSalary  fts ON e.emp_id  = fts.emp_id " +
            "LEFT JOIN PartTimeSalary  pts ON e.emp_id  = pts.emp_id " +
            "WHERE e.emp_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n--- Employee Found ---");
                    System.out.println("ID         : " + rs.getInt("emp_id"));
                    System.out.println("Name       : " + rs.getString("emp_name"));
                    System.out.println("Type       : " + rs.getString("emp_type"));
                    System.out.println("Department : " + rs.getString("dept_name"));
                    System.out.printf ("Salary     : ₹%.2f%n", rs.getDouble("calculated_salary"));
                } else {
                    System.out.println("⚠️  No employee found with ID " + id);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error searching employee: " + e.getMessage());
        }
    }

    // =========================================================
    // GET DEPARTMENTS
    // =========================================================

    /**
     * Fetches all departments from the Department table.
     */
    public List<String[]> getAllDepartments() {
        List<String[]> depts = new ArrayList<>();
        String sql = "SELECT dept_id, dept_name FROM Department ORDER BY dept_id";

        try (Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {

            while (rs.next()) {
                depts.add(new String[]{
                    String.valueOf(rs.getInt("dept_id")),
                    rs.getString("dept_name")
                });
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching departments: " + e.getMessage());
        }
        return depts;
    }
}
