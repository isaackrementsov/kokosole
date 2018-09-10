package app.controllers;
import advance.Controller;
public class MainController extends Controller {
    public void get(){
        String resp = "<html><head></head><body><form method='post' action='/'><input type='text' name='msg'><input type='text' name='details'><input type='submit' value='Submit'></form></body></html>";
        super.response = resp.getBytes();
    }
    public void post(){
        headerEdits.put("Location", "/");
        super.responseCode = 302;
    }
}