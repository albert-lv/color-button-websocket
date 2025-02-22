package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.handler.ColorWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final ColorWebSocketHandler colorWebSocketHandler;

    @Autowired
    public WebSocketConfig(ColorWebSocketHandler colorWebSocketHandler) {
        this.colorWebSocketHandler = colorWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(colorWebSocketHandler, "/color-websocket")
               .setAllowedOrigins("*");
    }
}
