package advance;
import com.sun.net.httpserver.*;
import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Arrays;
public class Server {
    public int port;
    private HttpServer server = null;
    public Server(int port){
        this.port = port;
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        }catch(IOException e){
            System.out.println("Error creating server:  " + e);
        }
    }
    public void addController(String url, Controller controller){
        this.server.createContext(url, controller);
        controller.rules = setParams(url);
    }
    public void addStaticController(String url, String root){
        StaticController staticController = new StaticController();
        this.addController(url, staticController);
        staticController.root = root;
    }
    private Param[] setParams(String format){
        Param[] params = new Param[format.split(":").length - 1];
        boolean paramStart = false;
        int p = 0;
        int i = 0;
        while(i < format.length()){
            if(format.charAt(i) == ':'){
                params[p] = new Param(i, "");
                paramStart = true;
            }
            if(paramStart){
                if(format.charAt(i) == '/'){
                    p++;
                    paramStart = false;
                }else{
                    params[p].name += Character.toString(format.charAt(i));
                }
            }
            i++;
        }
        return params;
    }
    public static class Param {
        int start;
        String name;
        public Param(int start, String name){
            this.name = name;
            this.start = start;
        }
    }
    public void listen(){
        this.server.setExecutor(null);
        this.server.start();
    }
}