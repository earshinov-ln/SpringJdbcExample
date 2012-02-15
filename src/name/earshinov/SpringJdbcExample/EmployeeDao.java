package name.earshinov.SpringJdbcExample;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

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

	public void insert(Employee e) {
		String sql =
			"INSERT INTO Employee (empno, ename, job_title)" +
			"VALUES (?, ?, ?)";
		new JdbcTemplate(dataSource).update(sql, e.getEmpno(), e.getName(), e.getJobTitle());
	}

	public Employee findByEmpno(int empno) {
		String sql =
			"SELECT * " +
			"FROM Employee " +
			"WHERE empno = ?";
		return new JdbcTemplate(dataSource).queryForObject(sql, new RowMapper<Employee>(){
			public Employee mapRow( ResultSet rs, int i ) throws SQLException {
				Employee ret = new Employee();
				ret.setEmpno(rs.getInt("empno"));
				ret.setName(rs.getString("ename"));
				ret.setJobTitle(rs.getString("job_title"));
				return ret;
			}
		}, empno);
	}
	
	
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
