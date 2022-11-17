package com.udbna.tools;

import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WebSocket {
    public class SocketResponse {
        public String content = "";
        public int loopCount = 0;
    }
    String curl = "";
    String u = "";
    String p = "";
    public WebSocket(String url, String user, String pass) {
        u = user;
        p = pass;
        if(new WebReq().ping(url)) {
            curl = url;
        }else{
            System.out.println("Error in WebSockets cant connect to Socket address");
        }
    }
    public SocketResponse sendCode(String code) {
        String response = "";
        int tries = 0;
        try {
            ArrayList<String> cr = new ArrayList<>();
            WebSocketClient client = new StandardWebSocketClient();
            WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
            WebReq req = new WebReq();
            req.login(u,p);
            headers.add("cookie", "cdsess=" + req.cookieStore.getCookies().get(0).getValue() + ";");
            final boolean[] end = {true};
            final boolean[] sendzero = {false};
            while(end[0]) {
                client.doHandshake(new WebSocketHandler() {
                    @Override
                    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                        session.sendMessage(new TextMessage("0"));
                    }

                    @Override
                    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                        if(!sendzero[0]) {
                            session.sendMessage(new TextMessage(code));
                            sendzero[0] = true;
                        }else {
                            cr.add(message.getPayload().toString());
                            end[0] = false;
                        }

                    }

                    @Override
                    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                        System.err.println("Transport Error in WebSocket");
                    }

                    @Override
                    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                    }

                    @Override
                    public boolean supportsPartialMessages() {
                        return true;
                    }
                }, headers, new URI(curl)).get(1, TimeUnit.DAYS);
                if(!end[0]) {
                    break;
                }
                tries++;
            }
            response = cr.get(0);

        } catch (URISyntaxException e) {
            System.out.println("Error by connection in WebSocket");
            e.printStackTrace();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        if(response.contains("44")) {
            System.err.println("Authentication failed in WebSocket");
        }
        SocketResponse response1 = new SocketResponse();
        response1.content = response;
        response1.loopCount = tries;
        return response1;
    }
}
