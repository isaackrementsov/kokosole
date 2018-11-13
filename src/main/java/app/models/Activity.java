package app.models;
import java.time.LocalDate;
import java.util.UUID;
import java.util.ArrayList;
import java.sql.*;
public class Activity extends Model {
    public String locationID;
    public String id;
    public String name;
    public Duration duration;
    public User[] participants;
    public Activity(String name, LocalDate start, LocalDate end, User[] participants, String locationId, String id){
        this.name = name;
        this.duration = new Duration(start, end);
        this.participants = participants;
        this.locationID = locationId;
        this.id = id;
    }
    public Activity(String name, LocalDate start, LocalDate end, User[] participants, String locationId){
        this(name, start, end, participants, locationId, UUID.randomUUID().toString());
    }
    public Activity(){ }
    public void save(String userID, boolean isChain){
        try{
            connect();
            PreparedStatement pst = conn.prepareStatement("INSERT INTO activities (name, start, end, location_id, uuid) values(?, ?, ?, ?, ?)");
            pst.setString(1, this.name);
            pst.setDate(2, Date.valueOf(this.duration.start));
            pst.setDate(3, Date.valueOf(this.duration.end));
            pst.setString(4, this.locationID);
            pst.setString(5, this.id);
            pst.execute();
            pst.close();   
            for(User user : this.participants){
                PreparedStatement pst2 = conn.prepareStatement("INSERT IGNORE INTO activity_connections (user_email, activity_id) values(?, ?)");
                pst2.setString(1, user.email);
                pst2.setString(2, this.id);
                pst2.execute();
                pst2.close();
            }
            Conversation conversation = new Conversation(this.name, userID, this.participants);
            conversation.save(true);
        }catch(ClassNotFoundException | SQLException e){
            handleException(e);
        }finally{
            if(!isChain){
                disconnect();
            }
        }
    }
    public void save(String userID){this.save(userID, false);}
    public void update(){
        try{
            connect();
            PreparedStatement pst = conn.prepareStatement(
                "UPDATE activities SET name=COALESCE(?, name),start=COALESCE(?, start),end=COALESCE(?, end) " + 
                " WHERE uuid=?"
            );
            pst.setString(1, this.name);
            pst.setDate(2, Date.valueOf(this.duration.start));
            pst.setDate(3, Date.valueOf(this.duration.end));
            pst.setString(4, this.id);
            pst.executeUpdate();
            pst.close();
            for(User participant : this.participants){
                if(!participant.email.equals(User.getByID(this.getUserID(true, true), true).email)){
                    PreparedStatement pst2 = conn.prepareStatement(
                        "INSERT IGNORE INTO activity_connections (user_email, activity_id) values(?,?)"
                    );
                    pst2.setString(1, participant.email);
                    pst2.setString(2, this.id);
                    pst2.execute();
                    pst2.close();
                }
            }
        }catch(ClassNotFoundException | SQLException e){
            handleException(e);
        }finally{
            disconnect();
        }
    }
    public void delete(boolean isChain){
        try{
            connect();
            PreparedStatement pst = conn.prepareStatement("DELETE FROM activity_connections WHERE activity_id=?");
            pst.setString(1, this.id);
            pst.execute();
            pst.close();
            PreparedStatement pst2 = conn.prepareStatement("DELETE FROM activities WHERE uuid=?");
            pst2.setString(1, this.id);
            pst2.execute();
            pst2.close();
        }catch(ClassNotFoundException | SQLException e){
            handleException(e);
        }finally{
            if(!isChain){
                disconnect();
            }
        }
    }
    public void delete(){this.delete(false);}
    public void deleteParticipant(String email){
        try{
            connect();
            PreparedStatement pst = conn.prepareStatement(
                "DELETE FROM activity_connections WHERE user_email=? AND activity_id=? LIMIT 1"
            );
            pst.setString(1, email);
            pst.setString(2, this.id);
            pst.executeUpdate();
            pst.close();
        }catch(ClassNotFoundException | SQLException e){
            handleException(e);
        }finally{
            disconnect();
        }
    }
    public String getUserID(boolean verification, boolean isChain){
        try{
            connect();
            ResultSet rs = executeQuery(
                "SELECT user_id FROM trips WHERE uuid=(SELECT trip_id FROM locations WHERE uuid= '" + this.locationID + "')"
            );
            String id = "";
            if(rs.next()){
                id = rs.getString("user_id");
            }
            return id;
        }catch(ClassNotFoundException | SQLException e){
            handleException(e);
            return "";
        }finally{
            if(!isChain){
                disconnect();
            }
        }   
    }
    public String getUserID(boolean verification){return this.getUserID(verification, false);}
    public static Activity getByID(String uuid, boolean isChain){
        try{
            connect();
            ResultSet rs = executeQuery("SELECT * FROM activities WHERE uuid='" + uuid + "'");
            Activity activity = new Activity();
            if(rs.next()){
                activity = getByResultSet(rs);
            }
            return activity;
        }catch(ClassNotFoundException | SQLException e){
            handleException(e);
            return new Activity();
        }finally{
            if(!isChain){
                disconnect();
            }
        }
    }
    public static Activity getByID(String uuid){return getByID(uuid, false);}
    private static User[] getParticipants(String activityID, String userID) throws SQLException, ClassNotFoundException {
        ResultSet rs = conn.createStatement().executeQuery(
            "SELECT * FROM activity_connections WHERE activity_id='" + activityID + "'"
        );
        ArrayList<User> users = new ArrayList<>();
        while(rs.next()){
            String sUserEmail = rs.getString("user_email");
            User user = User.getByEmail(sUserEmail, true);
            users.add(user);
        }
        users.add(User.getByID(userID, true));
        User[] userArray = new User[users.size()];
        userArray = users.toArray(userArray);
        return userArray;
    }  
    private static Activity getByResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        String sName = rs.getString("name");
        Duration sDuration = new Duration(rs.getDate("start"), rs.getDate("end"));
        String sUuid = rs.getString("uuid");
        String sLocationId = rs.getString("location_id");
        Activity activity = new Activity(sName, sDuration.start, sDuration.end, null, sLocationId, sUuid);
        User[] sParticipants = getParticipants(sUuid, activity.getUserID(true, true));
        activity.participants = sParticipants;
        return activity; 
    }
    public static Activity[] getActivitiesByID(String locationID) throws SQLException, ClassNotFoundException {
        ResultSet rs = executeQuery("SELECT * FROM activities WHERE location_id='" + locationID + "'");
        ArrayList<Activity> activities = new ArrayList<>();
        while(rs.next()){
            activities.add(getByResultSet(rs));
        }
        Activity[] activityArray = new Activity[activities.size()];
        activityArray = activities.toArray(activityArray);
        return activityArray;
    }
    public static Activity[] getByParticipantEmail(String email, boolean isChain){
        try{
            connect();
            PreparedStatement pst = conn.prepareStatement(
                "SELECT activity_id FROM activity_connections WHERE user_email=?"
            );
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();
            ArrayList<Activity> activities = new ArrayList<>();
            while(rs.next()){
                Activity activity = Activity.getByID(rs.getString("activity_id"), true);
                activities.add(activity);
            }
            pst.close();
            return activities.toArray(new Activity[activities.size()]);
        }catch(ClassNotFoundException | SQLException e){
            handleException(e);
            return new Activity[0];
        }finally{
            if(!isChain){
                disconnect();
            }
        } 
    }
    public static Activity[] getByParticipantEmail(String email){return getByParticipantEmail(email, false);}
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
                "FOREIGN KEY (location_id) REFERENCES locations(uuid));"
            );
            execute( 
                "CREATE TABLE " +
                "activity_connections (user_email varchar(255), " +
                "activity_id varchar(255), " + 
                "FOREIGN KEY (user_email) REFERENCES users(email)," + 
                "FOREIGN KEY (activity_id) REFERENCES activities(uuid)," + 
                "UNIQUE(user_email, activity_id))"
            );
        }catch(ClassNotFoundException | SQLException e){
            handleException(e);
        }finally{
            disconnect();
        }
    }
}