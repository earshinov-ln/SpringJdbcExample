package name.earshinov.SpringJdbcExample;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
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
    private static final String SAMPLE_JOB = "Accountant";
    
    private Employee getTestEmployee() {
        return getTestEmployee(FIRST_SAMPLE_EMPNO);
    }
    
    private Employee getTestEmployee(int empno) {
    	Date hireDate = getDate(2012, 1, 1);
        return new Employee(empno, "John Smith", SAMPLE_JOB, hireDate);
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
    
    // тестирование поиска по критериям
    
    private Employee[] prepareEmployeesForCriteriaSearch() {
    	Employee[] employees = new Employee[] {
			new Employee(1, "John Smith", "Accountant", getDate(2011, 11, 2)),
	    	new Employee(2, "John Doe", "Chief accountant", getDate(2005, 2, 2)),
	    	new Employee(3, "Alice Schwarzer", "Technical director", getDate(2007, 6, 2)),	
    	};
    	for (Employee e : employees)
    		employeeDao.insert(e);
    	return employees;
    }
    
    @Test
    public void test_criteria_search_matches_everything_by_default() throws Exception {
    	Employee[] inserted = prepareEmployeesForCriteriaSearch();
    	
    	EmployeeSearchCriteria criteria = new EmployeeSearchCriteria();
    	List<Employee> found = employeeDao.findByCriteria(criteria);
    	
    	Assert.assertTrue(found.contains(inserted[0]));
    	Assert.assertTrue(found.contains(inserted[1]));
    	Assert.assertTrue(found.contains(inserted[2]));
    }
    
    @Test
    public void test_criteria_search_matches_by_name() throws Exception {
    	Employee[] inserted = prepareEmployeesForCriteriaSearch();
    	
    	EmployeeSearchCriteria criteria = new EmployeeSearchCriteria();
    	criteria.setNamePattern("John*");
    	List<Employee> found = employeeDao.findByCriteria(criteria);
    	
    	Assert.assertTrue(found.contains(inserted[0]));
    	Assert.assertTrue(found.contains(inserted[1]));
    	Assert.assertFalse(found.contains(inserted[2]));
    }
    
    @Test
    public void test_criteria_search_matches_by_hire_date_from() throws Exception {
    	Employee[] inserted = prepareEmployeesForCriteriaSearch();
    	
    	EmployeeSearchCriteria criteria = new EmployeeSearchCriteria();
    	criteria.setHireDateFromInclusive(getDate(2007, 6, 2));
    	List<Employee> found = employeeDao.findByCriteria(criteria);
    	
    	Assert.assertTrue(found.contains(inserted[0]));
    	Assert.assertFalse(found.contains(inserted[1]));
    	Assert.assertTrue(found.contains(inserted[2]));
    }
    
    @Test
    public void test_criteria_search_matches_by_hire_date_to() throws Exception {
    	Employee[] inserted = prepareEmployeesForCriteriaSearch();
    	
    	EmployeeSearchCriteria criteria = new EmployeeSearchCriteria();
    	criteria.setHireDateToInclusive(getDate(2007, 6, 2));
    	List<Employee> found = employeeDao.findByCriteria(criteria);
    	
    	Assert.assertFalse(found.contains(inserted[0]));
    	Assert.assertTrue(found.contains(inserted[1]));
    	Assert.assertTrue(found.contains(inserted[2]));
    }
    
    @Test
    public void test_criteria_search_handles_special_chars_in_pattern() throws Exception {
    	Employee e1 = new Employee(1, "%_#", SAMPLE_JOB, null);
    	employeeDao.insert(e1);
    	Employee e2 = new Employee(2, "a_#", SAMPLE_JOB, null);
    	employeeDao.insert(e2);
    	Employee e3 = new Employee(3, "%b#", SAMPLE_JOB, null);
    	employeeDao.insert(e3);
    	
    	EmployeeSearchCriteria criteria = new EmployeeSearchCriteria();
    	criteria.setNamePattern("%_#");
    	List<Employee> employees = employeeDao.findByCriteria(criteria);
    	
    	Assert.assertTrue(employees.contains(e1));
    	Assert.assertFalse(employees.contains(e2));
    	Assert.assertFalse(employees.contains(e3));
    }


}
