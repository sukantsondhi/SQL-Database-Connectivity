import java.sql.*;
import java.io.*;

//Model Solution to lab 7,JDBC,Nov 2018,DB course,Royal Holloway,University of London\\this will work on linux.cim.If you want to work from your local\\computer,connecting to the posgresql server on linux.cim consult the"tunneling"pdf on moodle.

public class DBLab7_2018 {

	private static final String ChurchRoad = null;

	public static Connection connectToDatabase(String user, String password,
			String database) {
		System.out.println("-------- PostgreSQL "
				+ "JDBC Connection Testing ------------");

		Connection connection = null;

		try {
			connection = DriverManager.getConnection("jdbc:postgresql://teachdb.cs.rhul.ac.uk/CS2855/<username>",
					user, password);
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		}
		return connection;
	}

	public static ResultSet executeSelect(Connection connection, String query) {
		Statement st = null;
		try {
			st = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		ResultSet rs = null;
		try {
			rs = st.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		return rs;
	}

	public static void dropTable(Connection connection, String table) {
		Statement st = null;
		try {
			st = connection.createStatement();
			st.execute("DROP TABLE IF EXISTS " + table);
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void createTable(Connection connection,
			String tableDescription) {
		Statement st = null;
		try {
			st = connection.createStatement();
			st.execute("CREATE TABLE " + tableDescription);
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static int insertIntoTableFromFile(Connection connection,
			String table, String file) {



		// This is quite an ugly solution. It's better to use the batch() approach; See hints in the lab tutorial pdf. 		

		BufferedReader br = null;
		int numRows = 0;
		try {
			Statement st = connection.createStatement();
			String sCurrentLine, brokenLine[], composedLine = "";
			br = new BufferedReader(new FileReader(file));

			while ((sCurrentLine = br.readLine()) != null) {
				// Insert each line to the DB
				brokenLine = sCurrentLine.split(",");
				composedLine = "INSERT INTO customer VALUES (";
				int i;
				for (i = 0; i < brokenLine.length - 1; i++) {
					composedLine += "'" + brokenLine[i] + "',";
				}
				composedLine += "'" + brokenLine[i] + "')";
				numRows = st.executeUpdate(composedLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return numRows;
	}

	public static void main(String[] argv) throws SQLException {

		String user = "<your user name here>";

		// No need for port number; just use default
		String port = "";

		// your password;
		// **** FOR THE MINI PROJECT YOU HAVE TO
		// **** ASK THE USER FOR THE PASS AND USERNAME;
		// ***** DON'T PLUG IT HERE AS CONSTANT!!

		String password = "<PASSWORD for the database; that is, marking id>";

		// The name of the DB we have been using
		String database = "teachdb.cs.rhul.ac.uk";

		Connection connection = connectToDatabase(user, password, database);

		if (connection != null) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
			return;
		}
		// Now we're ready to work on the DB

		String query = "SELECT * FROM branch";
		ResultSet rs = executeSelect(connection, query);
		try {
			while (rs.next()) {
				System.out.print("Column 1 returned ");
				System.out.println(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		rs.close();

		dropTable(connection, "customer");
		createTable(
				connection,
				"customer(id int primary key, name varchar(15), street varchar(15), city varchar(15));");
		int rows = insertIntoTableFromFile(connection, "customer",
				"src/table.sql");
		System.out.println(rows + " rows inserted.");

		// USUALLY BETTER TO USE PREPARED STATEMENTS

		String SQL = "UPDATE customer "
				+ "SET street = ? "
				+ "WHERE id = ?";

		int affectedrows = 0;

		try (PreparedStatement pstmt = connection.prepareStatement(SQL)) {

			pstmt.setString(1, "Church Road");
			pstmt.setInt(2, 1);

			affectedrows = pstmt.executeUpdate();

		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
		System.out.println(affectedrows + " affected rows.");

	}

}
