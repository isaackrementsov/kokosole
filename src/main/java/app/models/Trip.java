package app.models;
import java.sql.*;
import java.util.ArrayList;
public class Trip extends Model {
    public String name;
    public Location[] locations;
    public String userID;
    public String id;
    public Trip(String name, Location[] locations, String userID){
        this.name = name;
        this.locations = locations;
        this.userID = userID;
    }
    public Trip(String name, Location[] locations, String userID, String id){
        this.name = name;
        this.locations = locations;
        this.userID = userID;
        this.id = id;
    }
    public void save(){
        try{
            connect();
            PreparedStatement pst = conn.prepareStatement("INSERT INTO users (name, user_id, uuid) values(?, ?, ?)");
            pst.setString(1, this.name);
            pst.setString(2, this.userID);
            pst.setString(3, this.id);
            pst.execute();
            pst.close();  
            for(Location loc : this.locations){
                loc.save();
            } 
            conn.close();
        }catch(ClassNotFoundException ce){
            System.out.println("Driver error: " + ce);
            ce.printStackTrace();
       }catch(SQLException se){
            System.out.println("SQL error: " + se);
            se.printStackTrace();
        }
    }
    public static Trip[] getByUserID(String userID){
        try{
            connect();
            ResultSet rs = executeQuery("SELECT * FROM trips WHERE user_id='" + userID + "'");
            ArrayList<Trip> trips = new ArrayList<>();
            while(rs.next()){
                String sName = rs.getString("town");
                String sUuid = rs.getString("uuid");
                String sUserID = rs.getString("user_id");
                Location[] sLocations = Location.getLocationsByID(sUuid);
                Trip trip = new Trip(sName, sLocations, sUserID, sUuid);
                trips.add(trip);
            }
            conn.close();
            Trip[] tripArray = new Trip[trips.size()];
            tripArray = trips.toArray(tripArray);
            return tripArray;
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
    public static Trip getByID(String uuid){
        try{
            connect();
            ResultSet rs = executeQuery("SELECT * FROM trips WHERE uuid='" + uuid + "'");
            Trip trip = getByResultSet(rs);
            conn.close();
            return trip;
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
    public static Trip getByResultSet(ResultSet rs) throws SQLException {
        if(rs.next()){
            String sName = rs.getString("town");
            String sUuid = rs.getString("uuid");
            String sUserID = rs.getString("user_id");
            Location[] sLocations = Location.getLocationsByID(sUuid);
            Trip trip = new Trip(sName, sLocations, sUserID, sUuid);
            return trip;
        }else{
            return null;
        }  
    }
    public static void migrate(){
        try{
            connect();
            execute(
                "CREATE TABLE " +
                "trips (name varchar(255)," +
                "user_id varchar(255)," +
                "uuid varchar(255)," +
                "PRIMARY KEY (uuid)," +
                "FOREIGN KEY (user_id) REFERENCES users(uuid))"
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