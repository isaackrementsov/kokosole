package app.controllers;
import advance.Controller;
import app.models.*;
public class AuthController extends Controller {
    public void get(){
        String action = super.params.get("action");
        if(action.equals("signup")){
            super.render("signup", null);
        }else if(action.equals("login")){
            super.render("login", null);
        }else{
            super.responseCode = 404;
        }
    }
    public void post(){
        String action = super.params.get("action");
        User user = new User(null, null, null);
        if(action.equals("signup")){
            user = new User(super.body.get("name"), super.body.get("email"), super.body.get("password"));
            user.save();
        }else if(action.equals("login")){
            user = User.login(super.body.get("email"), super.body.get("password"));
        }else{
            super.responseCode = 404;
        }
        if(action.equals("login") || action.equals("signup")){
            session.put("name", user.name);
            session.put("email", user.email);
            session.put("password", user.password);
            session.put("status", user.status);
            super.redirect("/user/"  + user.id, 302);
        }
    }
}