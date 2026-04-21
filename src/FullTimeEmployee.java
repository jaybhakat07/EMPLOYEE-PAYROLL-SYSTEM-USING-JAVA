/**
 * FullTimeEmployee - Represents a salaried full-time employee.
 *
 * Demonstrates: Inheritance from Employee, method Overriding
 * Salary = fixed monthly salary
 */
public class FullTimeEmployee extends Employee {
    private double monthlySalary;

    public FullTimeEmployee(String name, int id, double monthlySalary) {
        super(name, id);
        this.monthlySalary = monthlySalary;
    }

    @Override
    public double calculateSalary() {
        return monthlySalary;
    }

    public double getMonthlySalary() { return monthlySalary; }
}
