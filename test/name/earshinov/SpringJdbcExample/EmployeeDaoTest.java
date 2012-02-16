package name.earshinov.SpringJdbcExample;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;
import name.earshinov.Utils.DateUtils;

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
    private static final int THIRD_SAMPLE_EMPNO = 32098;
    private static final String SAMPLE_JOB = "Accountant";
    private static final Date SAMPLE_HIRE_DATE = DateUtils.getDate(2012, 1, 1);
    
    private Employee getTestEmployee() {
        return getTestEmployee(FIRST_SAMPLE_EMPNO);
    }
    
    private Employee getTestEmployee(int empno) {
        return new Employee(empno, "John Smith", SAMPLE_JOB, SAMPLE_HIRE_DATE);
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
			new Employee(1, "John Smith", "Accountant", DateUtils.getDate(2011, 11, 2)),
	    	new Employee(2, "John Doe", "Chief accountant", DateUtils.getDate(2005, 2, 2)),
	    	new Employee(3, "Alice Schwarzer", "Technical director", DateUtils.getDate(2007, 6, 2)),	
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
    	criteria.setHireDateFromInclusive(DateUtils.getDate(2007, 6, 2));
    	List<Employee> found = employeeDao.findByCriteria(criteria);
    	
    	Assert.assertTrue(found.contains(inserted[0]));
    	Assert.assertFalse(found.contains(inserted[1]));
    	Assert.assertTrue(found.contains(inserted[2]));
    }
    
    @Test
    public void test_criteria_search_matches_by_hire_date_to() throws Exception {
    	Employee[] inserted = prepareEmployeesForCriteriaSearch();
    	
    	EmployeeSearchCriteria criteria = new EmployeeSearchCriteria();
    	criteria.setHireDateToInclusive(DateUtils.getDate(2007, 6, 2));
    	List<Employee> found = employeeDao.findByCriteria(criteria);
    	
    	Assert.assertFalse(found.contains(inserted[0]));
    	Assert.assertTrue(found.contains(inserted[1]));
    	Assert.assertTrue(found.contains(inserted[2]));
    }
    
    @Test
    public void test_criteria_search_works_with_several_parameters() throws Exception {
    	Employee e = new Employee(FIRST_SAMPLE_EMPNO, "John Smith", SAMPLE_JOB, SAMPLE_HIRE_DATE);
    	employeeDao.insert(e);
    	
    	EmployeeSearchCriteria criteria = new EmployeeSearchCriteria();
    	criteria.setNamePattern("*hn?Sm*");
    	criteria.setHireDateFromInclusive(SAMPLE_HIRE_DATE);
    	criteria.setHireDateToInclusive(SAMPLE_HIRE_DATE);
		List<Employee> found = employeeDao.findByCriteria(criteria );
		
		Assert.assertTrue(found.contains(e));
    }
    
    @Test
    public void test_criteria_search_handles_special_chars_in_pattern() throws Exception {
    	Employee e1 = new Employee(FIRST_SAMPLE_EMPNO, "%_#", SAMPLE_JOB, null);
    	employeeDao.insert(e1);
    	Employee e2 = new Employee(SECOND_SAMPLE_EMPNO, "a_#", SAMPLE_JOB, null);
    	employeeDao.insert(e2);
    	Employee e3 = new Employee(THIRD_SAMPLE_EMPNO, "%b#", SAMPLE_JOB, null);
    	employeeDao.insert(e3);
    	
    	EmployeeSearchCriteria criteria = new EmployeeSearchCriteria();
    	criteria.setNamePattern("%_#");
    	List<Employee> employees = employeeDao.findByCriteria(criteria);
    	
    	Assert.assertTrue(employees.contains(e1));
    	Assert.assertFalse(employees.contains(e2));
    	Assert.assertFalse(employees.contains(e3));
    }
    
    @Test
    public void test_criteria_search_supports_star_wildcard() throws Exception {
    	Employee e = new Employee(FIRST_SAMPLE_EMPNO, "ab", SAMPLE_JOB, SAMPLE_HIRE_DATE);
    	employeeDao.insert(e);
    	
    	EmployeeSearchCriteria criteria = new EmployeeSearchCriteria();
    	criteria.setNamePattern("*");
		List<Employee> found = employeeDao.findByCriteria(criteria );
		
		Assert.assertTrue(found.contains(e));
    }
    
    @Test
    public void test_criteria_search_supports_quotation_mark_wildcard() throws Exception {
    	Employee e1 = new Employee(FIRST_SAMPLE_EMPNO, "a", SAMPLE_JOB, SAMPLE_HIRE_DATE);
    	Employee e2 = new Employee(SECOND_SAMPLE_EMPNO, "ab", SAMPLE_JOB, SAMPLE_HIRE_DATE);
    	employeeDao.insert(e1);
    	employeeDao.insert(e2);
    	
    	EmployeeSearchCriteria criteria = new EmployeeSearchCriteria();
    	criteria.setNamePattern("?");
		List<Employee> found = employeeDao.findByCriteria(criteria );
		
		Assert.assertTrue(found.contains(e1));
		Assert.assertFalse(found.contains(e2));
    }
    
    @Test
    public void test_criteria_search_pattern_is_case_sensitive() throws Exception {
    	Employee e1 = new Employee(FIRST_SAMPLE_EMPNO, "a", SAMPLE_JOB, SAMPLE_HIRE_DATE);
    	Employee e2 = new Employee(SECOND_SAMPLE_EMPNO, "A", SAMPLE_JOB, SAMPLE_HIRE_DATE);
    	employeeDao.insert(e1);
    	employeeDao.insert(e2);
    	
    	EmployeeSearchCriteria criteria = new EmployeeSearchCriteria();
    	criteria.setNamePattern("a");
		List<Employee> found = employeeDao.findByCriteria(criteria );
		
		Assert.assertTrue(found.contains(e1));
		Assert.assertFalse(found.contains(e2));
    }

}
