package name.earshinov.SpringJdbcExample;

import java.util.Date;

import name.earshinov.Utils.DateUtils;

public class Employee {

	private final int empno;
	private String name;
	private String jobTitle;
	private Date hireDate;
	
	
	// Из-за отсутствия конструктора без аргументов этот класс не может являться JavaBean'ом.
	// Зато можем сделать поле идентификатора неизменным.
	public Employee(int empno) {
		this.empno = empno;
	}
	
	public Employee(int empno, String name, String jobTitle, Date hireDate) {
		this(empno);
		setName(name);
		setJobTitle(jobTitle);
		setHireDate(hireDate);
	}
		
	
	@Override
	public boolean equals(Object obj) {
		if ( ! (obj instanceof Employee))
			return false;
		Employee other = (Employee)obj;
		return other.empno == empno &&
			( other.name == null ? name == null : other.name.equals(name) ) &&
			( other.jobTitle == null ? jobTitle == null : other.jobTitle.equals(jobTitle) ) &&
			( other.hireDate == null ? hireDate == null : other.hireDate.equals(hireDate) );
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 37*result + empno;
		result = 37*result + ( name == null ? 0 : name.hashCode());
		result = 37*result + ( jobTitle == null ? 0 : jobTitle.hashCode());
		result = 37*result + ( hireDate == null ? 0 : hireDate.hashCode());
		return result;
	}
	
	
	public int getEmpno() {
		return empno;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String ename) {
		this.name = ename;
	}
	
	public String getJobTitle() {
		return jobTitle;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
	
	/**
	 * Получить дату приёма сотрудника на работу.
	 * 
	 * Возвращаемый объект всегда имеет время 00:00 по UTC
	 */
	public Date getHireDate() {
		return hireDate;
	}
	
	/**
	 * Установить дату приёма сотрудника на работу.
	 * 
	 * Из переданного экземпляра Date берётся только дата.
	 */
	public void setHireDate(Date hireDate) {
		this.hireDate = hireDate == null ? null : DateUtils.stripTime(hireDate);
	}
}
