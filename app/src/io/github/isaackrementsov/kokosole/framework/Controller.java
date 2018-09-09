package io.github.isaackrementsov.kokosole.framework;
import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.net.URLDecoder;
import com.sun.net.httpserver.*;
public abstract class Controller implements HttpHandler {
    HashMap<String, Object> data = new HashMap<>();
    HashMap<String, String> params = new HashMap<>();
    HashMap<String, String> query = new HashMap<>();
    HashMap<String, Object> session = new HashMap<>();
    int responseCode = 200;
    int responseLength = 0;
    Server.Param[] rules;
    OutputStream res;
    private ArrayList<HashMap<String, Object>> sessionStore = new ArrayList<>();
    public Controller(){
        this.data = null;
    }
    public Controller(HashMap<String, Object> data){
        this.data = data;
    }
    private void parseQuery(URI url){
        HashMap<String, String> queryPairs = new HashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
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
    private HttpExchange getSession(HttpExchange he){
        Headers reqHeaders = he.getRequestHeaders();
        List<String> cookies = reqHeaders.get("Cookie");
        String sid = getSID(cookies);
        HashMap<String, Object> sessionDoc;
        if(sid == null){
            sessionDoc = new HashMap<>();
            String uuid = UUID.randomUUID().toString();
            sessionDoc.put("SID", uuid);
            reqHeaders.set("Set-Cookie", "SID=" + uuid);
        }else{
            sessionDoc = sessionStore.get(IntStream.range(0, sessionStore.size())
                .filter(i -> sessionStore.get(i).get("SID").equals(sid))
                    .findFirst()
                        .orElse(-1));
            if(sessionDoc == null){
                sessionDoc = new HashMap<>();
                sessionDoc.put("SID", sid);
            }
        }
        this.session = sessionDoc;
        return he;
    }
    public void handle(HttpExchange he){
        res = he.getResponseBody();
        String method = he.getRequestMethod();
        URI url = he.getRequestURI();
        parseParams(url);
        parseQuery(url); 
        he = getSession(he);
        try {
            switch(method){
                case "GET":
                    this.get(he);
                case "POST":
                    this.post(he);
                case "PUT":
                    this.put(he);
                case "PATCH":
                    this.patch(he);
                case "DELETE":
                    this.delete(he);
            }   
            he.sendResponseHeaders(responseCode, responseLength);
        }catch(Exception e){
            System.out.println("Controller exception: " + e);
        }
    
    }
    public void get(HttpExchange he) throws Exception {
        responseCode = 405;
    }
    public void post(HttpExchange he) throws Exception { 
        responseCode = 405;
    } 
    public void put(HttpExchange he) throws Exception { 
        responseCode = 405;
    }
    public void patch(HttpExchange he) throws Exception { 
        responseCode = 405;
    } 
    public void delete(HttpExchange he) throws Exception { 
        responseCode = 405;
    }
}