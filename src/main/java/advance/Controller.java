package advance;
import java.net.URI;
import java.net.URLDecoder;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import com.sun.net.httpserver.*;
public abstract class Controller implements HttpHandler {
    public int responseCode = 200;
    public byte[] response = {};
    public HashMap<String, Object> data = new HashMap<>();
    public HashMap<String, String> params = new HashMap<>();
    public HashMap<String, String> query = new HashMap<>();
    public HashMap<String, Object> session = new HashMap<>();
    public HashMap<String, String> headerEdits = new HashMap<>();
    public Server.Param[] rules;
    private OutputStream res;
    private ArrayList<HashMap<String, Object>> sessionStore = new ArrayList<>();
    private int sessIndex = -1;
    public Controller(){
        this.data = null;
    }
    public Controller(HashMap<String, Object> data){
        this.data = data;
    }
    private void parseQuery(URI url){
        HashMap<String, String> queryPairs = new HashMap<String, String>();
        String query = url.getQuery();
        String[] pairs;
        if(query == null){
            pairs = new String[]{};
        }else{
            pairs = query.split("&");
        }
        for(String pair : pairs) {
            int i = pair.indexOf("=");
            try {
                queryPairs.put(URLDecoder.decode(pair.substring(0, i), "UTF-8"), URLDecoder.decode(pair.substring(i + 1), "UTF-8"));   
            }catch(UnsupportedEncodingException ue){
                System.out.println("Problem decoding url: " + ue);
            }
        }
        this.query = queryPairs;
    }
    private void parseParams(URI httpUrl){
        String url = httpUrl.getPath();
        HashMap<String, String> paramsToParse = new HashMap<>();
        for(Server.Param p : this.rules){
            int end;
            int slashIndex = url.substring(p.start).indexOf("/");
            if(slashIndex == -1){
                end = url.length(); 
            }else{
                end = slashIndex + p.start;
            }
            paramsToParse.put(p.name, url.substring(p.start, end));
        }
        this.params = paramsToParse;
    }
    private String getSID(List<String> cookies){
        if(cookies == null){
            return null;
        }else{
            int index = IntStream.range(0, cookies.size())
                .filter(i -> cookies.get(i).contains("SID"))
                    .findFirst()
                        .orElse(-1);
            if(index == -1){
                return null;
            }else{
                return cookies.get(index).split("=")[1];
            }
        }
    }
    private void saveSession(){
        if(this.sessIndex == -1){
            sessionStore.add(this.session);
        }else{
            sessionStore.add(sessIndex, this.session);
        }
    }
    private void getSession(HttpExchange he){
        Headers reqHeaders = he.getRequestHeaders();
        List<String> cookies = reqHeaders.get("Cookie");
        String sid = this.getSID(cookies);
        HashMap<String, Object> sessionDoc;
        if(sid == null){
            sessionDoc = new HashMap<>();
            String uuid = UUID.randomUUID().toString();
            sessionDoc.put("SID", uuid);
            this.headerEdits.put("Set-Cookie", uuid);
        }else{
            sessionDoc = this.sessionStore.get(IntStream.range(0, sessionStore.size())
                .filter(i -> this.sessionStore.get(i).get("SID").equals(sid))
                    .findFirst()
                        .orElse(-1));
            if(sessionDoc == null){
                sessionDoc = new HashMap<>();
                sessionDoc.put("SID", sid);
            }
        }
        this.session = sessionDoc;
    }
    public void handle(HttpExchange he){
        this.res = he.getResponseBody();
        String method = he.getRequestMethod();
        URI url = he.getRequestURI();
        this.parseParams(url);
        this.parseQuery(url); 
        this.getSession(he);
        try {
            switch(method){
                case "GET":
                    this.get(he);
                    break;
                case "POST":
                    this.post(he);
                    break;
                case "PUT":
                    this.put(he);
                    break;
                case "PATCH":
                    this.patch(he);
                    break;
                case "DELETE":
                    this.delete(he);
                    break;
            }
            saveSession();
            Headers resHeaders = he.getResponseHeaders();
            for(String key : this.headerEdits.keySet()){
                resHeaders.set(key, this.headerEdits.get(key));
            }
            he.sendResponseHeaders(this.responseCode, this.response.length);
            this.res.write(this.response);
            this.res.flush();
            this.res.close();
        }catch(Exception e){
            System.out.println("Controller exception: " + e);
        }
    }
    public void get(HttpExchange he) throws Exception {
        this.responseCode = 405;
        this.response = "Method not allowed".getBytes();
    }
    public void post(HttpExchange he) throws Exception { 
        this.responseCode = 405;
        this.response = "Method not allowed".getBytes();
    } 
    public void put(HttpExchange he) throws Exception { 
        this.responseCode = 405;
        this.response = "Method not allowed".getBytes();
    }
    public void patch(HttpExchange he) throws Exception { 
        this.responseCode = 405;
        this.response = "Method not allowed".getBytes();
    } 
    public void delete(HttpExchange he) throws Exception { 
        this.responseCode = 405;
        this.response = "Method not allowed".getBytes();
    }
}