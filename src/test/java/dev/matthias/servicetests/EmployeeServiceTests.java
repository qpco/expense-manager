package dev.matthias.servicetests;

import dev.matthias.entities.Employee;
import dev.matthias.service.EmployeeService;
import dev.matthias.service.EmployeeServiceImpl;
import dev.matthias.exceptions.EmployeeNotFoundException;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeServiceTests {

    static EmployeeService employeeService = new EmployeeServiceImpl();
    Employee testEmployee;

    @BeforeEach
    void setup() {
       testEmployee = new Employee(1000, "Bob", "Barker");
    }

    @AfterEach
    void teardown() {
        testEmployee = null;
    }

    @Test
    @Order(1)
    @DisplayName("Should create new employee")
    void shouldCreateNewEmployee() {
        Employee savedEmployee = employeeService.createEmployee(testEmployee);
        Assertions.assertNotNull(savedEmployee);
    }

    @Test
    @Order(3)
    @DisplayName("Should get employee")
    void shouldGetEmployee() throws EmployeeNotFoundException {
        Employee retrievedEmployee = employeeService.readEmployee(testEmployee.getId());
        Assertions.assertEquals("Bob", retrievedEmployee.getFirstName());
    }

    @Test
    @DisplayName("Should not get nonexistent employee")
    void shouldNotGetNonexistentEmployee() throws EmployeeNotFoundException {
        Assertions.assertNull(employeeService.readEmployee(999));
        Assertions.assertNull(employeeService.readEmployee(10000));
    }

    @Test
    @Order(4)
    @DisplayName("Should update employee")
    void shouldUpdateEmployee() {
        testEmployee.setFirstName("Bobby");
        Employee updatedEmployee = employeeService.updateEmployee(testEmployee);
        Assertions.assertEquals("Bobby", updatedEmployee.getFirstName());
    }

    @Test
    @DisplayName("Should not update nonexistent employee")
    void shouldNotUpdateNonexistentEmployee() {
        testEmployee.setId(999);
        Assertions.assertNull(employeeService.updateEmployee(testEmployee));
    }

    @Test
    @Order(5)
    @DisplayName("Should delete employee")
    void shouldDeleteEmployee() {
        Assertions.assertTrue(employeeService.deleteEmployee(testEmployee.getId()));
    }

    @Test
    @DisplayName("Should not delete nonexistent employee")
    void shouldNotDeleteNonexistentEmployee() {
        Assertions.assertFalse(employeeService.deleteEmployee(999));
    }

}
