package app;
import app.controllers.*;
import advance.Server;
public class App {
    public static void main(String[] args){
        Server app = new Server(9000, "C:/users/isaac/documents/java/kokosole");
        app.setViewDir("/views/");
        app.addController("/", new MainController());
        app.addController("/auth/:action", new AuthController());
        app.addStaticController("/public/");
        app.listen();
    }
}