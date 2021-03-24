package vn.thuephonghoc.websocket;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/webSocket/{sid}")
@Slf4j
@Component
public class WebSocketServer {

	/**
     * The thread-safe Set of the concurrent package is used to store the MyWebSocket object corresponding to each client.
     */
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    /**
     * A connection session with a client requires it to send data to the client
     */
    private Session session;

    /**
     * Receive sid
     */
    private String sid="";
    /**
     * The method to call successfully when the connection is established
     * */
    @OnOpen
    public void onOpen(Session session,@PathParam("sid") String sid) {
        this.session = session;
        //If it exists, delete one first to prevent repeated push messages
        for (WebSocketServer webSocket:webSocketSet) {
            if (webSocket.sid.equals(sid)) {
                webSocketSet.remove(webSocket);
            }
        }
        webSocketSet.add(this);
        this.sid=sid;
    }

    /**
     * The method called when the connection is closed
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
    }

    /**
     * Method to be called after receiving client message
     * @param message The message sent by the client */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("Received "+sid+" information: "+message);
        //Group message
        for (WebSocketServer item: webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("An error occurred");
        error.printStackTrace();
    }
    /**
     * Realize server active push
     */
    private void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * Send custom messages in bulk
     * */
    public static void sendInfo(SocketMsg socketMsg,@PathParam("sid") String sid) throws IOException {
        String message = JSONObject.toJSONString(socketMsg);
        log.info("Push message to "+sid+", push content: "+message);
        for (WebSocketServer item: webSocketSet) {
            try {
                // Here can set to only push to this sid, if it is null, push all
                if(sid==null) {
                    item.sendMessage(message);
                }else if(item.sid.equals(sid)){
                    item.sendMessage(message);
                }
            } catch (IOException ignored) { }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebSocketServer that = (WebSocketServer) o;
        return Objects.equals(session, that.session) &&
                Objects.equals(sid, that.sid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(session, sid);
    }
}
