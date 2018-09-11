package app.controllers;
import advance.Controller;
public class MainController extends Controller {
    public void get(){
        super.render("main", null);
    }
}