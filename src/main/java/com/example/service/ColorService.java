package com.example.service;

import org.springframework.stereotype.Service;
import com.example.event.ColorEvent;
import com.example.event.ColorEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ColorService {
    private static final String[] COLORS = {
        "#FF0000", // Red
        "#FFA500", // Orange
        "#FFFF00", // Yellow
        "#008000", // Green
        "#0000FF", // Blue
        "#4B0082", // Indigo
        "#9400D3"  // Violet
    };
    
    private int currentColorIndex = 0;
    private final List<ColorEventListener> listeners = new ArrayList<>();
    private final EventBridgeService eventBridgeService;
    private final Random random = new Random();

    public ColorService(EventBridgeService eventBridgeService) {
        this.eventBridgeService = eventBridgeService;
    }

    public void addListener(ColorEventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ColorEventListener listener) {
        listeners.remove(listener);
    }

    public String nextColor() {
        currentColorIndex = (currentColorIndex + 1) % COLORS.length;
        String newColor = COLORS[currentColorIndex];
        notifyListeners(new ColorEvent(newColor));
        return newColor;
    }

    public String randomColor() {
        currentColorIndex = random.nextInt(COLORS.length);
        String newColor = COLORS[currentColorIndex];
        // 发送事件到EventBridge
        eventBridgeService.sendColorEvent(newColor);
        return newColor;
    }

    private void notifyListeners(ColorEvent event) {
        listeners.forEach(listener -> listener.onColorChanged(event));
    }

    public String getCurrentColor() {
        return COLORS[currentColorIndex];
    }

    public void setColor(String color) {
        // 验证颜色格式
        if (!color.matches("^#[0-9A-Fa-f]{6}$")) {
            throw new IllegalArgumentException("Invalid color format. Expected format: #RRGGBB");
        }
        
        // 通知所有监听器颜色变化
        notifyListeners(new ColorEvent(color));
    }
}
