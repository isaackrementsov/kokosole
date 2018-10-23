package app.controllers;
import java.util.HashMap;
import java.util.ArrayList;
import java.lang.reflect.Field;
import advance.Controller;
import app.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.json.JSONArray;
public class LocationController extends Controller {
    public void get(){
        String id = super.params.get("id");
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> data = new HashMap<>();
        data.put("session", super.session);
        if(id.equals("add")){
            if(super.session.get("id") == null){
                super.redirect("/login", 302);
            }else{ 
                data.put("tripID", super.session.get("tripID"));
                super.render("addLoc", data);
            }
        }else{
            Location location = Location.getByID(id);
            String userID = location.getUserID(true);
            if(userID == null){
                userID = "abc123";
            }
            HashMap<String, Object> locationMap = mapper.convertValue(location, HashMap.class);
            data.put("location", locationMap);
            data.put("permission", userID.equals(super.session.get("id")));
            super.render("location", data);
        }
    }
    public void post(){
        String id = super.params.get("id");
        JSONArray jsonActivities = new JSONArray(super.body.get("activities"));
        Location location = new Location(
            super.body.get("town"), 
            super.body.get("subdivision"), 
            super.body.get("country"), 
            Integer.parseInt(super.body.get("zip")), 
            null, 
            id
        );
        location.activities = TripController.getActivities(jsonActivities, location.id);
        location.save((String) super.session.get("userID"));
        super.redirect("/location/" + location.id, 302);
    }
    public void patch(){
        String id = super.params.get("id");
        Location location = Location.getByID(id);
        Class<?> c = location.getClass();
        String userID = location.getUserID(true);
        if(userID != null){
            if(userID.equals(super.session.get("id"))){
                for(String key : super.body.keySet()){
                    try{
                        Field f = c.getField(key);
                        f.set(location, super.body.get(key));
                    }catch(Exception n){ }
                }
                location.update();
            }
        }
        super.redirect("/location/" + location.id, 302);
    }
    public void delete(){
        String id = super.params.get("id");
        Location location = Location.getByID(id);
        String userID = location.getUserID(true);
        if(userID != null){
            if(userID.equals(super.session.get("id"))){
                location.delete();    
            }
        }
        super.redirect("/trip/" + location.tripID, 302);
    }
}