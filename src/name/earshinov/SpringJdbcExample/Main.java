package name.earshinov.SpringJdbcExample;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.RowCallbackHandler;

public class Main {
	
	public static final String USAGE =
		"Использование: java name.eashinov.DbExample.Main <КОМАНДА> <АРГУМЕНТЫ КОМАНДЫ>\n" +
		"\n" +
		"Доступные команды:\n" +
		"\n" +
		"    list-all - показать все записи из базы\n" +
		"    list-by-empno [EMPNO [...]] - показать записи с заданными EMPNO\n" +
		"    insert-with-duplicate EMPNO ENAME JOB_TITLE\n" +
		"\n" +
		"Команда insert-with-duplicate добавляет в базу заданного Employee, а также\n" +
		"запись-дубликат с EMPNO, увеличенным на 1, ссылающуюся на первую запись\n" +
		"посредством внешнего ключа DUPLICATE_EMPNO.";
	
	// Консольный интерфейс к программе
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println(USAGE);
			System.exit(1);
		}

		final String command = args[0];
		final List<String> remainingArgs = Arrays.asList(args).subList(1, args.length);
		try {
			if (command.equals("list-all"))
				executeListAll(remainingArgs);
			else if (command.equals("list-by-empno"))
				executeListByEmpno(remainingArgs);
			else if (command.equals("insert-with-duplicate"))
				executeInsertWithDuplicate(remainingArgs);
			else {
				System.err.println("Неизвестная команда: " + command);
				System.err.println(USAGE);
				System.exit(1);
			}
		}
		catch (CommandException e) {
			System.err.println("Ошибка при выполнении команды " + command);
			System.err.println(e.getMessage());
			System.err.println();
			System.err.println(USAGE);
			System.exit(1);
		}
		catch (Throwable e) {
			System.err.println("Ошибка при выполнении команды " + command);
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
	
	// Реализация команды list-all.
	// Демонстрация использования Statement.
	// Выводит все записи из базы
	private static void executeListAll(List<String> commandArgs)
		throws CommandException {

		if (commandArgs.size() > 0)
			throw new CommandException("Команда list-all не принимает аргументы");
		
		getEmployeeDao().handleAll(new EmployeePrinter());
	}	
	
	// Реализация команды list-by-empno.
	// Выводит записи из базы с empno, заданными в аргументах.
	private static void executeListByEmpno(List<String> commandArgs)
		throws CommandException {
		
		List<Integer> ids = new ArrayList<Integer>();
		try {
			for (String empnoString : commandArgs)
				ids.add(Integer.parseInt(empnoString));
		}
		catch (NumberFormatException e) {
			throw new CommandException("EMPNO должен быть числом");
		}
		
		getEmployeeDao().handleEmployeesByIds(ids, new EmployeePrinter());
	}
	
	// Реализация команды insert-with-duplicate.
	// Демонстрация управления транзакциями.
	// Добавление двух связанных записи в таблицу Employee
	private static void executeInsertWithDuplicate(List<String> commandArgs)
		throws CommandException {

		if (commandArgs.size() != 3)
			throw new CommandException("Некорректное число аргументов");

		// считываем из аргументов значения для вставки
		String empnoString = commandArgs.get(0);
		int empno;
		try {
			empno = Integer.parseInt(empnoString);
		}
		catch (NumberFormatException e) {
			throw new CommandException("EMPNO должен быть числом: \"" + empnoString + "\"");
		}
		String ename = commandArgs.get(1);
		String jobTitle = commandArgs.get(2);
		
		getEmployeeDao().insertWithDuplicate( new Employee(empno, ename, jobTitle) );
	}
	
	
	private static EmployeeDao getEmployeeDao() {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-context.xml");
		EmployeeDao employeeDao = context.getBean(EmployeeDaoBean.class);
		return employeeDao;
	}
	
	private static class EmployeePrinter implements RowCallbackHandler {
		
		@Override
		public void processRow(ResultSet rs) throws SQLException {
			printEmployee(rs);
		}
	}
	
	// Печать записи из таблицы Employee, на которую установлен ResultSet
	private static void printEmployee(ResultSet rs) throws SQLException {
		int empno = rs.getInt("EMPNO");
		String ename = rs.getString("ENAME");
		String jobTitle = rs.getString("JOB_TITLE");
		System.out.println("" + empno + ", " + ename + ", " + jobTitle);
	}

}
