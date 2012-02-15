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

	private DataSource dataSource;
	private TransactionTemplate transactionTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionTemplate = new TransactionTemplate( transactionManager );
	}
	
	
	// CRUD operations

	/** Добавить в базу указанную запись */
	public void insert(Employee e) {
		String sql =
			"INSERT INTO Employee (empno, ename, job_title)" +
			"VALUES (?, ?, ?)";
		new JdbcTemplate(dataSource).update(sql, e.getEmpno(), e.getName(), e.getJobTitle());
	}
	
	/** Обновить в базе запись */
	public void update(Employee e) {
		String sql =
			"UPDATE Employee " +
			"SET ename = ?, job_title = ? " +
			"WHERE empno = ?";
		new JdbcTemplate(dataSource).update(sql, e.getName(), e.getJobTitle(), e.getEmpno());
	}

	/** Получить из базы запись с указанным идентификатором.
	 * 
	 * @return Найденная запись или @c null, если записи с указанным идентификатором нет.
	 */
	public Employee findByEmpno(int empno) {
		try {
			String sql =
				"SELECT * " +
				"FROM Employee " +
				"WHERE empno = ?";
			return new JdbcTemplate(dataSource).queryForObject(sql, new RowMapper<Employee>(){
				public Employee mapRow( ResultSet rs, int i ) throws SQLException {
					return new Employee(
							rs.getInt("empno"),
							rs.getString("ename"),
							rs.getString("job_title"));
				}
			}, empno);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/** Удалить из базы запись с указанным идентификатором */
	public void deleteByEmpno(int empno) {
		String sql =
			"DELETE FROM Employee " +
			"WHERE empno = ?";
		new JdbcTemplate(dataSource).update(sql, empno);
	}
	
	
	// операции, реализованные по аналогии с проектом DbExample
	
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
}
