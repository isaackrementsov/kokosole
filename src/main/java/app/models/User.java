package app.models;
import java.sql.*;
import java.util.UUID;
import java.util.ArrayList;
public class User extends Model {
    public String name;
    public String email;
    public String password;
    public String avatar;
    public String status;
    public String bio;
    public String country;
    public String id;
    public User(String name, String email, String password, String avatar, String status, String bio, String country, String id){
        this.name = name;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.status = status;
        this.bio = bio;
        this.country = country;
        this.id = id;
    }
    public User(String name, String email, String password, String avatar, String country){
        this(name, email, password, avatar, "inactive", "", country, UUID.randomUUID().toString());
    }
    public User(String id){
        this.id = id;
    }
    public User(String id, String email){
        this.id = id;
        this.email = email;
    }
    public void save(){
        try{
            connect();
            PreparedStatement pst = conn.prepareStatement("INSERT INTO users (name, email, pwd, avatar, uuid, sts, bio, country) values(?, ?, ?, ?, ?, ?, ?, ?)");
            pst.setString(1, this.name);
            pst.setString(2, this.email);
            pst.setString(3, this.password);
            pst.setString(4, this.avatar);
            pst.setString(5, this.id);
            pst.setString(6, this.status);
            pst.setString(7, this.bio);
            pst.setString(8, this.country);
            pst.execute();
            pst.close();   
        }catch(ClassNotFoundException | SQLException e){
            handleException(e);
        }finally{
            disconnect();
        }
    }
    public void update(){
        try{
            connect();
            PreparedStatement pst = conn.prepareStatement(
                "UPDATE users SET name=COALESCE(?, name),email=COALESCE(?, email),pwd=COALESCE(?, pwd)," + 
                "avatar=COALESCE(?, avatar),sts=COALESCE(?, sts),bio=COALESCE(?, bio),country=COALESCE(?, country) WHERE uuid=?"
            );
            pst.setString(1, this.name);
            pst.setString(2, this.email);
            pst.setString(3, this.password);
            pst.setString(4, this.avatar);
            pst.setString(5, this.status);
            pst.setString(6, this.bio);
            pst.setString(7, this.country);
            pst.setString(8, this.id);
            pst.executeUpdate();
            pst.close();
        }catch(ClassNotFoundException | SQLException e){
            handleException(e);
        }finally{
            disconnect();
        }
    }
    public static User getByID(String uuid, boolean isChain){
        try{
            connect();
            ResultSet rs = executeQuery("SELECT * FROM users WHERE uuid='" + uuid + "'");
            User user = new User(null);
            if(rs.next()){
                user = getByResultSet(rs);
            }
            return user;
        }catch(ClassNotFoundException | SQLException e){
            handleException(e);
            return new User(null);
        }finally{
            if(!isChain){
                disconnect();
            }
        }
    }
    public static User getByID(String uuid){return getByID(uuid, false);}
    public static User getByEmail(String email, boolean isChain){
        try{
            connect();
            ResultSet rs = executeQuery("SELECT * FROM users WHERE email='" + email + "'");
            User user = new User(null);
            if(rs.next()){
                user = getByResultSet(rs);
            }
            return user;
        }catch(ClassNotFoundException | SQLException e){
            handleException(e);
            return new User(null);
        }finally{
            if(!isChain){
                disconnect();
            }
        }
    }
    public static User getByEmail(String email){return getByEmail(email, false);}
    public static User login(String email, String password){
        try{
            connect();
            PreparedStatement pst = conn.prepareStatement("SELECT * FROM users WHERE email=? AND pwd=?");
            pst.setString(1, email);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            User user = new User(null);
            if(rs.next()){
                user = getByResultSet(rs);
            }
            return user;
        }catch(ClassNotFoundException | SQLException e){
            handleException(e);
            return new User(null);
        }finally{
            disconnect();
        }
    }
    public static void deleteByID(String id){
        try{
            connect();
            execute("DELETE FROM users WHERE id='" + id + "'");
        }catch(ClassNotFoundException | SQLException e){
            handleException(e);
        }finally{
            disconnect();
        }
    }
    public static void migrate(){
        try{
            connect();
            execute(
                "CREATE TABLE " +
                "users (name varchar(255)," +
                "email varchar(255)," + 
                "pwd varchar(255)," + 
                "avatar text," +
                "bio text," +
                "country varchar(255)," +
                "sts varchar(255)," +
                "uuid varchar(255)," +
                "PRIMARY KEY (uuid)," +
                "UNIQUE(email))"
            );
        }catch(ClassNotFoundException | SQLException e){
            handleException(e);
        }finally{
            disconnect();
        }
    }
    private static User getByResultSet(ResultSet rs) throws SQLException {
        String sUuid = rs.getString("uuid");
        String sName = rs.getString("name");
        String sEmail = rs.getString("email");
        String sPassword = rs.getString("pwd");
        String sAvatar = rs.getString("avatar");
        String sStatus = rs.getString("sts");
        String sBio = rs.getString("bio");
        String sCountry = rs.getString("country");
        User user = new User(sName, sEmail, sPassword, sAvatar, sStatus, sBio, sCountry, sUuid);
        return user;  
    }
}