package com.example.handler;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.service.ColorService;
import com.example.event.ColorEvent;
import com.example.event.ColorEventListener;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class ColorWebSocketHandler extends TextWebSocketHandler implements ColorEventListener {
    private final ColorService colorService;
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Autowired
    public ColorWebSocketHandler(ColorService colorService) {
        this.colorService = colorService;
        this.colorService.addListener(this);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        session.sendMessage(new TextMessage(colorService.getCurrentColor()));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        colorService.nextColor();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        sessions.remove(session.getId());
    }

    @Override
    public void onColorChanged(ColorEvent event) {
        TextMessage colorMessage = new TextMessage(event.getColor());
        sessions.values().forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(colorMessage);
                }
            } catch (IOException e) {
                // Handle exception appropriately
            }
        });
    }
}
