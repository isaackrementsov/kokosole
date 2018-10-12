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
        JSONArray jsonParticipants = new JSONArray("participants");
        Activity activity = new Activity(
            super.body.get("name"),
            LocalDate.parse(super.body.get("start"), dt), 
            LocalDate.parse(super.body.get("end"), dt), 
            null, 
            super.body.get("locationId")
        );
        activity.participants = TripController.getParticipants(jsonParticipants);
        activity.save();    
    }
    public void patch(){
        String id = super.params.get("id");
        String action = super.query.get("action");
        String subAction = super.query.get("sub");
        Activity activity = Activity.getByID(id);
        Class<?> c = activity.getClass();
        String userID = activity.getUserID(true);
        if(userID != null){
            if(userID.equals(super.session.get("id"))){
                if(action.equals("participants")){
                    String pID = super.body.get("id");
                    ArrayList<User> participants = new ArrayList<User>(Arrays.asList(activity.participants));
                    if(subAction.equals("add")){
                        boolean exists = false;
                        for(User user : participants){
                            if(user.id.equals(pID)){
                                exists = true;
                            }
                        }
                        if(!exists){
                            participants.add(new User(pID));
                        }
                    }else if(subAction.equals("delete")){
                        participants.removeIf(user -> user.id.equals(pID));
                    }
                    activity.participants = participants.toArray(new User[participants.size()]);
                }else{
                    for(String key : super.body.keySet()){
                        try{
                            Field f = c.getField(key);
                            f.set(activity, super.body.get(key));
                        }catch(Exception n){}
                    }
                }
                activity.update();
            }
        }
        super.redirect("/location/" + activity.id, 302);
        super.redirect("/location/" + activity.id, 302);
    }
}