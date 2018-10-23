package app.controllers;
import app.models.*;
import advance.Controller;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONObject;
public class ActivityController extends Controller {
    public void post(){
        DateTimeFormatter dt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String id = super.params.get("id");
        JSONArray jsonParticipants = new JSONArray(super.body.get("participants"));
        Activity activity = new Activity(
            super.body.get("name"),
            LocalDate.parse(super.body.get("start"), dt), 
            LocalDate.parse(super.body.get("end"), dt), 
            null, 
            id
        );
        activity.participants = TripController.getParticipants(jsonParticipants);
        String userID = activity.getUserID(true);
        if(userID != null){
            if(userID.equals(super.session.get("id"))){
                activity.save((String) super.session.get("id"));    
            }
        }
        super.redirect("/location/" + id, 302);
    }
    public void patch(){
        String id = super.params.get("id");
        String action = super.query.get("action");
        if(action == null){
            action = "";
        }
        String subAction = super.query.get("sub");
        Activity activity = Activity.getByID(id);
        Class<?> c = activity.getClass();
        String userID = activity.getUserID(true);
        if(userID != null){
            boolean dontSkip = true;
            if(userID.equals(super.session.get("id"))){
                if(action.equals("participants")){
                    String pEmail = super.body.get("email");
                    ArrayList<User> participants = new ArrayList<User>(Arrays.asList(activity.participants));
                    if(subAction.equals("add")){
                        participants.add(new User(null, pEmail));
                    }else if(subAction.equals("delete")){
                        dontSkip = false;
                        participants.removeIf(user -> user.email.equals(pEmail));
                        activity.deleteParticipant(pEmail);
                    }
                    activity.participants = participants.toArray(new User[participants.size()]);
                }else{
                    for(String key : super.body.keySet()){
                        try{
                            if(key.equals("start") || key.equals("end")){
                                DateTimeFormatter dt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                                if(key.equals("start")){
                                    activity.duration.start = LocalDate.parse(super.body.get(key), dt);
                                }else{
                                    activity.duration.end = LocalDate.parse(super.body.get(key), dt);
                                }
                            }else{
                                Field f = c.getField(key);
                                f.set(activity, super.body.get(key));
                            }
                        }catch(Exception n){ }
                    }
                }
                if(dontSkip){
                    activity.update();
                }
            }
        }
        super.redirect("/location/" + activity.locationID, 302);
    }
    public void delete(){
        String id = super.params.get("id");
        Activity activity = Activity.getByID(id);
        String userID = activity.getUserID(true);
        if(userID != null){
            if(userID.equals(super.session.get("id"))){
                activity.delete();    
            }
        }
        super.redirect("/location/" + activity.locationID, 302);
    }
}