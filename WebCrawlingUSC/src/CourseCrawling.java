import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List; 

public class CourseCrawling {
	
	private static final int TIMEOUT = 2000000, SLEEPTIME = 100;
	private List<String> courseLinks, names, schools;
	private String currentSchool = "";
	
	public CourseCrawling() {
		courseLinks = new ArrayList<>();
		names = new ArrayList<>();
		schools = new ArrayList<>();
	}
	
	/**
	 * Output web crawling result.
	 * 
	 * @param url
	 */
	public void start(String url, String building) {
		JDBCDriver.connect();
		crawlBuildingJS(building);
		crawlTermPage(url);
		while (!courseLinks.isEmpty()) {
			String link = courseLinks.remove(0); // Course page link
			String name = names.remove(0); // Major full name
			String school = schools.remove(0);
			System.out.println("\tStart processing: " + link + "\t" + name);		
			crawlCoursePage(link, name, school);
		}
		JDBCDriver.close();
	} 
	
	private void crawlBuildingJS(String link) {
		try {
			BufferedReader reader = getReader(link);
			if (reader != null) {
				System.out.println("Adding buildings ...");
				String line  = reader.readLine();
				// Process building info.
				String startLocation = "\"code\":";
				while (line.contains(startLocation)) {
					if (line.indexOf("\"code\":null") >= 0 && line.indexOf("\"code\":null") < line.indexOf(startLocation)) {
						line = line.substring(line.indexOf("\"code\":null") 
								+ (new String("\"code\":null")).length());
					} else {
						// Get a new string line and update the original line.
						line = line.substring(line.indexOf(startLocation) + (new String("\"code\":")).length());
						
						/*
						 * Get [String ID, fullName, address;
						 *		float longitude, latitude;]
						 */
						String id = line.substring(line.indexOf("\"") + 1,
								line.indexOf("\","));
						// Check whether it is a building
						
							String fullName = line.substring(line.indexOf("\"name\":\"") + (new String("\"name\":\"")).length(),
									line.indexOf("\",\"short\":\""));
							String address = line.substring(line.indexOf("\"address\":\"") + (new String("\"address\":\"")).length(),
									line.indexOf("\",\"accessMap\":\""));
						if (id.length() == 3) {
							float longitude = Float.parseFloat(line.substring(
										line.indexOf("\"longitude\":\"") + (new String("\"longitude\":\"")).length(),
										line.indexOf("\",\"photo\":")
									));
							float latitude = Float.parseFloat(line.substring(
									line.indexOf("\"latitude\":\"") + (new String("\"latitude\":\"")).length(),
									line.indexOf("\",\"longitude\":\"")
								));
							JDBCDriver.addBuilding(new BuildingCandidate(id, fullName, address, longitude, latitude));
							System.out.println(id);
						}
					}
				}
				// Add TBA
				JDBCDriver.addBuilding(new BuildingCandidate("TBA", "TBA", "TBA"));
				System.out.println("Finish adding buildings!");
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private BufferedReader getReader(String link) throws IOException {
		BufferedReader reader = null, tmp = null;
		try {
			URL url = new URL(link);
			// Initialize a HTTP connection.
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			// Initialize a GET request.
			connection.setRequestMethod("GET");
			// Set up time out values.
			connection.setConnectTimeout(TIMEOUT);
			connection.setReadTimeout(TIMEOUT);
			// If successfully connected, then get the data from the web page.
			if (connection.getResponseCode() == 200) {
				System.out.println("\tSuccessfully connected! " + url);
				InputStream inputStream = connection.getInputStream();
				tmp = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			reader = tmp;
		}
		return reader;
	}

	private void crawlCoursePage(String link, String name, String school) {
		try {
			BufferedReader reader = getReader(link);
			if (reader != null) {
				String line = "";     
				while ((line = reader.readLine()) != null) {
					if (isCourseDescription(line)) {
						processCourseLine(line, school, name);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			Thread.sleep(SLEEPTIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	private void processCourseLine(String line, String school, String major) {

		String startLocation = "<div class=\"course-info expandable\" id=\"";
		String endLocation = "</td></tr></table></div></div>";
		while (line.contains(startLocation)) {
			int startIndex = line.indexOf(startLocation) + startLocation.length();
			int endIndex = line.indexOf(endLocation) + endLocation.length();
			// Trim the given {@line} and get the new line
			String newLine = line.substring(startIndex, endIndex);		
			line = line.substring(endIndex);
					
			// Update each course under given {@code major}
			int courseID = addCourseToDB(newLine, school, major);
			// Process section for each course 
			addSectionToDB(newLine, major, courseID);
			
		}	
	}

	private void addSectionToDB(String line, String major, int courseID) {
		if (courseID < 0) return;
		System.out.println("Requesting data for course sections...");
		String startLocation = "<tr data-section-id=", endLocation = "</tr>";
		List<SectionCandidate> sections = new ArrayList<>();
		List<SectionCandidate> lectures = new ArrayList<>();
		// Assume all labs, discussions, and quizzes can be binded with all lectures.
		List<String> lectureSection_IDs = new ArrayList<>();
		while (line.contains(startLocation)) {
			String tmp = line.substring(line.indexOf(startLocation));
			line = tmp.substring(tmp.indexOf(endLocation));
			String sectionID = getInnerHTMLByClassName(tmp, "section");
			String type = getInnerHTMLByClassName(tmp, "type");
			if (type.contains("Lecture")) type = "Lecture";
			// Process time {[x]x:xx-[x]x:xx(a/p)m}
			String[] times = processTime(getInnerHTMLByClassName(tmp, "time"));	
			// Process days
			String days = processDays(getInnerHTMLByClassName(tmp, "days").toUpperCase());
			// Process instructor
			String instructor = processInstructor(getInnerHTMLByClassName(tmp, "time"));
			// Process class capacity
			String classCapacityString = getInnerHTMLByClassName(tmp, "registered");
			classCapacityString = classCapacityString.substring(classCapacityString.indexOf("of ") + 3);
			classCapacityString = classCapacityString.substring(0, classCapacityString.indexOf("<"));
			int classCapacity = classCapacityString.contains(":") || classCapacityString.contains(" ") ? 0 : Integer.parseInt(classCapacityString);
			String building_ID = tmp.contains("\"map\"") ? getInnerHTMLByClassName(tmp, "map").substring(0,3) : "TBA";
			
			SectionCandidate section = new SectionCandidate(sectionID, type, times[0], times[1], days,
					instructor, building_ID, classCapacity, courseID);
			// Add lecture id
			if (section.isLecture()) {
				lectures.add(section);
				lectureSection_IDs.add(sectionID);
			} else {
				// Add a new section object
				sections.add(section);
			}
		}
		
		// Write data into DB
		System.out.println("Writing data into DB for course sections...");
		for (int i = 0; i < lectures.size(); i++) {
			JDBCDriver.addSection(lectures.get(i));
		}
		for (int i = 0; i < sections.size(); i++) {
			SectionCandidate section = sections.get(i);
			if (!section.isLecture()) {
				for (int j = 0; j < lectureSection_IDs.size(); j++)
					section.setLectureSection_ID(lectureSection_IDs.get(j));
			}
			JDBCDriver.addSection(section);
		}
		
	}
	
	private String processInstructor(String innerHTML) {
		if (innerHTML.contains("<a href=")) {
			innerHTML = innerHTML.substring(innerHTML.indexOf(">") + 1, innerHTML.indexOf("</"));
		}
		return innerHTML;
	}

	private String[] processTime(String time) {
		
		if (time.contains("TBA")) return new String[] {"23:58", "23:59"};
		
		String start_time = time.substring(0, time.indexOf("-"));
		String end_time = time.substring(time.indexOf("-") + 1, time.length() - 2);
		if (time.substring(time.length() - 2).contains("p")) {
			int start_h = Integer.parseInt(start_time.substring(0, start_time.indexOf(":")));
			int end_h = Integer.parseInt(end_time.substring(0, end_time.indexOf(":")));
			if ( start_h <= end_h && end_h != 12 ) {
				start_time =  (start_h + 12) + start_time.substring(start_time.indexOf(":"));
			}
			if ( end_h != 12) {
				end_time =  (end_h + 12) + end_time.substring(end_time.indexOf(":"));
			}
		}
		return new String[] {start_time, end_time};
	}

	private String getInnerHTMLByClassName(String line, String className) {
		// System.out.print("Processing " + className + ":\t");
		String startLocation = "class=\"" + className + "\">";
		line = line.substring(line.indexOf(startLocation));
		line = line.substring(0, line.indexOf("</td"));
		// System.out.println(line.substring(line.indexOf(">") + 1));
		
		return line.substring(line.indexOf(">") + 1);
		
	}

	private String processDays(String days) {
		if (days.contains("TBA")) return "MTWHF";
		String result = "";
		char[] symbols = {'M', 'T', 'W', 'H', 'F'};
		for (int i = 0; i < symbols.length; i++) {
			result += days.indexOf(symbols[i]) < 0 ? "" : symbols[i];
		}
		return result;
	}

	private int addCourseToDB(String line, String school, String major) {

		CourseCandidate course = new CourseCandidate(school, major);
		
		/*
		 * Update number, name, description, units
		 */
		
		// Get number
		int startIndex = line.indexOf("<strong>" + major + " ") 
				+ (new String("<strong>" + major + " ")).length();
		int endIndex = line.indexOf(":</strong>");
		String number = line.substring(startIndex, endIndex);
		course.setNumber(number);
		// Get name
		startIndex = line.indexOf("</strong>") + (new String("</strong>")).length();
		endIndex = line.indexOf("<span class=\"uni");
		String name = line.substring(startIndex, endIndex);
		line = line.substring(endIndex + 6);
		course.setName(name);
		// Get units
		float units = Float.parseFloat(line.substring(line.indexOf("(") + 1, line.indexOf("(") + 4));
		course.setUnits(units);
		// Get description
		startIndex = line.indexOf("<div class=\"catalogue\">") + (new String("<div class=\"catalogue\">")).length();
		line = line.substring(startIndex);
		endIndex = line.indexOf("</div>");
		String description = line.substring(0, endIndex);
		course.setDescription(description);
		
		// Insert into database
		JDBCDriver.addCourse(course);
		
		return JDBCDriver.getCourseId(course);
	}

	/**
	 * 
	 * @param host  
	 * @param linkMap 
	 * 
	 * @return 
	 * */
	private void crawlTermPage(String link) {
		try {
			BufferedReader reader = getReader(link);
			if (reader != null) {
				String line = "";
				while ((line = reader.readLine()) != null) {
					while (isCourseOption(line)) {

						// Trim new lines to get abbreviation, name pairs
						int startIndex = line.indexOf("<option value=");
						int endIndex = line.indexOf("</option>") + (new String("</option>")).length();
						String newLine = line.substring(startIndex, endIndex);
						// Update line
						line = line.substring(endIndex);
						
						// Skip all department options, ge, nursing courses, and graduate studies courses.
						if (!newLine.contains("disabled") 
								&& !newLine.contains("Graduate Studies")
								&& !newLine.contains("Nursing")
								&& !newLine.contains("Category A")
								&& !newLine.contains("Category B")
								&& !newLine.contains("Category C")
								&& !newLine.contains("Category D")
								&& !newLine.contains("Category E")
								&& !newLine.contains("Category F")
								&& !newLine.contains("Category G")
								&& !newLine.contains("Category H")) {
							// Process abbreviation and course name
							startIndex = newLine.indexOf("value=\"") + (new String("value=\"")).length();
							endIndex = newLine.indexOf("\">- ");
							String abbreviation = newLine.substring(startIndex, endIndex);
							endIndex = newLine.indexOf(" |");
							startIndex = newLine.indexOf("\">- ") + 4;
							// String courseName = newLine.substring(startIndex, endIndex);
							// System.out.println(abbreviation + ": " + courseName);
							
							// Add links into course links map
							courseLinks.add(link + "/classes/" + abbreviation);
							names.add(abbreviation.toUpperCase());
							schools.add(currentSchool);
						} else if (newLine.contains("disabled")) {
							/*
							 * Update current school
							 */
							startIndex = newLine.indexOf("disabled=\"disabled\">") + (new String("disabled=\"disabled\">")).length();
							endIndex = newLine.indexOf("</option>");
							currentSchool = newLine.substring(startIndex, endIndex);
						}

					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private boolean isCourseOption(String line) {
		if (line.contains("<option value=\"") 
				&& line.contains("\">- ") 
				&& line.contains("</option>")) 
			return true;
		return false;
	}
	
	private boolean isCourseDescription(String line) {
		if (line.contains("<div class=\"course-info expandable\""))
			return true;
		return false;
	}
}
