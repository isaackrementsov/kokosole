package advance;
import java.util.HashMap;
public interface SessionStore {
    HashMap<String, Object> get(String key);
    void set(String key, HashMap<String, Object> value);
}