package app.models;
import java.sql.*;
import java.time.LocalDateTime;;
import java.util.UUID;
import java.util.ArrayList;
public class Message extends Model {
    public LocalDateTime sentAt;
    public String content;
    public String userEmail;
    public String conversationID;
    public String id;
    public Message(String content, String userEmail, String conversationID, LocalDateTime sa, String id){
        this.sentAt = sa;
        this.content = content;
        this.userEmail = userEmail;
        this.conversationID = conversationID;
        this.id = id;
    }
    public Message(String content, String userEmail, String conversationID, LocalDateTime sa){
        this(content, userEmail, conversationID, sa, UUID.randomUUID().toString());
    }
    public void save(){
        try{
            connect();
            PreparedStatement pst = conn.prepareStatement(
                "INSERT INTO messages (sent_at, content, user_email, conversation_id, uuid) values(?, ?, ?, ?, ?)"
            );
            pst.setTimestamp(1, Timestamp.valueOf(this.sentAt));
            pst.setString(2, this.content);
            pst.setString(3, this.userEmail);
            pst.setString(4, this.conversationID);
            pst.setString(5, this.id);
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
    public static Message[] getByConversationID(String conversationID){
        try{
            connect();
            PreparedStatement pst = conn.prepareStatement("SELECT * FROM messages WHERE conversation_id=? ORDER BY sent_at ASC");
            pst.setString(1, conversationID);
            ResultSet rs = pst.executeQuery();
            ArrayList<Message> messages = new ArrayList<>();
            while(rs.next()){
                LocalDateTime sSentAt = rs.getTimestamp("sent_at").toLocalDateTime();
                String sContent = rs.getString("content");
                String sUserEmail = rs.getString("user_email");
                String sConversationID = rs.getString("conversation_id");
                String uuid = rs.getString("uuid");
                messages.add(new Message(sContent, sUserEmail, sConversationID, sSentAt, uuid));
            }
            pst.close();
            conn.close();
            return messages.toArray(new Message[messages.size()]);
        }catch(ClassNotFoundException ce){
            System.out.println("Driver error: " + ce);
            ce.printStackTrace();
            return new Message[0];
        }catch(SQLException se){
            System.out.println("SQL error: " + se);
            se.printStackTrace();
            return new Message[0];
        }
    }
    public static void migrate(){
        try{
            connect();
            execute(
                "CREATE TABLE messages(content text," +
                "sent_at timestamp," +
                "user_email varchar(255)," + 
                "conversation_id varchar(255)," + 
                "uuid varchar(255)," +
                "PRIMARY KEY (uuid)," + 
                "FOREIGN KEY (user_email) REFERENCES users(email)," +
                "FOREIGN KEY (conversation_id) REFERENCES conversations(uuid))" 
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