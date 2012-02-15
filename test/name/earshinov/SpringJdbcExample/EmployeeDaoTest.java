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
    public void test_create_and_read() {
        Employee insertedEmployee = getTestEmployee();
        employeeDao.insert( insertedEmployee );
        Employee returnedEmployee = employeeDao.findByEmpno( insertedEmployee.getEmpno() );
        Assert.assertEquals(insertedEmployee, returnedEmployee);
    }
    
    @Test
    public void test_delete() {
        
        Employee e1 = getTestEmployee(FIRST_SAMPLE_EMPNO);
        employeeDao.insert(e1);
        
        Employee e2 = getTestEmployee(SECOND_SAMPLE_EMPNO);
        employeeDao.insert(e2);
        
        employeeDao.deleteByEmpno( e1.getEmpno() );
        Assert.assertNull(employeeDao.findByEmpno(e1.getEmpno()));
        
        // запись Employee2 не должна никуда деться
        Assert.assertEquals(e2, employeeDao.findByEmpno(e2.getEmpno()));
    }
    
    @Test
    public void test_update() {
        Employee insertedEmployee = getTestEmployee();
        employeeDao.insert(insertedEmployee);
        
        insertedEmployee.setJobTitle("Chief " + insertedEmployee.getJobTitle());
        employeeDao.update(insertedEmployee);
        
        Employee returnedEmployee = employeeDao.findByEmpno(insertedEmployee.getEmpno());
        Assert.assertEquals(insertedEmployee, returnedEmployee);
    }

}
