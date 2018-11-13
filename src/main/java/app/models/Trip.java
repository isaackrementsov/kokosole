package app.models;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;
public class Trip extends Model {
    public String name;
    public Location[] locations;
    public String userID;
    public String id;
    public Trip(String name, Location[] locations, String userID, String id){
        this.name = name;
        this.locations = locations;
        this.userID = userID;
        this.id = id;
    }
    public Trip(String name, Location[] locations, String userID){
        this(name, locations, userID, UUID.randomUUID().toString());
    }
    public Trip(){ }
    public void save(String userID){
        try{
            connect();
            PreparedStatement pst = conn.prepareStatement("INSERT INTO trips (name, user_id, uuid) values(?, ?, ?)");
            pst.setString(1, this.name);
            pst.setString(2, this.userID);
            pst.setString(3, this.id);
            pst.execute();
            pst.close();  
            for(Location loc : this.locations){
                loc.save(userID, true);
            } 
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
                "UPDATE trips SET name=COALESCE(?, name) WHERE uuid=?"
            );
            pst.setString(1, this.name);
            pst.setString(2, this.id);
            pst.executeUpdate();
            pst.close();
        }catch(ClassNotFoundException | SQLException e){
            handleException(e);
        }finally{
            disconnect();
        }
    }
    public void delete(){
        try{
            for(Location location: this.locations){
                location.delete(true);
            }
            connect();
            PreparedStatement pst2 = conn.prepareStatement("DELETE FROM trips WHERE uuid=?");
            pst2.setString(1, this.id);
            pst2.execute();
            pst2.close();
        }catch(ClassNotFoundException | SQLException e){
            handleException(e);
        }finally{
            disconnect();
        } 
    }
    public static Trip[] getByUserID(String userID){
        try{
            connect();
            ResultSet rs = executeQuery("SELECT * FROM trips WHERE user_id='" + userID + "'");
            ArrayList<Trip> trips = new ArrayList<>();
            while(rs.next()){
                String sName = rs.getString("name");
                String sUuid = rs.getString("uuid");
                String sUserID = rs.getString("user_id");
                Location[] sLocations = Location.getLocationsByID(sUuid);
                Trip trip = new Trip(sName, sLocations, sUserID, sUuid);
                trips.add(trip);
            }
            Trip[] tripArray = new Trip[trips.size()];
            tripArray = trips.toArray(tripArray);
            return tripArray;
        }catch(ClassNotFoundException | SQLException e){
            handleException(e);
            return new Trip[0];
        }finally{
            disconnect();
        }
    }
    public static Trip getByID(String uuid){
        try{
            connect();
            ResultSet rs = executeQuery("SELECT * FROM trips WHERE uuid='" + uuid + "'");
            Trip trip = getByResultSet(rs);
            return trip;
        }catch(ClassNotFoundException | SQLException e){
            handleException(e);
            return new Trip();
        }finally{
            disconnect();
        }
    }
    private static Trip getByResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        if(rs.next()){
            String sName = rs.getString("name");
            String sUuid = rs.getString("uuid");
            String sUserID = rs.getString("user_id");
            Location[] sLocations = Location.getLocationsByID(sUuid);
            Trip trip = new Trip(sName, sLocations, sUserID, sUuid);
            return trip;
        }else{
            return new Trip();
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
        }catch(ClassNotFoundException | SQLException e){
            handleException(e);
        }finally{
            disconnect();
        }
    }
}