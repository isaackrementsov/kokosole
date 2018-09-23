package app.models;
import java.sql.*;
import java.util.UUID;
public class User extends Model {
    public String name;
    public String email;
    public String password;
    public String status;
    public String id;
    public User(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
        this.status = "inactive";
        this.id = UUID.randomUUID().toString();
    }
    public User(String name, String email, String password, String id, String status){
        this.name = name;
        this.email = email;
        this.password = password;
        this.id = id;
        this.status = status;
    }
    public void save(){
        try{
            connect();
            execute(
                "INSERT INTO users (name, email, pwd, uuid, sts) values('" 
                + this.name + "','" + this.email + "','" + this.password + "','" + this.id + "','" + this.status +  
                "')"
            );
        }catch(ClassNotFoundException ce){
            System.out.println("Driver error: " + ce);
            ce.printStackTrace();
       }catch(SQLException se){
            System.out.println("SQL error: " + se);
            se.printStackTrace();
        }
    }
    public void update(){
        try{
            connect();
            execute(
                "UPDATE users SET " + 
                "name='" + this.name +
                "',email='" + this.email + 
                "',pwd='" + this.password +
                "',sts='" + this.status +
                "' WHERE uuid='" + this.id + "'"
            );
        }catch(ClassNotFoundException ce){
            System.out.println("Driver error: " + ce);
            ce.printStackTrace();
       }catch(SQLException se){
            System.out.println("SQL error: " + se);
            se.printStackTrace();
        }
    }
    public static User findByID(String uuid){
        try{
            connect();
            ResultSet rs = executeQuery("SELECT * FROM users WHERE uuid='" + uuid + "'");
            if(rs.next()){
                String sUuid = rs.getString("uuid");
                String sName = rs.getString("name");
                String sEmail = rs.getString("email");
                String sPassword = rs.getString("pwd");
                String sStatus = rs.getString("sts");
                User user = new User(sName, sEmail, sPassword, sUuid, sStatus);
                return user;
            }else{
                return null;
            }
        }catch(ClassNotFoundException ce){
            System.out.println("Driver error: " + ce);
            ce.printStackTrace();
            return null;
       }catch(SQLException se){
            System.out.println("SQL error: " + se);
            se.printStackTrace();
            return null;
        }
    }
    public static User login(String email, String password){
        try{
            connect();
            ResultSet rs = executeQuery("SELECT * FROM users WHERE email='" + email + "' AND pwd='" + password + "'");
            if(rs.next()){
                String sUuid = rs.getString("uuid");
                String sName = rs.getString("name");
                String sEmail = rs.getString("email");
                String sPassword = rs.getString("pwd");
                String sStatus = rs.getString("sts");
                User user = new User(sName, sEmail, sPassword, sUuid, sStatus);
                return user;
            }else{
                return null;
            }
        }catch(ClassNotFoundException ce){
            System.out.println("Driver error: " + ce);
            ce.printStackTrace();
            return null;
       }catch(SQLException se){
            System.out.println("SQL error: " + se);
            se.printStackTrace();
            return null;
        }
    }
    public static void deleteByID(String id){
        try{
            connect();
            execute("DELETE FROM users WHERE id='" + id + "'");
        }catch(ClassNotFoundException ce){
            System.out.println("Driver error: " + ce);
            ce.printStackTrace();
       }catch(SQLException se){
            System.out.println("SQL error: " + se);
            se.printStackTrace();
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
                  "uuid varchar(255)," 
                  +  "sts varchar(255))"
            );
        }catch(ClassNotFoundException ce){
            System.out.println("Driver error: " + ce);
            ce.printStackTrace();
       }catch(SQLException se){
            System.out.println("SQL error: " + se);
            se.printStackTrace();
        }
    }
}