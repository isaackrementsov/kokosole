package app.controllers;
import advance.Controller;
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
        if(action.equals("signup")){
            
        }else if(action.equals("login")){

        }else{
            super.responseCode = 404;
        }
    }
}