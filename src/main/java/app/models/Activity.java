package app.models;
import java.time.LocalDate;;
import java.util.UUID;
import java.util.ArrayList;
import java.sql.*;
public class Activity extends Model {
    public String locationId;
    public String id;
    public String name;
    public Duration duration;
    public User[] participants;
    public Activity(String name, LocalDate start, LocalDate end, User[] participants, String locationId, String id){
        this.name = name;
        this.duration = new Duration(start, end);
        this.participants = participants;
        this.locationId = locationId;
        this.id = id;
    }
    public Activity(String name, LocalDate start, LocalDate end, User[] participants, String locationId){
        this(name, start, end, participants, locationId, UUID.randomUUID().toString());
    }
    public void save(){
        try{
            connect();
            PreparedStatement pst = conn.prepareStatement("INSERT INTO activities (name, start, end, location_id, uuid) values(?, ?, ?, ?, ?)");
            pst.setString(1, this.name);
            pst.setDate(2, Date.valueOf(this.duration.start));
            pst.setDate(3, Date.valueOf(this.duration.end));
            pst.setString(4, this.locationId);
            pst.setString(5, this.id);
            pst.execute();
            pst.close();   
            conn.close();
            for(User user : this.participants){
                connect();
                PreparedStatement pst2 = conn.prepareStatement("INSERT INTO activity_connections (user_id, activity_id) values(?, ?)");
                pst2.setString(1, user.id);
                pst2.setString(2, this.id);
                pst2.execute();
                pst2.close();
                conn.close();
            }
        }catch(ClassNotFoundException ce){
            System.out.println("Driver error: " + ce);
            ce.printStackTrace();
        }catch(SQLException se){
            System.out.println("SQL error: " + se);
            se.printStackTrace();
        }
    }
    public static Activity getByID(String uuid){
        try{
            connect();
            ResultSet rs = executeQuery("SELECT * FROM activities WHERE uuid='" + uuid + "'");
            Activity activity = getByResultSet(rs);
            conn.close();
            return activity;
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
    private static User[] getParticipants(String activityID) throws SQLException {
        ResultSet rs = executeQuery("SELECT * FROM activity_connections WHERE activity_id='" + activityID + "'");
        ArrayList<User> users = new ArrayList<>();
        while(rs.next()){
            String sUuid = rs.getString("uuid");
            User user = User.getByID(sUuid);
            users.add(user);
        }
        return (User[]) users.toArray();
    }  
    private static Activity getByResultSet(ResultSet rs) throws SQLException {
        if(rs.next()){
            String sName = rs.getString("name");
            Duration sDuration = new Duration(rs.getDate("start"), rs.getDate("end"));
            String sUuid = rs.getString("uuid");
            String sLocationId = rs.getString("location_id");
            User[] sParticipants = getParticipants(sUuid);
            Activity activity = new Activity(sName, sDuration.start, sDuration.end, sParticipants, sLocationId, sUuid);
            return activity;
        }else{
            return null;
        }   
    }
    public static Activity[] getActivitiesByID(String locationID) throws SQLException {
        ResultSet rs = executeQuery("SELECT * FROM activities WHERE uuid='" + locationID + "'");
        ArrayList<Activity> activities = new ArrayList<>();
        while(rs.next()){
            String sName = rs.getString("name");
            Duration sDuration = new Duration(rs.getDate("start"), rs.getDate("end"));
            String sUuid = rs.getString("uuid");
            String sLocationId = rs.getString("location_id");
            User[] sParticipants = getParticipants(sUuid);
            Activity activity = new Activity(sName, sDuration.start, sDuration.end, sParticipants, sLocationId, sUuid);
            activities.add(activity);
        }
        Activity[] activityArray = new Activity[activities.size()];
        activityArray = activities.toArray(activityArray);
        return activityArray;
    }
    public static void migrate(){
        try{
            connect();
            execute(
                "CREATE TABLE " + 
                "activities (name varchar(255), " +
                "start datetime," + 
                "end datetime," +
                "uuid varchar(255)," +
                "location_id varchar(255)," +
                "PRIMARY KEY (uuid)," +
                "FOREIGN KEY (location_id) REFERENCES locations(uuid))" 
            );
            connect();
            execute(
                "CREATE TABLE " +
                "activity_connections (user_id varchar(255), " +
                "activity_id varchar(255), " + 
                "FOREIGN KEY (user_id) REFERENCES users(uuid)," + 
                "FOREIGN KEY (activity_id) REFERENCES activities(uuid))"
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