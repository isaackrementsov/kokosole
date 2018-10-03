package advance;
import java.net.URI;
import java.io.BufferedOutputStream;
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
        if(file.isFile() && file.exists()){
            Path filePath = Paths.get(super.root + path);
            String mime = Files.probeContentType(filePath);
            BufferedOutputStream out = new BufferedOutputStream(super.res);
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
                out.write(buffer, 0, i);
            }
            fis.close();
            out.close();
            super.res.close();
        }
    }
}