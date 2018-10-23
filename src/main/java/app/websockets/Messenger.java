package app.websockets;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.time.LocalDateTime;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import advance.Server;
import app.models.Message;
public class Messenger extends WebSocketServer {
    public static WebSocketStore connections = new WebSocketStore();
    public Messenger(int port){
        super(new InetSocketAddress(port));
    }
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake){
        String uri = handshake.getResourceDescriptor();
        String sid = uri.split("/")[1];
        HashMap<String, Object> sess = Server.sessionStore.get(sid);
        connections.set(sess, conn);
    }  
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote){
        connections.remove(conn);
    }
    @Override
    public void onMessage(WebSocket conn, String message){
        HashMap<String, Object> sess = connections.get(conn);
        String conversationID = (String) sess.get("conversationID");
        String userEmail = (String) sess.get("email");
        LocalDateTime sentAt = LocalDateTime.now();
        connections.sendByConversationID(conversationID, message + "|" + userEmail + "|" + sentAt);
        Message msg = new Message(message, userEmail, conversationID, sentAt);
        msg.save();
    }   
    @Override
    public void onError(WebSocket conn, Exception ex){
        ex.printStackTrace();
    }
    @Override
    public void onStart(){ }
}