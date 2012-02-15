package name.earshinov.SpringJdbcExample;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

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
    	Date hireDate = getDate(2012, 1, 1);
        return new Employee(empno, "John Smith", "Accoutant", hireDate);
    }
    
    /** Получить экземпляр Date, установленный на заданный день */
    private static Date getDate(int year, int month, int day)
    {
    	Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    	cal.setTimeInMillis(0L); // сбрасываем часы, минуты, секунды
    	cal.set(year, month, day);
    	return cal.getTime();
    }
    
 
    @Test
    public void test_create_and_read() throws Exception {
        Employee insertedEmployee = getTestEmployee();
        employeeDao.insert( insertedEmployee );
        Employee returnedEmployee = employeeDao.findByEmpno( insertedEmployee.getEmpno() );
        
        Assert.assertEquals(insertedEmployee.getHireDate(), returnedEmployee.getHireDate()); // FIXME: удалить!!
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
