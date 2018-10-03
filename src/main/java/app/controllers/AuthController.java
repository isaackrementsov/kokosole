package app.controllers;
import java.util.HashMap;
import advance.Controller;
import app.models.*;
public class AuthController extends Controller {
    public void get(){
        if(super.session.get("id") == null){
            String action = super.params.get("action");
            HashMap<String, Object> data = new HashMap<>();
            data.put("session", super.session);
            if(action.equals("signup")){
                super.render("signup", data);
            }else if(action.equals("login")){
                super.render("login", data);
            }else{
                super.responseCode = 404;
            }
        }else{
            super.redirect("/user/" + session.get("id"), 302);
        }
    }
    public void post(){
        String action = super.params.get("action");
        User user = new User(null);
        if(action.equals("logout")){
            super.deleteSession();
            super.redirect("/auth/login", 302);
        }else{
            if(action.equals("signup")){
                user = new User(super.body.get("name"), super.body.get("email"), super.body.get("password"), "avatar.jpg", super.body.get("country"));
                user.save();
            }else if(action.equals("login")){
                user = User.login(super.body.get("email"), super.body.get("password"));
            }else{
                super.responseCode = 404;
            }
            if(user == null){
                super.redirect("/auth/login", 302);
            }else{
                if(action.equals("login") || action.equals("signup")){
                    super.session.put("name", user.name);
                    super.session.put("email", user.email);
                    super.session.put("password", user.password);
                    super.session.put("status", user.status);
                    super.session.put("country", user.country);
                    super.session.put("avatar", user.avatar);
                    super.session.put("id", user.id);
                    super.redirect("/user/"  + user.id, 302);
                }
            }
        }
    }
}