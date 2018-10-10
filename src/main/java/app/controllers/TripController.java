package app.controllers;
import app.models.*;
import advance.Controller;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.json.JSONArray;
public class TripController extends Controller {
    public void get(){
        String id = super.params.get("id");
        HashMap<String, Object> data = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        data.put("session", super.session);
        if(id.equals("add")){
            if(super.session.get("id") == null){
                super.redirect("/auth/login", 302);
            }else{
                User[] users = User.getAll();
                HashMap<String, Object>[] usersMap = mapper.convertValue(users, HashMap[].class);
                data.put("users", usersMap);
                super.render("addTrip", data);
            }
        }else if(id.equals("home")){
            Trip[] trips = Trip.getByUserID((String) super.session.get("id"));
            HashMap<String, Object>[] tripsMap = mapper.convertValue(trips, HashMap[].class);
            data.put("trips", tripsMap);
            super.render("tripHome", data);
        }else{
            Trip trip = Trip.getByID(id);
            HashMap<String, Object> tripMap = mapper.convertValue(trip, HashMap.class);
            data.put("trip", tripMap);
            super.render("trip", data);
        }
    }
    public void post(){
        DateTimeFormatter dt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        JSONArray jsonLocations = new JSONArray(super.body.get("locations"));
        Trip trip = new Trip(
            super.body.get("name"), 
            null, 
            (String) super.session.get("id")
        );
        trip.locations = getLocations(jsonLocations, trip.id);
        trip.save();
        super.redirect("/trip/" + trip.id, 302);
    }
    public void patch(){
        Trip trip = Trip.getByID(super.params.get("id"));
        if(trip != null){
            if(trip.userID.equals(super.session.get("id"))){
                trip.name = super.body.get("name");
                trip.update();
            }
        }
        super.redirect("/trip/" + trip.id, 302);
    }
    public static Location[] getLocations(JSONArray jsonLocations, String tripId){
        int len = jsonLocations.length();
        Location[] locations = new Location[len];
        for(int x = 0; x < len; x++){
            JSONObject jsonLocation = jsonLocations.getJSONObject(x);
            JSONArray jsonActivities = jsonLocation.getJSONArray("activities");
            Location location = new Location(
                jsonLocation.getString("town"), 
                jsonLocation.getString("subdivision"), 
                jsonLocation.getString("country"), 
                jsonLocation.getInt("zip"), 
                null,
                tripId
            );
            location.activities = getActivities(jsonActivities, location.id);
            locations[x] = location;
        }        
        return locations;
    }
    public static Activity[] getActivities(JSONArray jsonActivities, String locationId){
        DateTimeFormatter dt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        int len = jsonActivities.length();
        Activity[] activities = new Activity[len];
        for(int y = 0; y < len; y++){
            JSONObject jsonActivity = jsonActivities.getJSONObject(y);
            JSONArray jsonParticipants = jsonActivity.getJSONArray("participants");
            Activity activity = new Activity(
                jsonActivity.getString("name"), 
                LocalDate.parse(jsonActivity.getString("start"), dt), 
                LocalDate.parse(jsonActivity.getString("end"), dt), 
                getParticipants(jsonParticipants), 
                locationId
            );
            activities[y] = activity;
        }    
        return activities;    
    }
    public static User[] getParticipants(JSONArray jsonParticipants){
        int len = jsonParticipants.length();
        User[] participants = new User[len];
        for(int i = 0; i < len; i++){
            JSONObject jsonParticipant = jsonParticipants.getJSONObject(i); 
            User user = new User(jsonParticipant.getString("id"));
            participants[i] = user;                
        }
        return participants;
    }
}