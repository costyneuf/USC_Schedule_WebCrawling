import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// Use Singleton
public class DatabaseHandler {
	private Connection conn = null;
	private ResultSet rs = null;
	private Statement stmt = null;
	
	private static DatabaseHandler dbh;

	// JDBC driver name and database URL
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost/scheduling";

	// Database credentials
	private static final String USER = "root";
	private static final String PASS = "root";

	/*
	 * ---- Private constructor ----
	 */
	private DatabaseHandler() {
		// No code needs here.
	}
	
	public static DatabaseHandler getOneInstance() {
		if (dbh == null) dbh = new DatabaseHandler();
		return dbh;
	}
	
	public void connect() {
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (conn != null) {
				conn.close();
				conn = null;
			}
			if (stmt != null) {
				stmt = null;
			}
		} catch (SQLException sqle) {
			System.out.println("connection close error");
			sqle.printStackTrace();
		}
	}

	public void addCourse(CourseCandidate course) {
		if (conn == null)
			return;
		String sql = "";
		try {
			stmt = conn.createStatement();
			sql = "SELECT * FROM Course WHERE major= " + "'" 
					+ course.getMajor() + "' AND number='" + course.getNumber() + "'";
			rs = stmt.executeQuery(sql);
			// Check whether the course has been stored
			if (!rs.next()) {
				sql = course.insertDBString();
				stmt.executeUpdate(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(sql);
		}
	}

	public void addBuilding(BuildingCandidate building) {
		if (conn == null)
			return;
		String sql = "";
		try {
			stmt = conn.createStatement();
			sql = "SELECT * FROM Building WHERE ID= " + "'" 
					+ building.getID() + "'";
			rs = stmt.executeQuery(sql);
			// Check whether the building has been stored
			if (!rs.next()) {
				sql = building.insertDBString();
				stmt.executeUpdate(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(sql);
		}
	}
	
	public void addSection(Section section) {
		if (conn == null)
			return;
		String sql = "";
		try {
			stmt = conn.createStatement();
			sql = section.getSelectDBString();
			rs = stmt.executeQuery(sql);
			// Check whether the section has been stored
			if (!rs.next()) {
				sql = section.insertDBString();
				stmt.executeUpdate(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(sql);
		}
	}

	/**
	 * Report the course id of {@code course}.
	 * 
	 * @param course
	 * @return either a positive integer, or -1 if not existed, or -2 connection loss.
	 */
	public int getCourseId(CourseCandidate course) {
		if (conn == null) return -2;
		int ID = -1;
		String sql = "";
		try {
			stmt = conn.createStatement();
			sql = "SELECT * FROM Course WHERE major= " + "'" 
					+ course.getMajor() + "' AND number='" + course.getNumber() + "'";
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				ID = Integer.parseInt(rs.getString("ID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(sql);
		}
		
		return ID;
	}
	

}
