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
	 * @throws EmployeeDaoException - запись с указанным идентификатором не найдена
	 */
	public abstract Employee findByEmpno(int empno) throws EmployeeDaoException;

	/** Удалить из базы запись с указанным идентификатором */
	public abstract void deleteByEmpno(int empno);

	/** Получить список работников, удовлетворяющих критериям поиска */
	public abstract List<Employee> findByCriteria(EmployeeSearchCriteria criteria);
	
	// Операции, реализованные по аналогии с проектом DbExample

	/** Выполнить переданный обработчик для каждого работника в базе */
	public abstract void handleAll(RowCallbackHandler callback);

	/** Выполнить переданный обработчик для каждого работника в базе, идентификатор которого в списке @c ids */
	public abstract void handleEmployeesByIds(List<Integer> ids, RowCallbackHandler callback);

	/** Добавить в базу указанную запись и дубликат (такого же сотрудника с empno на единицу больше) */
	public abstract void insertWithDuplicate(Employee e);
}