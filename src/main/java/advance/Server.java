package advance;
import com.sun.net.httpserver.*;
import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Arrays;
import freemarker.template.*;
public class Server {
    public int port;
    public Configuration config;
    public String viewDir;
    public static SessionStore sessionStore = new DefaultStore();
    private String root;
    private HttpServer server;
    public Server(int port, String root){
        this.port = port;
        this.root = root;
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        }catch(IOException e){
            System.out.println("Error creating server:  " + e);
        }
    }
    public void setViewDir(String viewDir){
        this.viewDir = viewDir;
        this.config = this.configureTemplating();
    }
    public void addController(String url, Controller controller){
        controller.rules = this.setParams(url);
        controller.root = this.root;
        controller.viewDir = this.viewDir;
        controller.config = this.config;
        url = url.split(":")[0];
        this.server.createContext(url, controller);
    }
    public void addStaticController(String url){
        StaticController staticController = new StaticController();
        staticController.root = this.root;
        this.addController(url, staticController);
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
    private Configuration configureTemplating(){
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_27);
        try{
            cfg.setDirectoryForTemplateLoading(new File(this.root + this.viewDir));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setLogTemplateExceptions(false);
            cfg.setWrapUncheckedExceptions(true);
        }catch(IOException ioe){
            System.out.println("Template configuration failed " + ioe);
            System.out.println("Is Apache Freemarker installed?");
        }
        return cfg;
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