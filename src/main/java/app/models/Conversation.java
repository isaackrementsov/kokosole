package app.models;
import java.util.UUID;
import java.util.ArrayList;
import java.sql.*;
public class Conversation extends Model {
    public String title;
    public String userID;
    public User[] participants;
    public String id;
    public Conversation(String title, String userID, User[] participants, String id){
        this.title = title;
        this.userID = userID;
        this.participants = participants;
        this.id = id;
    }
    public Conversation(String title, String userID, User[] participants){
        this(title, userID, participants, UUID.randomUUID().toString());
    }
    public Conversation(){ }
    public void save(){
        try{
            connect();
            PreparedStatement pst = conn.prepareStatement("INSERT INTO conversations (title, user_id, uuid) values(?, ?, ?)");
            pst.setString(1, this.title);
            pst.setString(2, this.userID);
            pst.setString(3, this.id);
            pst.execute();
            pst.close();
            for(User user : this.participants){
                PreparedStatement pst2 = conn.prepareStatement(
                    "INSERT IGNORE INTO conversation_connections (user_email, conversation_id) values(?,?)"
                );
                pst2.setString(1, user.email);
                pst2.setString(2, this.id);
                pst2.execute();
                pst2.close();
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
    public void update(){
        try{
            connect();
            PreparedStatement pst = conn.prepareStatement("UPDATE conversations SET title=COALESCE(?, title) WHERE uuid=?");
            pst.setString(1, this.title);
            pst.setString(2, this.id);
            pst.execute();
            pst.close();
            for(User user : this.participants){
                if(!user.email.equals(User.getByID(this.userID).email)){
                    connect();
                    PreparedStatement pst2 = conn.prepareStatement(
                        "INSERT IGNORE INTO conversation_connections (user_email, conversation_id) values(?, ?)"
                    );
                    pst2.setString(1, user.email);
                    pst2.setString(2, this.id);
                    pst2.execute();
                    pst2.close();
                    conn.close();
                }
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
    public void delete(){
        try{
            connect();
            PreparedStatement pst = conn.prepareStatement("DELETE FROM conversation_connections WHERE conversation_id=?");
            pst.setString(1, this.id);
            pst.execute();
            pst.close();
            PreparedStatement pst2 = conn.prepareStatement("DELETE FROM messages WHERE conversation_id=?");
            pst2.setString(1, this.id);
            pst2.execute();
            pst2.close();
            PreparedStatement pst3 = conn.prepareStatement("DELETE FROM conversations WHERE uuid=?");
            pst3.setString(1, this.id);
            pst3.execute();
            pst3.close();
            conn.close();
        }catch(ClassNotFoundException ce){
            System.out.println("Driver error: " + ce);
            ce.printStackTrace();
        }catch(SQLException se){
            System.out.println("SQL error: " + se);
            se.printStackTrace();
        }
    }
    public void deleteParticipant(String email){
        try{
            connect();
            PreparedStatement pst = conn.prepareStatement(
                "DELETE FROM conversation_connections WHERE user_email=? AND conversation_id=? LIMIT 1"
            );
            pst.setString(1, email);
            pst.setString(2, this.id);
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
    public static Conversation[] getByUserID(String id){
        try{
            connect();
            PreparedStatement pst = conn.prepareStatement("SELECT * FROM conversations WHERE user_id=?");
            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();
            ArrayList<Conversation> conversations = new ArrayList<>();
            while(rs.next()){
                conversations.add(getByResultSet(rs));
            }
            Conversation[] conversationArray = conversations.toArray(new Conversation[conversations.size()]);
            pst.close();
            conn.close();
            return conversationArray;
        }catch(ClassNotFoundException ce){
            System.out.println("Driver error: " + ce);
            ce.printStackTrace();
            return new Conversation[0];
        }catch(SQLException se){
            System.out.println("SQL error: " + se);
            se.printStackTrace();
            return new Conversation[0];
        }
    }
    public static Conversation[] getByParticipantEmail(String email){
        try{
            connect();
            PreparedStatement pst = conn.prepareStatement(
                "SELECT conversation_id FROM conversation_connections WHERE user_email=?"
            );
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();
            ArrayList<Conversation> conversations = new ArrayList<>();
            while(rs.next()){
                Conversation conversation = Conversation.getByID(rs.getString("conversation_id"));
                conversations.add(conversation);
            }
            pst.close();
            conn.close();
            return conversations.toArray(new Conversation[conversations.size()]);
        }catch(ClassNotFoundException ce){
            System.out.println("Driver error: " + ce);
            ce.printStackTrace();
            return new Conversation[0];
        }catch(SQLException se){
            System.out.println("SQL error: " + se);
            se.printStackTrace();
            return new Conversation[0];
        }  
    }
    public static Conversation getByID(String id){
        try{
            connect();
            PreparedStatement pst = conn.prepareStatement("SELECT * FROM conversations WHERE uuid=?");
            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();
            Conversation conversation = new Conversation();
            if(rs.next()){
                conversation = getByResultSet(rs);
            }
            pst.close();
            conn.close();
            return conversation;
        }catch(ClassNotFoundException ce){
            System.out.println("Driver error: " + ce);
            ce.printStackTrace();
            return new Conversation();
        }catch(SQLException se){
            System.out.println("SQL error: " + se);
            se.printStackTrace();
            return new Conversation();
        }
    }
    private static Conversation getByResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        connect();
        String sTitle = rs.getString("title");
        String sUserID = rs.getString("user_id");
        String uuid = rs.getString("uuid");
        PreparedStatement pst2 = conn.prepareStatement("SELECT * FROM conversation_connections WHERE conversation_id=?");
        pst2.setString(1, uuid);
        ResultSet rs2 = pst2.executeQuery();
        ArrayList<User> users = new ArrayList<>();
        while(rs2.next()){
            String sEmail = rs2.getString("user_email");
            users.add(User.getByEmail(sEmail));
        }
        users.add(User.getByID(sUserID));
        pst2.close();
        User[] participants = users.toArray(new User[users.size()]);
        conn.close();
        return new Conversation(sTitle, sUserID, participants, uuid);
    }
    public static void migrate(){
        try{
            connect();
            execute(
                "CREATE TABLE " + 
                "conversations (title varchar(255), " +
                "uuid varchar(255)," +
                "user_id varchar(255)," +
                "PRIMARY KEY (uuid)," +
                "FOREIGN KEY (user_id) REFERENCES users(uuid))"
            );
            connect();
            execute(
                "CREATE TABLE " + 
                "conversation_connections (user_email varchar(255), " +
                "conversation_id varchar(255)," +
                "FOREIGN KEY (conversation_id) REFERENCES conversations(uuid)," +
                "FOREIGN KEY (user_email) REFERENCES users(email)," +
                "UNIQUE(user_email, conversation_id))"
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