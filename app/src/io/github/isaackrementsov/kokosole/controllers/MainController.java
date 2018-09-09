package io.github.isaackrementsov.controllers;
import framework.Controller;
import com.sun.net.httpServer.HttpExchange;
public class MainController extends Controller {
    public void get(HttpExchange he){
        String response = "Hello world!";
        responseLength = response.length();
        res.write(response.getBytes());
    }
}