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
public class EmployeeDaoTest {
    
    @Autowired
    private EmployeeDao employeeDao;
 
    private static final int FIRST_SAMPLE_EMPNO = 32096;
    private static final int SECOND_SAMPLE_EMPNO = 32097;
    
    private Employee getTestEmployee() {
        return getTestEmployee(FIRST_SAMPLE_EMPNO);
    }
    
    private Employee getTestEmployee(int empno) {
        return new Employee(empno, "John Smith", "Accoutant");
    }
    
 
    @Test
    public void test_create_and_read() throws Exception {
        Employee insertedEmployee = getTestEmployee();
        employeeDao.insert( insertedEmployee );
        Employee returnedEmployee = employeeDao.findByEmpno( insertedEmployee.getEmpno() );
        Assert.assertEquals(insertedEmployee, returnedEmployee);
    }
    
    @Test(expected=EmployeeDaoException.class)
    public void test_delete() throws Exception {
        Employee e = getTestEmployee();
        employeeDao.insert(e);
        employeeDao.deleteByEmpno(e.getEmpno());
        employeeDao.findByEmpno(e.getEmpno());
    }
    
    @Test
    public void test_delete_does_not_delete_other_employees() throws Exception {
        Employee existingEmployee = getTestEmployee(FIRST_SAMPLE_EMPNO);
        employeeDao.insert(existingEmployee);
        
        Employee employeeToBeDeleted = getTestEmployee(SECOND_SAMPLE_EMPNO);
        employeeDao.insert(employeeToBeDeleted);
        employeeDao.deleteByEmpno( employeeToBeDeleted.getEmpno() );
        
        Assert.assertEquals(existingEmployee, employeeDao.findByEmpno(existingEmployee.getEmpno()));
    }
    
    @Test
    public void test_update() throws Exception {
        Employee insertedEmployee = getTestEmployee();
        employeeDao.insert(insertedEmployee);
        
        insertedEmployee.setJobTitle("Chief " + insertedEmployee.getJobTitle());
        employeeDao.update(insertedEmployee);
        
        Employee returnedEmployee = employeeDao.findByEmpno(insertedEmployee.getEmpno());
        Assert.assertEquals(insertedEmployee, returnedEmployee);
    }

}
