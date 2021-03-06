package app.controllers;
import advance.Controller;
import app.models.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;
import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.ObjectMapper;
public class UserController extends Controller {
    public void get(){
        User user = User.getByID(super.params.get("id"));  
        if(user.name == null){
            super.responseCode = 404;
            super.response = "User not found".getBytes();
        }else{
            Trip[] trips = Trip.getByUserID(user.id);
            HashMap<String, Object> data = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, Object>[] tripMap = mapper.convertValue(trips, HashMap[].class);
            HashMap<String, Object> userMap = mapper.convertValue(user, HashMap.class);
            int locLen = 0;
            for(Trip trip : trips){
                locLen += trip.locations.length;
            }
            String level = "Beginner user";
            if(locLen > 3){
                level = "Intermediate user";
            }
            if(locLen > 6){
                level = "Experienced user";
            }
            data.put("user", userMap);
            data.put("trips", tripMap);
            data.put("tripLen", trips.length);
            data.put("locLen", locLen);
            data.put("session", super.session);
            data.put("level", level);
            super.render("home", data);
        }
    }
    public void patch() throws Exception {
        User user = new User((String) super.session.get("id"));
        Class<?> c = user.getClass();
        for(String key : super.body.keySet()){
            try{
                Field f = c.getField(key);
                f.set(user, super.body.get(key));
            }catch(NoSuchFieldException n){}
        }
        if(super.query.get("key") != null){
            if(files != null){
                if(files.length > 0){
                    boolean upd = true;
                    String[] splits = files[0].filename.split("\\.");
                    String mime = "jpg";
                    if(splits.length > 1){
                        mime = splits[1];
                    }
                    if(mime.matches("jpg|png|svg|bmp")){
                        String filestr = (String) super.session.get("id") + UUID.randomUUID().toString() + "." + mime;
                        String filename = super.root + "/public/upl/" + filestr;
                        files[0].filename = filename;
                        try{
                            files[0].save();
                        }catch(IOException ioe){
                            upd = false;
                        }
                        if(upd){
                            String old = User.getByID(user.id).avatar;
                            File oldFile = new File(super.root + "/public/upl/" + old);
                            oldFile.delete();
                            user.avatar = filestr;
                            super.session.put("avatar", filestr);
                        }
                    }
                }
            }
        }
        user.update();
        super.redirect("/user/" + user.id, 302);
    }
}