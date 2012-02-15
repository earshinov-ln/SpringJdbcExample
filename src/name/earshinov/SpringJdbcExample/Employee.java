package name.earshinov.SpringJdbcExample;

public class Employee {

	private final int empno;
	private String name;
	private String jobTitle;
	
	
	// Из-за отсутствия конструктора без аргументов этот класс не может являться JavaBean'ом.
	// Зато можем сделать поле идентификатора неизменным.
	public Employee(int empno) {
		this.empno = empno;
	}
	
	public Employee(int empno, String name, String jobTitle) {
		this(empno);
		this.name = name;
		this.jobTitle = jobTitle;
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
	
	
	@Override
	public boolean equals(Object obj) {
		if ( ! (obj instanceof Employee))
			return false;
		Employee other = (Employee)obj;
		return other.empno == empno &&
			( other.name == null && name == null || other.name.equals(name) ) &&
			( other.jobTitle == null && jobTitle == null || other.jobTitle.equals(jobTitle) );
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 37*result + empno;
		result = 37*result + ( name == null ? 0 : name.hashCode());
		result = 37*result + ( jobTitle == null ? 0 : jobTitle.hashCode());
		return result;
	}	
}
