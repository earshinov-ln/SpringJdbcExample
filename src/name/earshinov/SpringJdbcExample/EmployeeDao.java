package name.earshinov.SpringJdbcExample;

import java.util.List;

import org.springframework.jdbc.core.RowCallbackHandler;

public interface EmployeeDao {

	/** Добавить в базу указанную запись */
	public abstract void insert(Employee e);

	/** Обновить в базе запись */
	public abstract void update(Employee e);

	/**
	 * Получить из базы запись с указанным идентификатором.
	 * 
	 * @return Найденная запись или @c null, если записи с указанным идентификатором нет.
	 */
	public abstract Employee findByEmpno(int empno) throws EmployeeDaoException;

	/** Удалить из базы запись с указанным идентификатором */
	public abstract void deleteByEmpno(int empno);

	/** Выполнить переданный обработчик для каждого работника в базе */
	public abstract void handleAll(RowCallbackHandler callback);

	/** Выполнить переданный обработчик для каждого работника в базе, идентификатор которого в списке @c ids */
	public abstract void handleEmployeesByIds(List<Integer> ids, RowCallbackHandler callback);

	public abstract void insertWithDuplicate(final int empno, final String ename, final String jobTitle);
}
