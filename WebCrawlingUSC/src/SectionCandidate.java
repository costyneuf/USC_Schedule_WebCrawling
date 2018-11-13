
public class SectionCandidate {
	
	/*
	 *    Lecture Section
	      `sectionID` VARCHAR(45) NOT NULL,
  		  `type` VARCHAR(45) NULL,
		  `start_time` VARCHAR(45) NULL,
		  `end_time` VARCHAR(45) NULL,
		  `day` VARCHAR(45) NULL,
		  `instructor` VARCHAR(45) NULL,
		  `numRegistered` INT(4) NULL,
		  `classCapacity` INT(4) NULL,
		  `Building_ID` VARCHAR(4) NULL,
		  `Course_ID` INT(11) NOT NULL,
		  
		  Lab/Quiz/Discussion Section
		  `sectionID` VARCHAR(45) NOT NULL,
		  `type` VARCHAR(45) NULL,
		  `start_time` VARCHAR(45) NULL,
		  `end_time` VARCHAR(45) NULL,
		  `day` VARCHAR(45) NULL,
		  `instructor` VARCHAR(45) NULL,
		  `numRegistered` INT(4) NULL,
		  `classCapacity` INT(4) NULL,
		  `Building_ID` VARCHAR(4) NOT NULL,
		  `Course_ID` INT(11) NOT NULL,
		  `Lecture_SectionID` VARCHAR(45) NOT NULL,
	 */
	
	private String sectionID, type;
	// 24 hour format -- MTWHF
	private String start_time, end_time, day;
	private String instructor, building_ID, lectureSection_ID="";
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
	
	public void setLectureSection_ID(String lectureSection_ID) {
		this.lectureSection_ID = lectureSection_ID;
	}
	
	public String insertDBString() {
		
		String base = "INSERT INTO `scheduling`.`" + type + "_Sections` ("
				+ "`sectionID`, `type`, `start_time`, `end_time`, `day`, "
				+ "`instructor`, `numRegistered`, `classCapacity`, `Building_ID`, "
				+ "`Course_ID`" + (type.toLowerCase().equals("lecture") ? "" : ", `Lecture_SectionID`") 
				+ ")\n\tVALUES (";
		
		base += "\"" + sectionID + "\", ";
		base += "\"" + type + "\", ";
		base += "\"" + start_time + "\", ";
		base += "\"" + end_time + "\", ";
		base += "\"" + day + "\", ";
		base += "\"" + instructor + "\", ";
		base += numRegistered + ", ";
		base += classCapacity + ", ";
		base += "\"" + building_ID + "\", ";
		base += course_ID;
		base += type.toLowerCase().equals("lecture") ? ");" : ", \"" + lectureSection_ID + "\");";
		
		return base;
	}
}
