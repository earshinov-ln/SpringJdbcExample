package name.earshinov.SpringJdbcExample;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import junit.framework.Assert;

import org.junit.Test;


public class EmployeeTest {

	@Test
	public void test_date_is_stored_with_utc_time_00() {
		Employee e = new Employee(1);
		
		GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		cal.set(2012, 1, 1, 12, 30, 45);
		e.setHireDate(cal.getTime());
		
		cal.setTime(e.getHireDate());
		Assert.assertEquals(2012, cal.get(Calendar.YEAR));
		Assert.assertEquals(1, cal.get(Calendar.MONTH));
		Assert.assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(0, cal.get(Calendar.HOUR));
		Assert.assertEquals(0, cal.get(Calendar.MINUTE));
		Assert.assertEquals(0, cal.get(Calendar.SECOND));
	}
	
}
