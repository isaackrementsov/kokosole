package app.controllers;
import app.models.*;
import advance.Controller;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.json.JSONArray;
public class TripController extends Controller {
    public void get(){
        if(super.session.get("id") == null){
            super.redirect("/auth/login", 302);
        }else{
            String id = super.params.get("id");
            HashMap<String, Object> data = new HashMap<>();
            data.put("session", super.session);
            if(id.equals("add")){
                super.render("addTrip", data);
            }else{
                Trip trip = Trip.getByID(id);
                ObjectMapper mapper = new ObjectMapper();
                HashMap<String, Object> tripMap = mapper.convertValue(trip, HashMap.class);
                data.put("trip", tripMap);
                super.render("trip", data);
            }
        }
    }
    public void post(){
        DateTimeFormatter dt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        JSONArray jsonLocations = new JSONArray(super.body.get("locations"));
        Trip trip = new Trip(super.body.get("name"), new Location[jsonLocations.length()], (String) super.session.get("id"));
        for(int x = 0; x < jsonLocations.length(); x++){
            JSONObject jsonLocation = jsonLocations.getJSONObject(x);
            JSONArray jsonActivities = jsonLocation.getJSONArray("activities");
            Location location = new Location(
                jsonLocation.getString("town"), 
                jsonLocation.getString("subdivision"), 
                jsonLocation.getString("country"), 
                jsonLocation.getInt("zip"), 
                new Activity[jsonActivities.length()],
                trip.id
            );
            for(int y = 0; y < jsonActivities.length(); y++){
                JSONObject jsonActivity = jsonActivities.getJSONObject(y);
                JSONArray jsonParticipants = jsonActivity.getJSONArray("participants");
                System.out.println(jsonActivity.getString("start"));
                Activity activity = new Activity(
                    jsonActivity.getString("name"), 
                    LocalDate.parse(jsonActivity.getString("start"), dt), 
                    LocalDate.parse(jsonActivity.getString("end"), dt), 
                    new User[jsonParticipants.length()], 
                    location.id
                );
                for(int i = 0; i < jsonParticipants.length(); i++){
                    JSONObject jsonParticipant = jsonParticipants.getJSONObject(i); 
                    User user = new User(jsonParticipant.getString("id"));
                    activity.participants[i] = user;                
                }
                location.activities[y] = activity;
            }
            trip.locations[x] = location;
        }
        trip.save();
        super.redirect("/trip/" + trip.id, 302);
    }
}