package app;
import app.controllers.*;
import app.models.*;
import advance.Server;
public class App {
    public static void main(String[] args){
        String root = "C:/users/isaac/documents/java/kokosole";
        for(int i = 0; i < args.length; i++){
            switch(args[i].charAt(0)){
                case 'm': //"m-User,Trip,Location"
                    String dbString = args[i].substring(2);
                    if(dbString.equals("all")){
                        User.migrate();
                        Trip.migrate();
                        Location.migrate();
                        Activity.migrate();
                    }else{
                        String[] dbs = args[i].split(",");
                        for(String db : dbs){
                            switch(db){
                                case "User":
                                    User.migrate();
                                    break;
                                case "Trip":
                                    Trip.migrate();
                                    break;
                                case "Location":
                                    Location.migrate();
                                    break;
                                case "Activity":
                                    Activity.migrate();
                                    break;
                            }
                        }
                    }
                    break;
                case 'd': //"m-/your/directory"
                    root = args[i].substring(2);
                    break;
                case 'c':
                    Model.CONNECTION_STRING = args[i].substring(2);
                    break;
                case 'p':
                    Model.PASSWORD = args[i].substring(2);
                    break;
                case 'u':
                    Model.USERNAME = args[i].substring(2);
                    break;
            }
        }
        Server app = new Server(9000, "C:/users/isaac/documents/java/kokosole");
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