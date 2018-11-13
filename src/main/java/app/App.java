package app;
import app.controllers.*;
import app.models.*;
import app.websockets.*;
import advance.Server;
public class App {
    public static void main(String[] argList){
        String root = "C:/users/isaac/documents/java/kokosole";
        String[] args = new String[0];
        if(argList.length > 0){
          args = argList[0].split(";");
        }
        for(int i = 0; i < args.length; i++){
            switch(args[i].charAt(0)){
                case 'm': //"m-User,Trip,Location"
                    String dbString = args[i].substring(2);
                    if(dbString.equals("all")){
                        User.migrate();
                        Trip.migrate();
                        Location.migrate();
                        Activity.migrate();
                        Conversation.migrate();
                        Message.migrate();
                    }else{
                        String[] dbs = args[i].substring(2).split(",");
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
                                case "Conversation":
                                    Conversation.migrate();
                                    break;
                                case "Message":
                                    Message.migrate();
                                    break;
                            }
                        }
                    }
                    break;
                case 'd': //"m-/your/directory"
                    root = args[i].substring(2);
                    break;
                case 'p':
                    Model.PASSWORD = args[i].substring(2);
                    break;
                case 'u':
                    Model.USERNAME = args[i].substring(2);
                    break;
            }
        }
        Model.init();
        Server app = new Server(9000, root);
        Messenger wss = new Messenger(8000);
        app.setViewDir("/views/");
        app.addController("/", new MainController());
        app.addController("/auth/:action", new AuthController());
        app.addController("/user/:id", new UserController());
        app.addController("/trip/:id", new TripController());
        app.addController("/location/:id", new LocationController());
        app.addController("/activity/:id", new ActivityController());
        app.addController("/conversation/:id", new ConversationController());
        app.addStaticController("/public/");
        app.listen();
        wss.run();
    }
}
