package app.models;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;
public class Location extends Model {
    public String town;
    public String subdivision;
    public String country;
    public int zip;
    public Activity[] activities;
    public String id;
    public String tripID;
    public Location(String town, String subdivision, String country, int zip, Activity[] activities, String tripID, String id){
        this.town = town;
        this.subdivision = subdivision;
        this.country = country;
        this.zip = zip;
        this.activities = activities;
        this.tripID = tripID;
        this.id = id;       
    }
    public Location(String town, String subdivision, String country, int zip, Activity[] activities, String tripID){
        this(town, subdivision, country, zip, activities, tripID, UUID.randomUUID().toString());
    }
    public Location(){ }
    public void save(String userID){
        try{
            connect();
            PreparedStatement pst = conn.prepareStatement("INSERT INTO locations (town, subdivision, country, zip, trip_id, uuid) values(?, ?, ?, ?, ?, ?)");
            pst.setString(1, this.town);
            pst.setString(2, this.subdivision);
            pst.setString(3, this.country);
            pst.setInt(4, this.zip);
            pst.setString(5, this.tripID);
            pst.setString(6, this.id);
            pst.execute();
            pst.close();   
            conn.close();
            for(Activity activity : this.activities){
                activity.save(userID);
            }
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
            PreparedStatement pst = conn.prepareStatement(
                "UPDATE locations SET town=COALESCE(?, town),subdivision=COALESCE(?, subdivision),country=COALESCE(?, country)," + 
                "zip=COALESCE(?, zip) WHERE uuid=?"
            );
            pst.setString(1, this.town);
            pst.setString(2, this.subdivision);
            pst.setString(3, this.country);
            pst.setInt(4, this.zip);
            pst.setString(5, this.id);
            pst.executeUpdate();
            pst.close();
            conn.close();
        }catch(ClassNotFoundException ce){
            System.out.println("Driver error: " + ce);
            ce.printStackTrace();
        }catch(SQLException se){
            System.out.println("SQL error: " + se);
            se.printStackTrace();
        }
    }
    public void delete(){
        try{
            for(Activity activity : this.activities){
                activity.delete();
            }
            connect();
            PreparedStatement pst = conn.prepareStatement("DELETE FROM locations WHERE uuid=?");
            pst.setString(1, this.id);
            pst.execute();
            pst.close();
            conn.close();
        }catch(ClassNotFoundException ce){
            System.out.println("Driver error: " + ce);
            ce.printStackTrace();
        }catch(SQLException se){
            System.out.println("SQL error: " + se);
            se.printStackTrace();
        }  
    }
    public String getUserID(boolean verification){
        try{
            connect();
            ResultSet rs = executeQuery("SELECT user_id FROM trips WHERE uuid='" + this.tripID + "'");
            String id = "";
            if(rs.next()){
                id = rs.getString("user_id");
            }
            conn.close();
            return id;
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
    public static Location getByID(String uuid){
        try{
            connect();
            ResultSet rs = executeQuery("SELECT * FROM locations WHERE uuid='" + uuid + "'");
            Location location = getByResultSet(rs);
            conn.close();
            return location;
        }catch(ClassNotFoundException ce){
            System.out.println("Driver error: " + ce);
            ce.printStackTrace();
            return new Location();
       }catch(SQLException se){
            System.out.println("SQL error: " + se);
            se.printStackTrace();
            return new Location();
        }
    }
    private static Location getByResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        if(rs.next()){
            String sTown = rs.getString("town");
            String sSubdivision = rs.getString("subdivision");
            String sCountry = rs.getString("country");
            String sUuid = rs.getString("uuid");
            String sTripID = rs.getString("trip_id");
            Activity[] sActivities = Activity.getActivitiesByID(sUuid);
            int sZip = rs.getInt("zip");
            Location location = new Location(sTown, sSubdivision, sCountry, sZip, sActivities, sTripID, sUuid);
            return location;
        }else{
            return new Location();
        }  
    }
    public static Location[] getLocationsByID(String tripID) throws SQLException, ClassNotFoundException {
        connect();
        ResultSet rs = executeQuery("SELECT * FROM locations WHERE trip_id='" + tripID + "'");
        ArrayList<Location> locations = new ArrayList<>();
        while(rs.next()){
            String sTown = rs.getString("town");
            String sSubdivision = rs.getString("subdivision");
            String sCountry = rs.getString("country");
            String sUuid = rs.getString("uuid");
            String sTripID = rs.getString("trip_id");
            Activity[] sActivities = Activity.getActivitiesByID(sUuid);
            int sZip = rs.getInt("zip");
            Location location = new Location(sTown, sSubdivision, sCountry, sZip, sActivities, sTripID, sUuid);
            locations.add(location);
        }
        conn.close();
        Location[] locationArray = new Location[locations.size()];
        locationArray = locations.toArray(locationArray);
        return locationArray;
    }
    public static void migrate(){
        try{
            connect();
            execute(
                "CREATE TABLE " +
                "locations (town varchar(255)," +
                "subdivision varchar(255)," + 
                "country varchar(255)," + 
                "zip int," +
                "trip_id varchar(255)," +
                "uuid varchar(255)," +
                "PRIMARY KEY (uuid)," +
                "FOREIGN KEY (trip_id) REFERENCES trips(uuid))"
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