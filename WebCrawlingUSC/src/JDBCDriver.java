import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCDriver {
	private static Connection conn = null;
	private static ResultSet rs = null;
	private static Statement stmt = null;

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/scheduling";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "root";

	public static void connect() {
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void close() {
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

	public static void addCourse(CourseCandidate course) {
		if (conn == null)
			return;
		String sql = "";
		try {
			stmt = conn.createStatement();
			sql = "SELECT * FROM Course WHERE major= " + "'" 
					+ course.getMajor() + "' AND number='" + course.getNumber() + "'";
			rs = stmt.executeQuery(sql);
			// rs.next();
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

	public static void addBuilding(BuildingCandidate building) {
		if (conn == null)
			return;
		String sql = "";
		try {
			stmt = conn.createStatement();
			sql = "SELECT * FROM Building WHERE ID= " + "'" 
					+ building.getID() + "'";
			rs = stmt.executeQuery(sql);
			// rs.next();
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
	
	public static void addSection(SectionCandidate section) {
		if (conn == null)
			return;
		String sql = "";
		try {
			stmt = conn.createStatement();
			sql = section.getSelectDBString();
			rs = stmt.executeQuery(sql);
			// rs.next();
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

	public static int getCourseId(CourseCandidate course) {
		if (conn == null) return -1;
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
