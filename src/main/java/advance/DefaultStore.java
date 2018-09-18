package advance;
import java.util.HashMap;
import java.util.ArrayList;
public class DefaultStore implements SessionStore {
    HashMap<String, HashMap<String, Object>> store;
    public DefaultStore(){
        this.store = new HashMap<>();
    }
    public HashMap<String, Object> get(String key){
        return store.get(key);
    }
    public void set(String key, HashMap<String, Object> value){
        store.put(key, value);
    }
}