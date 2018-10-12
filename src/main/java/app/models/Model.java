package app.models;
import java.sql.*;
public abstract class Model {
    protected static Connection conn;
    public static String CONNECTION_STRING = "jdbc:mysql://localhost/kokosole?autoReconnect=true&useSSL=false";
    public static String USERNAME = "root";
    public static String PASSWORD = "password123";
    protected static void connect() throws SQLException, ClassNotFoundException {
        String driver = "com.mysql.jdbc.Driver";
        String url = CONNECTION_STRING;
        Class.forName(driver);
        conn = DriverManager.getConnection(url, USERNAME, PASSWORD);
    }
    protected static ResultSet executeQuery(String query) throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);
        return rs;
    }
    protected static void execute(String query) throws SQLException {
        Statement st = conn.createStatement();
        st.execute(query);
        st.close();
        conn.close();
    }
    public static void migrate(){};
}