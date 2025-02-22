package com.example.service;

import com.alibaba.fastjson.JSON;
import com.aliyun.eventbridge.EventBridgeClient;
import com.aliyun.eventbridge.models.CloudEvent;
import com.aliyun.eventbridge.models.PutEventsResponse;
import com.aliyun.eventbridge.util.EventBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class EventBridgeService {

    private final EventBridgeClient eventBridgeClient;
    
    @Value("${aliyun.eventbridge.eventBusName}")
    private String eventBusName;

    public EventBridgeService(EventBridgeClient eventBridgeClient) {
        this.eventBridgeClient = eventBridgeClient;
    }

    public void sendColorEvent(String color) {
        try {
            // 构建事件数据
            Map<String, String> data = new HashMap<>();
            data.put("color", color);
            data.put("timestamp", String.valueOf(System.currentTimeMillis()));

            // 使用EventBuilder创建CloudEvent
            CloudEvent cloudEvent = EventBuilder.builder()
                    .withId(UUID.randomUUID().toString())
                    .withSource(URI.create("color-button-app"))
                    .withType("com.example.color.changed")
                    .withSubject("ColorChangeEvent")
                    .withTime(new Date())
                    .withJsonStringData(JSON.toJSONString(data))
                    .withAliyunEventBus(eventBusName)
                    .build();

            // 发送事件
            PutEventsResponse response = eventBridgeClient.putEvents(Collections.singletonList(cloudEvent));
            
            // 处理响应
            if (response.getFailedEntryCount() != null && response.getFailedEntryCount() > 0) {
                throw new RuntimeException("Failed to send some events to EventBridge");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to send color event to EventBridge", e);
        }
    }
}
