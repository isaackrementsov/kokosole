package app.models;
import java.sql.*;
public abstract class Model {
    protected static Connection conn;
    protected static void connect() throws SQLException, ClassNotFoundException {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost/kokosole?autoReconnect=true&useSSL=false";
        Class.forName(driver);
        conn = DriverManager.getConnection(url, "root", "A+4444444444vermont");
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