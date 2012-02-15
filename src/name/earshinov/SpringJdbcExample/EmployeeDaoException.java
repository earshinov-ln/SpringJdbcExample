package name.earshinov.SpringJdbcExample;

public class EmployeeDaoException extends Exception {

	private static final long serialVersionUID = 1L;

	public EmployeeDaoException() {
	}

	public EmployeeDaoException(String message) {
		super(message);
	}

	public EmployeeDaoException(Throwable cause) {
		super(cause);
	}

	public EmployeeDaoException(String message, Throwable cause) {
		super(message, cause);
	}

}
