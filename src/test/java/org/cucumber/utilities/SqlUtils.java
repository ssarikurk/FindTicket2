package org.cucumber.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlUtils {
    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;

    public static void createConnection() {
        String dbUrl = "jdbc:mysql://localhost:3306/mkmanon";
        String dbUsername = "root";
        String dbPassword = "";
        try {
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void createConnection(String dbName) {
        String dbUrl = "jdbc:mysql://localhost:3306/"+dbName+"?zeroDateTimeBehavior=convertToNull";
        String dbUsername = "root";
        String dbPassword = " ";
        try {
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            System.out.println("*************Connected**************");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("*************Connection Failed**************");
        }
    }
    public static void createConnection(String ip, String dbName) {
        String dbUrl = "jdbc:mysql://"+ip+":3306/"+dbName+"?zeroDateTimeBehavior=convertToNull";
        String dbUsername = "root";
        String dbPassword = "xxxxxxxx";
        try {
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            System.out.println("*************Connected**************");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("*************Connection Failed**************");
        }
    }

    public static void createConnection(String ip, String dbName, String user, String pass) {

        String dbUrl = "jdbc:mysql://"+ip+":3306/"+dbName+"?zeroDateTimeBehavior=convertToNull";
        System.out.println("dbUrl = " + dbUrl);
        try {
            connection = DriverManager.getConnection(dbUrl, user, pass);
            System.out.println("***Connected MySQL DB: "+dbName+" ***");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("***Connection Failed to: "+dbName+" ***");
        }
    }

    public static void destroy() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     *
     * @param query
     * @return returns a single cell value. If the results in multiple rows and/or
     *         columns of data, only first column of the first row will be returned.
     *         The rest of the data will be ignored
     */
    public static Object getCellValue(String query) {
        return getQueryResultList(query).get(0).get(0);
    }
    /**
     *
     * @param query
     * @return returns a list of Strings which represent a row of data. If the query
     *         results in multiple rows and/or columns of data, only first row will
     *         be returned. The rest of the data will be ignored
     */
    public static List<Object> getRowList(String query) {
        return getQueryResultList(query).get(0);
    }
    /**
     *
     * @param query
     * @return returns a map which represent a row of data where key is the column
     *         name. If the query results in multiple rows and/or columns of data,
     *         only first row will be returned. The rest of the data will be ignored
     */
    public static Map<String, Object> getRowMap(String query) {
        return getQueryResultMap(query).get(0);
    }
    /**
     *
     * @param query
     * @return returns query result in a list of lists where outer list represents
     *         collection of rows and inner lists represent a single row
     */
    public static List<List<Object>> getQueryResultList(String query) {
        executeQuery(query);
        List<List<Object>> rowList = new ArrayList<>();
        ResultSetMetaData rsmd;
        try {
            rsmd = resultSet.getMetaData();
            while (resultSet.next()) {
                List<Object> row = new ArrayList<>();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    row.add(resultSet.getObject(i));
                }
                rowList.add(row);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rowList;
    }
    /**
     *
     * @param query
     * @param column
     * @return list of values of a single column from the result set
     */
    public static List<Object> getColumnData(String query, String column) {
        executeQuery(query);
        List<Object> rowList = new ArrayList<>();
        ResultSetMetaData rsmd;
        try {
            rsmd = resultSet.getMetaData();
            while (resultSet.next()) {
                rowList.add(resultSet.getObject(column));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rowList;
    }
    public static List<String> getColumnStrData(String query, String column) {
        executeQuery(query);
        List<String> rowList = new ArrayList<>();
        ResultSetMetaData rsmd;
        try {
            rsmd = resultSet.getMetaData();
            while (resultSet.next()) {
                rowList.add(resultSet.getObject(column).toString());
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rowList;
    }
    /**
     *
     * @param query
     * @return returns query result in a list of maps where the list represents
     *         collection of rows and a map represents represent a single row with
     *         key being the column name
     */
    public static List<Map<String, Object>> getQueryResultMap(String query) {
        executeQuery(query);
        List<Map<String, Object>> rowList = new ArrayList<>();
        ResultSetMetaData rsmd;
        try {
            rsmd = resultSet.getMetaData();
            while (resultSet.next()) {
                Map<String, Object> colNameValueMap = new HashMap<>();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    colNameValueMap.put(rsmd.getColumnName(i), resultSet.getObject(i));
                }
                rowList.add(colNameValueMap);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rowList;
    }
    /**
     *
     * @param query
     * @return List of columns returned in result set
     */
    public static String[] getColumnNamesArray(String query) throws SQLException {
        executeQuery(query);
        ResultSetMetaData rsmd;

            rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();
            String[] columnName = new String[columnCount+1];

            for (int i = 0; i < columnCount; i++) {
                columnName[i]=rsmd.getColumnName(i);
            }

        return columnName;
    }

    public static List<String> getColumnNames(String query) {
        executeQuery(query);
        List<String> columns = new ArrayList<>();
        ResultSetMetaData rsmd;
        try {
            rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columns.add(rsmd.getColumnName(i));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return columns;
    }

    private static void executeQuery(String query) {
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void queryExecuter(String query) {
        try(Statement stmt = connection.createStatement();) {
        stmt.executeUpdate(query);
        System.out.println("Mysql Query Executed Successful");
        } catch (SQLException e) {
            e.printStackTrace();
        } 
    }


    public static void batchQueryExecuter(String[] queries) {
//        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            // Create a statement object to execute queries
            try (Statement statement = connection.createStatement()) {
                // Execute each query
                for (String query : queries) {
                    System.out.println("query = " + query);
                    try {
                        statement.executeUpdate(query);
                        System.out.println("Query executed successfully: " + query);
                    }catch (SQLException e){
                        e.printStackTrace();
                        System.out.println(query+" not executed");
                    }
                }

            } catch (SQLException e) {
            e.printStackTrace();
            }

    }
   

    public static int getRowCount() throws Exception {
        resultSet.last();
        int rowCount = resultSet.getRow();
        return rowCount;
    }
    public static int getRowCount(String query) throws Exception {
        resultSet = statement.executeQuery(query);
        resultSet.last();
        int rowCount = resultSet.getRow();
        return rowCount;
    }
    public static void createDB(String dbName) throws Exception {
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        statement.executeUpdate("CREATE DATABASE "+dbName);
        System.out.println("Database created successfully...");
    }

    public static void dropDB(String dbName) throws Exception {
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        statement.executeUpdate("DROP DATABASE "+dbName);
        System.out.println("Database dropped successfully...");
    }

    public static void importDB(String dbName) throws Exception {
        String q = "";
        File f = new File("C:\\Users\\Administrator\\Downloads\\master.sql"); // source path is the absolute path of dumpfile.
        try {
            BufferedReader bf = new BufferedReader(new FileReader(f));
            String line = null;
            line = bf.readLine();
//            System.out.println("line = " + line);
            while (line != null) {
                q = q + line + "\n";
//                System.out.println("q = " + q);
                line = bf.readLine();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
// Now we have the content of the dumpfile in 'q'.
// We must separate the queries, so they can be executed. And Java Simply does this:
        String[] commands = q.split(";");

        try {
            Statement statement = connection.createStatement();
            for (String s : commands) {
//                System.out.println("s = " + s);
                statement.execute(s);
            }
        } catch (Exception ex) {
        }
//        closeConnection(con);
        System.out.println("Database imported successfully...");
    }


}
