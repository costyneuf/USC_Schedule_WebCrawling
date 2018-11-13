/**
 *    `ID` INT(11) NOT NULL,
	  `school` VARCHAR(45) NULL,
	  `major` VARCHAR(45) NULL,
	  `number` VARCHAR(45) NULL,
	  `units` FLOAT NULL,
	  `name` VARCHAR(100) NULL,
	  `description` TEXT NULL,
	  `semester` INT(11) NULL,
	  PRIMARY KEY (`ID`))
 *
 */
public class CourseCandidate {
	private String school, major, number, name, description;
	private float units;
	private int semester;
	
	private static final int MAX_MAJOR = 45, MAX_NAME = 100;
	
	private static final int DEFAULT_SEMESTER = 1;

	public CourseCandidate(String school, String major) {
		if (school.contains(" ")) school = school.substring(0, school.indexOf(" "));
		this.school = school;
		if (major.length() > MAX_MAJOR) major = major.substring(0, MAX_MAJOR);
		this.major = major;
		semester = DEFAULT_SEMESTER;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public void setName(String name) {
		// Remove spaces from the name and make sure the length of the name is no larger than 100.
		while (name.charAt(0) == ' ') name = name.substring(1);
		while (name.charAt(name.length() - 1) == ' ') name = name.substring(0, name.length() - 1);
		if (name.length() > MAX_NAME) name = name.substring(0, MAX_NAME - 3) + "...";
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setUnits(float units) {
		this.units = units;
	}
	
	public String insertDBString() {
		
		if (number == null || name == null || description == null)
			return null;
		
		String base = "INSERT INTO `scheduling`.`Course` ("
				+ "`school`, `major`, `number`, `units`, `name`, "
				+ "`description`, `semester`)\n\tVALUES (";
		school = school.replace('"', '\'');
		base += "\"" + school + "\", ";
		base += "\"" + major + "\", ";
		base += "\"" + number + "\", ";
		base += units + ", ";
		name = name.replace('"', '\'');
		base += "\"" + name + "\", ";
		description = description.replace("\"", "'");
		base += "\"" + description + "\", ";
		base += semester + ");";
		
		return base;
	}
	
	/*
	 * Methods inherited from Object
	 */

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getNumber() {
		return number;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((major == null) ? 0 : major.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		result = prime * result + ((school == null) ? 0 : school.hashCode());
		result = prime * result + semester;
		result = prime * result + Float.floatToIntBits(units);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CourseCandidate other = (CourseCandidate) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (major == null) {
			if (other.major != null)
				return false;
		} else if (!major.equals(other.major))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		if (school == null) {
			if (other.school != null)
				return false;
		} else if (!school.equals(other.school))
			return false;
		if (semester != other.semester)
			return false;
		if (Float.floatToIntBits(units) != Float.floatToIntBits(other.units))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CourseCandidate [school=" + school + ", major=" + major + ", number=" + number + ", name=" + name
				+ ", description=" + description + ", units=" + units + ", semester=" + semester + "]";
	}
	
	
}
