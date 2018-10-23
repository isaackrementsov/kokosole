package app.controllers;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import advance.Controller;
import app.models.*;
public class ConversationController extends Controller {
    public void get(){
        if(super.session.get("id") == null){
            super.redirect("/auth/login", 302);
        }else{
            ObjectMapper mapper = new ObjectMapper();
            String id = super.params.get("id");
            HashMap<String, Object> data = new HashMap<>();
            data.put("session", super.session);
            if(id.equals("home")){
                Conversation[] conversations = Conversation.getByUserID((String) super.session.get("id"));
                Conversation[] shared = Conversation.getByParticipantEmail((String) super.session.get("email"));
                HashMap<String, Object>[] conversationsMap = mapper.convertValue(conversations, HashMap[].class);
                HashMap<String, Object>[] sharedMap = mapper.convertValue(shared, HashMap[].class);
                data.put("conversations", conversationsMap);
                data.put("sharedConversations", sharedMap);
                super.render("conversations", data);
            }else{
                Conversation conversation = Conversation.getByID(id);
                if(conversation.userID == null){
                    conversation = new Conversation();
                }else{
                    if(!conversation.userID.equals(super.session.get("id"))){
                        boolean isMember = false;
                        for(User participant : conversation.participants){
                            if(participant.id.equals(super.session.get("id"))){
                                isMember = true;
                                break;
                            }
                        }
                        if(!isMember){
                            conversation = new Conversation();
                        }
                    }
                }
                Message[] messages = Message.getByConversationID(conversation.id);
                HashMap<String, Object> conversationMap =  mapper.convertValue(conversation, HashMap.class);
                HashMap<String, Object>[] messagesMap = mapper.convertValue(messages, HashMap[].class);
                super.session.put("conversationID", conversation.id);
                data.put("permission", super.session.get("id").equals(conversation.userID));
                data.put("conversation", conversationMap);
                data.put("messages", messagesMap);
                super.render("conversation", data);
            }
        }
    }
    public void post(){
        String id = super.params.get("id");
        JSONArray jsonParticipants = new JSONArray(super.body.get("participants"));
        Conversation conversation = new Conversation(super.body.get("name"), id, null);
        conversation.participants = TripController.getParticipants(jsonParticipants);
        if(id != null){
            if(id.equals(super.session.get("id"))){
                conversation.save();    
            }
        }
        super.redirect("/conversation/" + conversation.id, 302);
    }
    public void patch(){
        String id = super.params.get("id");
        Conversation conversation = Conversation.getByID(id);
        String action = super.query.get("action");
        if(action == null){
            action = "";
        }
        String subAction = super.query.get("sub");
        if(conversation.userID != null){
            boolean dontSkip = true;
            if(conversation.userID.equals(super.session.get("id"))){
                if(action.equals("participants")){
                    String pEmail = super.body.get("email");
                    ArrayList<User> participants = new ArrayList<User>(Arrays.asList(conversation.participants));
                    if(subAction.equals("add")){
                        participants.add(new User(null, pEmail));
                    }else if(subAction.equals("delete")){
                        dontSkip = false;
                        participants.removeIf(user -> user.email.equals(pEmail));
                        conversation.deleteParticipant(pEmail);
                    }
                    conversation.participants = participants.toArray(new User[participants.size()]);
                }else{
                    conversation.title = super.body.get("title");
                }
                if(dontSkip){
                    conversation.update();
                }
            }
        }
        super.redirect("/conversation/" + conversation.id, 302);
    }
    public void delete(){
        String id = super.params.get("id");
        Conversation conversation = Conversation.getByID(id);
        if(super.session.get("id") != null){
            if(super.session.get("id").equals(conversation.userID)){
                conversation.delete();
            }
        }
        super.redirect("/conversation/home", 302);
    }
}