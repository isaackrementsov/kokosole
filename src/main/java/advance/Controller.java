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
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.apache.commons.io.FileUtils;;
public abstract class Controller implements HttpHandler {
    public Server.Param[] rules;
    public OutputStream res;
    public Configuration config;
    protected String contentType = "text/html";
    protected MultiPart[] files;
    protected String formContentType;
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
    /*
        Most file uploading credit goes to apimeister and their FormData class at
        https://apimeister.com/2015/10/10/formdatahandler-implements-com-sun-net-httpserver-httphandler.html
    */
    public static class MultiPart {
        public PartType type;
        public String contentType;
        public String name;
        public String filename;
        public String value;
        public byte[] bytes;
        public void save() throws IOException {
            FileUtils.writeByteArrayToFile(new File(filename), bytes);
        }
    }
    private enum PartType{
        TEXT,FILE
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
                return cookies.get(index).split(";")[0].split("=")[1];
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
            Headers h = this.rawExchange.getRequestHeaders();
            this.formContentType = h.getFirst("Content-Type");
            BufferedReader br = new BufferedReader(isr);
            int i;
            StringBuilder sb = new StringBuilder();
            if(this.formContentType == null){
                while((i = br.read()) != -1){
                    sb.append((char) i);
                }
            }else{
                if(this.formContentType.contains("multipart/form-data")){
                    this.files = uploadFile(is);
                }else{
                    while((i = br.read()) != -1){
                        sb.append((char) i);
                    }  
                }
            }
            br.close();
            isr.close();
            HashMap<String, String> requestBody;
            if(this.formContentType == null){
                requestBody = separateQuery(sb.toString());
            }else{
                if(this.formContentType.startsWith("multipart/form-data")){
                    requestBody = new HashMap<>();
                }else{
                    requestBody = separateQuery(sb.toString());
                }
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
    public static byte[] getInputAsBinary(InputStream requestStream) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try{
            byte[] buf = new byte[100000];
            int bytesRead = 0;
            while((bytesRead = requestStream.read(buf)) != -1){
                bos.write(buf, 0, bytesRead);
            }
            requestStream.close();
            bos.close();
        }catch(IOException e){
            System.out.println("Error uploading files: " + e);
            e.printStackTrace();
        }
        return bos.toByteArray();
    }
    public List<Integer> searchBytes(byte[] srcBytes, byte[] searchBytes, int searchStartIndex, int searchEndIndex) {
        final int destSize = searchBytes.length;
        final List<Integer> positionIndexList = new ArrayList<Integer>();
        int cursor = searchStartIndex;
        while(cursor < searchEndIndex + 1) {
            int index = indexOf(srcBytes, searchBytes, cursor, searchEndIndex);
            if(index >= 0) {
                positionIndexList.add(index);
                cursor = index + destSize;
            }else{
                cursor++;
            }
        }
        return positionIndexList;
    }
    public int indexOf(byte[] srcBytes, byte[] searchBytes, int startIndex, int endIndex) {
        if(searchBytes.length == 0 || (endIndex - startIndex + 1) < searchBytes.length){
            return -1;
        }
        int maxScanStartPosIdx = srcBytes.length - searchBytes.length;
        final int loopEndIdx;
        if(endIndex < maxScanStartPosIdx){
            loopEndIdx = endIndex;
        }else{
            loopEndIdx = maxScanStartPosIdx;
        }
        int lastScanIdx = -1;
        label:
        for(int i = startIndex; i <= loopEndIdx; i++) {
            for(int j = 0; j < searchBytes.length; j++) {
                if(srcBytes[i + j] != searchBytes[j]) {
                    continue label;
                }
                lastScanIdx = i + j;
            }
            if(endIndex < lastScanIdx || lastScanIdx - i + 1 < searchBytes.length) {
                return -1;
            }
            return i;
        }
        return -1;
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
            he.close();
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
    protected MultiPart[] uploadFile(InputStream in){
        ArrayList<MultiPart> list = new ArrayList<>();
        if(this.formContentType.contains("multipart/form-data")){
            String boundary = formContentType.substring(formContentType.indexOf("boundary=") + 9);
            byte[] boundaryBytes = ("\r\n--" + boundary).getBytes(Charset.forName("UTF-8"));
            byte[] payload = getInputAsBinary(in);
            List<Integer> offsets = searchBytes(payload, boundaryBytes, 0, payload.length - 1);
            for(int idx = 0; idx < offsets.size(); idx++){
                int startPart = offsets.get(idx);
                int endPart = payload.length;
                if(idx < offsets.size() - 1){
                    endPart = offsets.get(idx+1);
                }
                byte[] part = Arrays.copyOfRange(payload,startPart,endPart);
                int headerEnd = indexOf(part,"\r\n\r\n".getBytes(Charset.forName("UTF-8")),0,part.length-1);
                if(headerEnd > 0){
                    MultiPart p = new MultiPart();
                    byte[] head = Arrays.copyOfRange(part, 0, headerEnd);
                    String header = new String(head);
                    int nameIndex = header.indexOf("\r\nContent-Disposition: form-data; name=");
                    if(nameIndex >= 0){
                        int startMarker = nameIndex + 39;
                        int fileNameStart = header.indexOf("; filename=");
                        if (fileNameStart >= 0) {
                            String filename = header.substring(fileNameStart + 11, header.indexOf("\r\n", fileNameStart));
                            p.filename = filename.replace('"', ' ').replace('\'', ' ').trim();
                            p.name = header.substring(startMarker, fileNameStart).replace('"', ' ').replace('\'', ' ').trim();
                            p.type = PartType.FILE;
                        }else{
                            int endMarker = header.indexOf("\r\n", startMarker);
                            if(endMarker == -1) endMarker = header.length();
                            p.name = header.substring(startMarker, endMarker).replace('"', ' ').replace('\'', ' ').trim();
                            p.type = PartType.TEXT;
                        }
                    }else{
                        continue;
                    }
                    int typeIndex = header.indexOf("\r\nContent-Type:");
                    if(typeIndex >= 0){
                        int startMarker = typeIndex + 15;
                        int endMarker = header.indexOf("\r\n", startMarker);
                        if (endMarker == -1)
                            endMarker = header.length();
                        p.contentType = header.substring(startMarker, endMarker).trim();
                    }
                    if(p.type == PartType.TEXT){
                        byte[] body = Arrays.copyOfRange(part, headerEnd + 4, part.length);
                        p.value = new String(body);
                    }else{
                        p.bytes = Arrays.copyOfRange(part, headerEnd + 4, part.length);
                    }
                    list.add(p);
                }
            }
        }
        return list.toArray(new MultiPart[list.size()]);
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