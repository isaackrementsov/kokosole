package app.models;
import java.sql.*;

import javax.sql.PooledConnection;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
public abstract class Model {
    public static String CONNECTION_STRING = "jdbc:mysql://localhost/kokosole?autoReconnect=true&useSSL=false";
    public static String USERNAME = "root";
    public static String PASSWORD = "password123";
    protected static MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
    protected static Connection conn;
    protected static void connect() throws SQLException, ClassNotFoundException {
        if(conn == null){
            conn = ds.getPooledConnection().getConnection();
        }else if(conn.isClosed()){
            conn = ds.getPooledConnection().getConnection();
        }
    }
    protected static void disconnect(){
        if(conn != null){
            try{
                conn.close();
            }catch(SQLException se){
                System.out.println("Error disconnecting: " + se);
                se.printStackTrace();
            }
        }
    }
    protected static void handleException(Exception e){
        System.out.println("SQL or Driver exception: " + e);
        e.printStackTrace();
    }
    protected static void execute(String query) throws SQLException {
        Statement st = conn.createStatement();
        st.execute(query);
        st.close();
    }
    protected static ResultSet executeQuery(String query) throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);
        return rs;
    }
    public static void init(){
        ds.setUser(USERNAME);
        ds.setPassword(PASSWORD);
        ds.setServerName("localhost");
        ds.setDatabaseName("kokosole");
        ds.setAutoReconnect(true);
        ds.setUseSSL(false);
    }
    public static void migrate(){};
}