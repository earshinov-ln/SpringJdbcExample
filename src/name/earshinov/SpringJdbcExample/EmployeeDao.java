package name.earshinov.SpringJdbcExample;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

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

public class EmployeeDao {

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
		EmployeeDao.insertSql = insertSql;
	}
	
	private static String updateSql;
	public static void setUpdateSql(String updateSql) {
		EmployeeDao.updateSql = updateSql;
	}
	
	private static String findByEmpnoSql;
	public static void setFindByEmpnoSql(String findByEmpnoSql) {
		EmployeeDao.findByEmpnoSql = findByEmpnoSql;
	}
	
	private static String deleteByEmpnoSql;
	public static void setDeleteByEmpnoSql(String deleteByEmpnoSql) {
		EmployeeDao.deleteByEmpnoSql = deleteByEmpnoSql;
	}
	
	
	// CRUD operations

	/** Добавить в базу указанную запись */
	public void insert(Employee e) {
		new JdbcTemplate(dataSource).update(insertSql, e.getEmpno(), e.getName(), e.getJobTitle());
	}
	
	/** Обновить в базе запись */
	public void update(Employee e) {
		new JdbcTemplate(dataSource).update(updateSql, e.getName(), e.getJobTitle(), e.getEmpno());
	}
	
	/**
	 * Получить из базы запись с указанным идентификатором.
	 * 
	 * @return Найденная запись или @c null, если записи с указанным идентификатором нет.
	 */
	public Employee findByEmpno(int empno) {
		try {
			return new JdbcTemplate(dataSource).queryForObject(findByEmpnoSql, new EmployeeMapper(), empno);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	/** Удалить из базы запись с указанным идентификатором */
	public void deleteByEmpno(int empno) {
		new JdbcTemplate(dataSource).update(deleteByEmpnoSql, empno);
	}
	
	
	// Операции, реализованные по аналогии с проектом DbExample
	
	public void handleAll( RowCallbackHandler callback ) {
		new JdbcTemplate(dataSource).query("SELECT * FROM Employee", callback );
	}

	public void handleEmployeesByIds(List<Integer> ids, RowCallbackHandler callback) {
		new NamedParameterJdbcTemplate(dataSource).query(
			"SELECT * FROM Employee WHERE empno IN ( :ids )",
            Collections.singletonMap("ids", ids),
            callback);
	}

	public void insertWithDuplicate(final int empno, final String ename, final String jobTitle) {
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
