package advance;
import java.net.URI;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
public class StaticController extends Controller {  
    public StaticController(){
        super();
        super.setSession = false;
    }
    public void get() throws IOException {
        URI url = rawExchange.getRequestURI();
        String path = url.getPath();
        File file = new File(super.root + path);
        if(file.isFile()){
            Path filePath = Paths.get(super.root + path);
            String mime = Files.probeContentType(filePath);
            super.overrideSendHeaders = true;
            super.overrideWrite = true;
            super.overrideHeaders = true;
            super.overrideClose = true;
            super.headerEdits.put("Content-Type", mime);
            super.rawExchange.sendResponseHeaders(responseCode, response.length);
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[0x10000];
            int i = 0;
            while((i = fis.read(buffer)) >= 0){
                super.res.write(buffer, 0, i);
            }
            fis.close();
            super.res.close();
        }else{
            super.response = "404 (Not Found)\n".getBytes();
            super.responseCode = 404;
        }
    }
}