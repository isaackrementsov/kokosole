package app.controllers;
import java.util.HashMap;
import advance.Controller;
public class MainController extends Controller {
    public void get(){
        HashMap<String, Object> data = new HashMap<>();
        data.put("session", super.session);
        System.out.println(super.session);
        super.render("main", data);
    }
}