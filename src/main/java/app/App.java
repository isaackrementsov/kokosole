package app;
import app.controllers.*;
import app.models.*;
import advance.Server;
public class App {
    public static void main(String[] args){
        Server app = new Server(9000, "C:/users/isaac/documents/java/kokosole");
        if(args.length > 0){
            if(args[0].equals("migrate")){
                User.migrate();
                Trip.migrate();
                Location.migrate();
                Activity.migrate();
            }    
        }    
        app.setViewDir("/views/");
        app.addController("/", new MainController());
        app.addController("/auth/:action", new AuthController());
        app.addController("/user/:id", new UserController());
        app.addController("/trip/:id", new TripController());
        app.addController("/location/:id", new LocationController());
        app.addController("/activity/:id", new ActivityController());
        app.addStaticController("/public/");
        app.listen();
    }
}