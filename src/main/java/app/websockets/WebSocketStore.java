package app.websockets;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.ArrayList;
import org.java_websocket.WebSocket;
class WebSocketStore {
    private static HashMap<HashMap<String, Object>, WebSocket> connections = new HashMap<>();
    public void set(HashMap<String, Object> session, WebSocket conn){
        connections.put(session, conn);
    }
    public WebSocket get(HashMap<String, Object> session){
        return connections.get(session);
    }
    public HashMap<String, Object> get(WebSocket ws){
        HashMap<String, Object> sess = new HashMap<>();
        for(HashMap<String, Object> session : connections.keySet()){
            if(ws.equals(connections.get(session))){
                sess = session;
                break;
            }
        }
        return sess;
    }
    public void sendByConversationID(String conversationID, String data){
        for(HashMap<String, Object> session : connections.keySet()){
            if(session.get("conversationID") != null){
                if(session.get("conversationID").equals(conversationID)){
                    WebSocket ws = connections.get(session);
                    if(ws != null){
                        ws.send(data);
                    }
                }
            }
        }
    }
    public void sendByConversationID(String conversationID, byte[] data){
        for(HashMap<String, Object> session : connections.keySet()){
            if(session.get("conversationID").equals(conversationID)){
                connections.get(session).send(data);
            }
        }
    }
    public void remove(WebSocket conn){
        connections.entrySet().removeIf(c -> c.getValue().equals(conn));
    }
}