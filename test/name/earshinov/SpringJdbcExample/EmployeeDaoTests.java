package name.earshinov.SpringJdbcExample;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/spring-context.xml")
@Transactional
public class EmployeeDaoTests {
 
    @Autowired
    private EmployeeDao employeeDao;
 
    @Test
    public void test_create_and_read() {
    	Employee insertedEmployee = new Employee(32096, "John Smith", "Accoutant");
        employeeDao.insert( insertedEmployee );
        Employee returnedEmployee = employeeDao.findByEmpno( insertedEmployee.getEmpno() );
        Assert.assertEquals(insertedEmployee, returnedEmployee);
    }
    
    @Test
    public void test_delete() {
    	
    	Employee e1 = new Employee(32096, "John Smith", "Accountant");
    	employeeDao.insert(e1);
    	
    	Employee e2 = new Employee(32097, "John Doe", "Senior Java Programmer");
    	employeeDao.insert(e2);
    	
    	employeeDao.deleteByEmpno( e1.getEmpno() );
    	Assert.assertNull(employeeDao.findByEmpno(e1.getEmpno()));
    	
    	// запись Employee2 не должна никуда деться
    	Assert.assertEquals(e2, employeeDao.findByEmpno(e2.getEmpno()));
    }
    
    @Test
    public void test_update() {
    	Employee insertedEmployee = new Employee(32096, "John Smith", "Accountant");
    	employeeDao.insert(insertedEmployee);
    	
    	insertedEmployee.setJobTitle("Chief accountant");
    	employeeDao.update(insertedEmployee);
    	
    	Employee returnedEmployee = employeeDao.findByEmpno(insertedEmployee.getEmpno());
    	Assert.assertEquals(insertedEmployee, returnedEmployee);
    }

}
