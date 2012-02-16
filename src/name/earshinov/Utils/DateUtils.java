package name.earshinov.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public final class DateUtils {

	private DateUtils() { }
	
	/** Получить экземпляр Date, установленный на заданный день и время 00:00 по UTC */
    public static Date getDate(int year, int month, int day) {
    	Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    	cal.setTimeInMillis(0L); // сбрасываем часы, минуты, секунды
    	cal.set(year, month, day);
    	return cal.getTime();
    }
    
    /** Получить копию переданного экземпляра Date с временем, установленным в 00:00 по UTC */
	public static Date stripTime(Date date) {
		if (date == null)
			throw new IllegalArgumentException();
		
		// <http://stackoverflow.com/questions/5050170/java-getting-date-without-time>
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTimeInMillis(0L); // сбрасываем часы, минуты, секунды
		cal.set(year, month, day);
    	return cal.getTime();
	}

	/** Получить экземпляр Date из поля типа DATE в БД, управляемой СУБД Apache Derby */
	public static Date getDateFromDerbySqlDate(ResultSet rs, String sqlColumnName) throws SQLException {
		// <http://db.apache.org/derby/papers/JDBCImplementation.html#Date+Handling>
		return rs.getDate(sqlColumnName, new GregorianCalendar(TimeZone.getTimeZone("UTC")));
	}
	
}
