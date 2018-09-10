package app.controllers;
import advance.Controller;
public class MainController extends Controller {
    public void get(){
        super.render("mainPageForSite", null);
    }
    public void post(){
        super.redirect("/", 302);
    }
}