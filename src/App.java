import java.io.*;
import java.sql.*;
import java.util.*;

public class App {

    public static Connection connectToDatabase(String user, String password, String database) {
        System.out.println("------ Testing PostgreSQL JDBC Connection ------");
        Connection connection = null;
        try {
            String protocol = "jdbc:postgresql://";
            String dbName = "/CS2855%2f";
            String fullURL = protocol + database + dbName + user;
            connection = DriverManager.getConnection(fullURL, user, password);
        } catch (SQLException e) {
            String errorMsg = e.getMessage();
            if (errorMsg.contains("authentication failed")) {
                System.out.println(
                        "ERROR: \tDatabase password is incorrect. Have you changed the password string above?");
                System.out.println("\n\tMake sure you are NOT using your university password.\n"
                        + "\tYou need to use the password that was emailed to you!");
            } else {
                System.out.println("Connection failed! Check output console.");
                e.printStackTrace();
            }
        }
        return connection;
    }

    // My Methods

    public static ResultSet executeSelect(Connection connection, int count, String query) {

        System.out.println("MySql: EXECUTING QUERY...\n");

        Statement st = null;
        ResultSet rs = null;
        try {

            st = connection.createStatement();

            if ((count % 10) == 1) {
                System.out.println("################## " + count + "st Query ###############");
            } else if ((count % 10) == 2) {
                System.out.println("################## " + count + "nd Query ###############");
            } else if ((count % 10) == 3) {
                System.out.println("################## " + count + "rd Query ###############");
            } else {
                System.out.println("################## " + count + "th Query ###############");
            }

            rs = st.executeQuery(query);

            try {
                while (rs.next()) {
                    System.out.println(rs.getString(1) + " " + rs.getString(2));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            System.out.println();
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public static void createTable(Connection connection, String tableName, String tableDescription) {

        System.out.println("MySql: EXECUTING QUERY...\n");
        try {
            Statement st;
            st = connection.createStatement();
            st.execute("CREATE TABLE " + tableName + " ( \n" + tableDescription + "\n);");
            st.close();
            System.out.println("CREATED TABLE " + tableName + " SUCCESSFULLY\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int insertIntoTableFromFile(Connection connection, String table, String filename) {

        System.out.println("MySql: EXECUTING QUERY...\n");
        System.out.println("MYSql-> INSERTING VALUES INTO " + table + " FROM " + filename + "\n");

        BufferedReader br = null;
        int numRows = 0;
        try {
            Statement st = connection.createStatement();
            String sCurrentLine, brokenLine[], composedLine = "";
            br = new BufferedReader(new FileReader(filename));

            while ((sCurrentLine = br.readLine()) != null) {
                // Insert each line to the DB
                brokenLine = sCurrentLine.split(",");
                composedLine = "INSERT INTO " + table + " VALUES (";
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

    public static void insertIntoTableFile(Connection connection, String fileDirectory, String query) {
        try {

            PreparedStatement st = connection.prepareStatement(query);
            
            BufferedReader rdr = new BufferedReader(new FileReader(fileDirectory));
        
            String lineText;
            
            while((lineText = rdr.readLine()) != null) {

                String[] dataLine = lineText.split(",");
                
                for(int i = 0; i<dataLine.length; i++) {
                    st.setObject(i+1,dataLine[i]);
                }
                st.addBatch();
            }
            
            st.executeBatch();
            rdr.close();
            System.out.println("Data has been added");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dropTable(Connection connection, String table) {

        System.out.println("MySql: EXECUTING QUERY...\n");
        Statement st = null;
        try {
            st = connection.createStatement();
            st.execute("DROP TABLE IF EXISTS " + table + " ;");
            st.close();
            System.out.println("TABLE " + table + " DROPED SUCCESSFULLY\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Username: ");
        String user = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        scanner.close();

        // String database = "teachdb.cs.rhul.ac.uk";
        String database = "localhost";

        Connection connection = connectToDatabase(user, password, database);
        if (connection != null) {
            System.out
                    .println("SUCCESS: You made it!" + "\n\t You can now take control of your database!\n");
        } else {
            System.out.println("ERROR: \tFailed to make connection!");
            System.exit(1);
        }

        // Add Code after this

        dropTable(connection, "AIRPORT CASCADE");
        dropTable(connection, "DELAYEDFLIGHTS CASCADE");

        createTable(connection, "AIRPORT",
                "AIRPORT_CODE VARCHAR(3) NOT NULL, NAME VARCHAR (60) NOT NULL, CITY VARCHAR(50) NOT NULL, STATE_CODE VARCHAR(2) NOT NULL, NUM_ARR_DELAYS INT NOT NULL DEFAULT 0, NUM_DEP_DELAYS INT NOT NULL DEFAULT 0, TOT_ARR_DELAY_MINS INT NOT NULL DEFAULT 0, PRIMARY KEY (AIRPORT_CODE)");

        createTable(connection, "DELAYEDFLIGHTS",
                "FLIGHT_ID INT NOT NULL, MONTH INT NOT NULL, DAY_OF_MONTH INT NOT NULL, DAY_OF_WEEK INT NOT NULL, DEP_TIME INT NOT NULL, SCHEDULED_DEP_TIME INT NOT NULL, ARR_TIME INT NOT NULL, SECHEDULED_ARR_TIME INT NOT NULL, UNIQUE_CARRIER VARCHAR(3) NOT NULL, FLIGHT_NUMBER INT NOT NULL, ACTUALFLIGHTTIME INT NOT NULL, SCHEDULEDFLIGHTTIME INT NOT NULL, AIR_TIME INT NOT NULL, ARR_DELAY INT NOT NULL, DEP_DELAY INT NOT NULL, ORIGIN VARCHAR(3) NOT NULL, DESTINATION VARCHAR(3) NOT NULL, DISTANCE INT NOT NULL, PRIMARY KEY (FLIGHT_ID), FOREIGN KEY (ORIGIN) REFERENCES AIRPORT (AIRPORT_CODE)");

        insertIntoTableFromFile(connection, "AIRPORT", "airport");
        insertIntoTableFromFile(connection, "DELAYEDFLIGHTS", "delayedFlights");

        String q1 = "SELECT UNIQUE_CARRIER, COUNT(*) AS NUM_DELAYS FROM DELAYEDFLIGHTS WHERE DEP_DELAY > 0 OR ARR_DELAY > 0 GROUP BY UNIQUE_CARRIER ORDER BY NUM_DELAYS DESC LIMIT 5;";

        String q2 = "SELECT DISTINCT CITY, COUNT(*) AS MOST_DELAYS FROM DELAYEDFLIGHTS, AIRPORT WHERE DEP_DELAY > 0 AND (ORIGIN = AIRPORT_CODE) GROUP BY CITY ORDER BY MOST_DELAYS DESC LIMIT 5;";

        String q3 = "SELECT DESTINATION, SUM(ARR_DELAY) AS TOTAL_DELAY_MINUTES FROM DELAYEDFLIGHTS GROUP BY DESTINATION ORDER BY TOTAL_DELAY_MINUTES DESC LIMIT 5 OFFSET 1 ;";

        String q4 = "SELECT STATE_CODE, COUNT(*) AS NUM_AIRPORTS FROM AIRPORT GROUP BY STATE_CODE HAVING COUNT(*) >= 10 ORDER BY NUM_AIRPORTS DESC;";

        String q5 = "SELECT DESTINATION, COUNT(*) AS NUM_DELAYS FROM DELAYEDFLIGHTS WHERE ORIGIN = DESTINATION AND DEP_DELAY > 0 OR ARR_DELAY > 0 GROUP BY DESTINATION ORDER BY NUM_DELAYS DESC LIMIT 5;";

        executeSelect(connection, 1, q1);
        executeSelect(connection, 2, q2);
        executeSelect(connection, 3, q3);
        executeSelect(connection, 4, q4);
        executeSelect(connection, 5, q5);

    }

}
