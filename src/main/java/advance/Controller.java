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
import java.io.File;
import java.util.HashMap;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Iterator;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;
import freemarker.template.*;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
public abstract class Controller implements HttpHandler {
    public Server.Param[] rules;
    public OutputStream res;
    public Configuration config;
    protected String contentType = "text/html";
    protected String root;
    protected String viewDir;
    protected int responseCode = 200;
    protected byte[] response = {};
    protected HttpExchange rawExchange;
    protected boolean overrideSendHeaders = false;
    protected boolean overrideWrite = false;
    protected boolean overrideHeaders = false;
    protected boolean overrideClose = false;
    protected HashMap<String, String> body;
    protected HashMap<String, Object> data;
    protected HashMap<String, String> params;
    protected HashMap<String, String> query;
    protected HashMap<String, Object> session;
    protected HashMap<String, String> headerEdits;
    protected boolean setSession = true;
    public Controller(){
        this.data = null;
    }
    public Controller(HashMap<String, Object> data){
        this.data = data;
    }
    private void parseQuery(URI url){
        String query = url.getQuery();
        HashMap<String, String> queryPairs = separateQuery(query);
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
            paramsToParse.put(p.name.split(":")[1], url.substring(p.start, end));
        }
        this.params = paramsToParse;
    }
    private String getSID(List<String> cookies){
        if(cookies == null){
            return null;
        }else{
            int index = -1;
            for(int i = 0; i < cookies.size(); i++){
                if(cookies.get(i).contains("SID")){
                    index = i;
                }
            }
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
            this.headerEdits.put("Set-Cookie", "SID=" + uuid + ";path=/");
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
            HashMap<String, String> requestBody;
            if(sb.toString().length() > 10000){
                requestBody = new HashMap<>();
            }else{
                requestBody = separateQuery(sb.toString());
            }
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
        this.getRequestBody(he);
        String methodInput = this.body.get("_method");
        String methodQuery = this.query.get("_method");
        if(methodInput != null){
            method = methodInput;
        }
        if(methodQuery != null){
            method = methodQuery;
        }
        if(setSession){
            this.getSession(he);
        }
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
            if(this.setSession){
                Server.sessionStore.set(this.session.get("SID").toString(), this.session);
            }
            Headers resHeaders = he.getResponseHeaders();
            if(!this.overrideHeaders){
                resHeaders.add("Content-Type", contentType);
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
            e.printStackTrace();
        }
        this.body = null;
        this.query = null;
        this.overrideHeaders = false;
        this.overrideSendHeaders = false;
        this.overrideClose = false;
        this.overrideWrite = false;
        this.contentType = "text/html";
        this.headerEdits = null;
    }
    protected void render(String filename, Object tData){
        if(config != null){
            try{
                this.overrideSendHeaders = true;
                this.overrideHeaders = true;
                Template temp = config.getTemplate(filename + ".ftlh");
                OutputStreamWriter ow = new OutputStreamWriter(this.res);
                Headers resHeaders = this.rawExchange.getResponseHeaders();
                resHeaders.add("Content-Type", contentType);
                for(String key : this.headerEdits.keySet()){
                    resHeaders.set(key, this.headerEdits.get(key));
                }
                this.rawExchange.sendResponseHeaders(responseCode, 0);
                temp.process(tData, ow);
            }catch(Exception e){
                System.out.println("Render error: " + e);
            }
        }
    }
    protected void redirect(String url, int code){
        this.responseCode = code;
        this.headerEdits.put("Location", url);
    }
    protected String[] uploadFile(String folder, String name, String mime){
        HttpExchange he = this.rawExchange;
        DiskFileItemFactory d = new DiskFileItemFactory();
        try {
            d.setRepository(new File(this.root));
            ServletFileUpload up = new ServletFileUpload(d);
            List<FileItem> result = up.parseRequest(new RequestContext(){
                public String getCharacterEncoding(){
                    return "UTF-8";
                }
                public int getContentLength(){
                    return 0;
                }
                public String getContentType(){
                    return he.getRequestHeaders().getFirst("Content-Type");
                }
                public InputStream getInputStream() throws IOException {
                    return he.getRequestBody();
                }
            });
            ArrayList<String> files = new ArrayList<>();
            for(FileItem item : result){
                if(item.isFormField()){
                    String fileStr = folder;
                    String fileName = item.getName();
                    if(folder == null){
                        fileStr = "";
                    }
                    if(name == null){
                        fileStr = fileName;
                    }else{
                        fileStr = name;
                    }
                    if(mime == null && name != null){
                        fileStr += "." + fileName.split(".")[fileName.length() - 1];
                    }else if(mime != null){
                        fileStr += mime;
                    }
                    File toUpload = new File(fileStr);  
                    item.write(toUpload);  
                    files.add(fileStr);                
                }
            }
            String[] fileArray = new String[files.size()];
            fileArray = files.toArray(fileArray);
            return fileArray;
        }catch(Exception ioe){
            System.out.println("Error uploading file: " + ioe);
            ioe.printStackTrace();
            return null;
        }
    }
    protected void deleteSession(){
        this.session.keySet().removeIf(key -> !key.equals("SID"));
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