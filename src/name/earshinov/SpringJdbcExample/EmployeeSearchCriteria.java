package name.earshinov.SpringJdbcExample;

import java.util.Date;

public class EmployeeSearchCriteria {

	public String namePattern;
	public Date hireDateFromInclusive;
	public Date hireDateToInclusive;
	
	public String getNamePattern() {
		return namePattern;
	}
	public void setNamePattern(String namePattern) {
		this.namePattern = namePattern;
	}
	
	public Date getHireDateFromInclusive() {
		return hireDateFromInclusive;
	}
	public void setHireDateFromInclusive(Date hireDateFromInclusive) {
		this.hireDateFromInclusive = hireDateFromInclusive;
	}
	
	public Date getHireDateToInclusive() {
		return hireDateToInclusive;
	}
	public void setHireDateToInclusive(Date hireDateToInclusive) {
		this.hireDateToInclusive = hireDateToInclusive;
	}
}
