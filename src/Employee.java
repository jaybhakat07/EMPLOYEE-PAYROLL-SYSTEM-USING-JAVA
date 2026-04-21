/**
 * Employee - Abstract base class (Abstraction + Encapsulation)
 *
 * Demonstrates OOP concepts:
 * - Abstraction: calculateSalary() is abstract, forcing subclasses to implement
 * - Encapsulation: fields are private, accessed via getters
 */
public abstract class Employee {
    private String name;
    private int id;

    public Employee(String name, int id) {
        this.name = name;
        this.id   = id;
    }

    public String getName() { return name; }
    public int    getId()   { return id;   }

    // Abstract method — implemented differently by each subclass
    public abstract double calculateSalary();

    @Override
    public String toString() {
        return "Employee [name=" + name + ", id=" + id + ", salary=" + calculateSalary() + "]";
    }
}
