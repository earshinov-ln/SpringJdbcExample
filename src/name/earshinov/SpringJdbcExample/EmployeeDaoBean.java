package name.earshinov.SpringJdbcExample;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class EmployeeDaoBean implements EmployeeDao {

	// Dependency injection
	
	private DataSource dataSource;
	private TransactionTemplate transactionTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionTemplate = new TransactionTemplate( transactionManager );
	}
	
	
	// SQL-запросы
	
	private static String insertSql;
	public static void setInsertSql(String insertSql) {
		EmployeeDaoBean.insertSql = insertSql;
	}
	
	private static String updateSql;
	public static void setUpdateSql(String updateSql) {
		EmployeeDaoBean.updateSql = updateSql;
	}
	
	private static String findByEmpnoSql;
	public static void setFindByEmpnoSql(String findByEmpnoSql) {
		EmployeeDaoBean.findByEmpnoSql = findByEmpnoSql;
	}
	
	private static String deleteByEmpnoSql;
	public static void setDeleteByEmpnoSql(String deleteByEmpnoSql) {
		EmployeeDaoBean.deleteByEmpnoSql = deleteByEmpnoSql;
	}
	
	
	// CRUD operations

	@Override
	public void insert(Employee e) {
		NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(dataSource);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("empno", e.getEmpno());
		params.put("name", e.getName());
		params.put("jobTitle", e.getJobTitle());
		jdbc.update(insertSql, params);
	}
	
	@Override
	public void update(Employee e) {
		NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(dataSource);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("empno", e.getEmpno());
		params.put("name", e.getName());
		params.put("jobTitle", e.getJobTitle());
		jdbc.update(updateSql, params);
	}
	
	@Override
	public Employee findByEmpno(int empno) throws EmployeeDaoException {
		NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(dataSource);
		try {
			return jdbc.queryForObject(findByEmpnoSql,
					Collections.singletonMap("empno", empno),
					new EmployeeMapper());
		} catch (EmptyResultDataAccessException e) {
			throw new EmployeeDaoException("Employee with employee number " + empno + " not found", e);
		}
	}
	
	@Override
	public void deleteByEmpno(int empno) {
		NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(dataSource);
		jdbc.update(deleteByEmpnoSql, Collections.singletonMap("empno", empno));
	}
	
	
	// Операции, реализованные по аналогии с проектом DbExample
	
	@Override
	public void handleAll( RowCallbackHandler callback ) {
		new JdbcTemplate(dataSource).query("SELECT * FROM Employee", callback );
	}

	@Override
	public void handleEmployeesByIds(List<Integer> ids, RowCallbackHandler callback) {
		new NamedParameterJdbcTemplate(dataSource).query(
			"SELECT * FROM Employee WHERE empno IN ( :ids )",
            Collections.singletonMap("ids", ids),
            callback);
	}

	@Override
	public void insertWithDuplicate(final int empno, final String ename, final String jobTitle) {
		
		// <http://stackoverflow.com/questions/1023907/easy-transactions-using-spring-jdbc>
		transactionTemplate.execute(new TransactionCallbackWithoutResult(){
			protected void doInTransactionWithoutResult(TransactionStatus tran) {
				
				JdbcTemplate jdbc = new JdbcTemplate(dataSource);
				
				String sql =
					"INSERT INTO Employee (EMPNO, ENAME, JOB_TITLE) " +
					"VALUES (?, ?, ?)";
				jdbc.update(sql, empno, ename, jobTitle);
				
				// проверка отката транзации:
				//int unused_ret = 1 / 0;

				sql =
					"INSERT INTO Employee (EMPNO, ENAME, JOB_TITLE, DUPLICATE_EMPNO) " +
					"VALUES (?, ?, ?, ?)";
				jdbc.update( sql, empno+1, ename, jobTitle, empno);
			}
		});
	}
	
	
	// вспомогательные классы и методы
	
	private static class EmployeeMapper implements RowMapper<Employee> {
		public Employee mapRow( ResultSet rs, int i ) throws SQLException {
			return new Employee(
					rs.getInt("empno"),
					rs.getString("ename"),
					rs.getString("job_title"));
		}
	}
}
