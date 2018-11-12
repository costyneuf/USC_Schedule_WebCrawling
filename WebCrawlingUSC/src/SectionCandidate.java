
public class SectionCandidate {
	
	private String sectionID, type, start_time, end_time, day, 
		instructor, building_ID;
	private int numRegistered, classCapacity, course_ID;
	
	public SectionCandidate(String sectionID, String type, String start_time, String end_time, String day,
			String instructor, String building_ID, int classCapacity, int course_ID) {

		this.sectionID = sectionID;
		this.type = type;
		this.start_time = start_time;
		this.end_time = end_time;
		this.day = day;
		this.instructor = instructor;
		this.building_ID = building_ID;
		this.numRegistered = 0;
		this.classCapacity = classCapacity;
		this.course_ID = course_ID;
	}

	public String getType() {
		return type;
	}
	
	public String insertDBString() {
		
//		if (number == null || name == null || description == null)
//			return null;
//		
//		String base = "INSERT INTO `scheduling`.`Course` ("
//				+ "`school`, `major`, `number`, `units`, `name`, "
//				+ "`description`, `semester`)\n\tVALUES (";
//		school = school.replace('"', '\'');
//		base += "\"" + school + "\", ";
//		base += "\"" + major + "\", ";
//		base += "\"" + number + "\", ";
//		base += units + ", ";
//		name = name.replace('"', '\'');
//		base += "\"" + name + "\", ";
//		description = description.replace("\"", "'");
//		base += "\"" + description + "\", ";
//		base += semester + ");";
//		
//		return base;
		
		return null;
	}
}
