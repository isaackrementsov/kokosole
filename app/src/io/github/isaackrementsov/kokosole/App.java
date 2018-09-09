import framework.Server;
import controllers.*;
public class App {
    public static void main(String[] args){
        Server app = new Server(3000);
        app.addController("/", new MainController());
        app.listen();
    }
}