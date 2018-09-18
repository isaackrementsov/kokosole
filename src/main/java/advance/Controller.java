package advance;
import java.net.URI;
import java.net.URLDecoder;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import com.sun.net.httpserver.*;
import freemarker.template.*;
public abstract class Controller implements HttpHandler {
    public String contentType = "text/html";
    public String root;
    public String viewDir;
    public int responseCode = 200;
    public byte[] response = {};
    public Configuration config;
    public HttpExchange rawExchange;
    public boolean overrideSendHeaders = false;
    public boolean overrideWrite = false;
    public boolean overrideHeaders = false;
    public boolean overrideClose = false;
    public HashMap<String, String> body;
    public HashMap<String, Object> data;
    public HashMap<String, String> params;
    public HashMap<String, String> query;
    public HashMap<String, Object> session;
    public HashMap<String, String> headerEdits;
    public Server.Param[] rules;
    public OutputStream res;
    private ArrayList<HashMap<String, Object>> sessionStore = new ArrayList<>();
    private int sessIndex = -1;
    public Controller(){
        this.data = null;
    }
    public Controller(HashMap<String, Object> data){
        this.data = data;
    }
    private void parseQuery(URI url){
        String query = url.getQuery();
        HashMap<String, String> queryPairs = separateQuery(query);
        separateQuery(query);        
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
            sessionDoc = Server.sessionStore.get(sid);
            if(sessionDoc == null){
                sessionDoc = new HashMap<>();
                sessionDoc.put("SID", sid);
            }
        }
        this.session = sessionDoc;
    }
    private void getRequestBody(HttpExchange he){
        try{
            InputStream is = he.getRequestBody();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            int i;
            StringBuilder sb = new StringBuilder();
            while((i = br.read()) != -1){
                sb.append((char) i);
            }
            br.close();
            isr.close();
            HashMap<String, String> requestBody = separateQuery(sb.toString());
            this.body = requestBody;
        }catch(IOException ioe){
            System.out.println("Error parsing request body: " + ioe);
        }
    }
    private HashMap<String, String> separateQuery(String query){
        HashMap<String, String> queryPairs = new HashMap<>();
        String[] pairs;
        if(query == null || query.equals("")){
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
        return queryPairs;
    }
    public void handle(HttpExchange he){
        this.res = he.getResponseBody();
        this.rawExchange = he;
        this.headerEdits = new HashMap<String, String>();
        String method = he.getRequestMethod();
        URI url = he.getRequestURI();
        this.parseParams(url);
        this.parseQuery(url); 
        this.getSession(he);
        this.getRequestBody(he);
        try {
            switch(method){
                case "GET":
                    this.get();
                    break;
                case "POST":
                    this.post();
                    break;
                case "PUT":
                    this.put();
                    break;
                case "PATCH":
                    this.patch();
                    break;
                case "DELETE":
                    this.delete();
                    break;
            }
            Server.sessionStore.set(session.get("SID").toString(), session);
            if(!this.overrideHeaders){
                Headers resHeaders = he.getResponseHeaders();
                resHeaders.set("Content-Type", contentType);
                for(String key : this.headerEdits.keySet()){
                    resHeaders.set(key, this.headerEdits.get(key));
                }
            }   
            if(!this.overrideSendHeaders){
                he.sendResponseHeaders(this.responseCode, this.response.length);
            }
            if(!this.overrideWrite){
                this.res.write(this.response);
            }
            if(!this.overrideClose){
                this.res.flush();
                this.res.close();
            }
        }catch(Exception e){
            System.out.println("Controller exception: " + e);
        }
        this.overrideHeaders = false;
        this.overrideSendHeaders = false;
        this.overrideClose = false;
        this.overrideWrite = false;
        this.contentType = "text/html";
        this.headerEdits = null;
        this.sessIndex = -1;
    }
    public void render(String filename, Object tData){
        if(config != null){
            try{
                this.overrideSendHeaders = true;
                Template temp = config.getTemplate(filename + ".ftlh");
                OutputStreamWriter ow = new OutputStreamWriter(this.res);
                this.rawExchange.sendResponseHeaders(responseCode, 0);
                temp.process(tData, ow);
            }catch(Exception e){
                System.out.println("Render error: " + e);
            }
        }
    }
    public void redirect(String url, int code){
        this.responseCode = code;
        this.headerEdits.put("Location", url);
    }
    public void get() throws Exception {
        this.responseCode = 405;
        this.response = "Method not allowed".getBytes();
    }
    public void post() throws Exception { 
        this.responseCode = 405;
        this.response = "Method not allowed".getBytes();
    } 
    public void put() throws Exception { 
        this.responseCode = 405;
        this.response = "Method not allowed".getBytes();
    }
    public void patch() throws Exception { 
        this.responseCode = 405;
        this.response = "Method not allowed".getBytes();
    } 
    public void delete() throws Exception { 
        this.responseCode = 405;
        this.response = "Method not allowed".getBytes();
    }
}