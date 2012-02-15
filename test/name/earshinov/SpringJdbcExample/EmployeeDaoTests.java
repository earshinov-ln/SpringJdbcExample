package name.earshinov.SpringJdbcExample;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/spring-context.xml")
public class EmployeeDaoTests {
 
    @Autowired
    private EmployeeDao employeeDao;
 
    @Test
    public void test_create_and_read() {
    	Employee insertedEmployee = new Employee();
    	insertedEmployee.setEmpno(456);
    	insertedEmployee.setName("John Smith");
    	insertedEmployee.setJobTitle("Accountant");
        employeeDao.insert( insertedEmployee );
        
        Employee returnedEmployee = employeeDao.findByEmpno( insertedEmployee.getEmpno() );
        Assert.assertEquals(insertedEmployee, returnedEmployee);
    }

}
