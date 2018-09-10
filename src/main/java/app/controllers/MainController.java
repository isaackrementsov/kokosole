package app.controllers;
import advance.Controller;
import com.sun.net.httpserver.HttpExchange;
public class MainController extends Controller {
    public void get(HttpExchange he){
        super.response = "Hello world!".getBytes();
    }
}