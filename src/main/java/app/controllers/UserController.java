package app.controllers;
import advance.Controller;
import app.models.*;
public class UserController extends Controller {
    public void get(){
        super.render("home", super.session);
    }
}