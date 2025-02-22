package com.example.controller;

import com.example.model.ColorEventData;
import com.example.service.ColorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/function")
public class FunctionCallbackController {
    private static final Logger logger = LoggerFactory.getLogger(FunctionCallbackController.class);
    private final ColorService colorService;

    public FunctionCallbackController(ColorService colorService) {
        this.colorService = colorService;
    }

    @PostMapping("/callback")
    public ResponseEntity<String> handleColorCallback(@RequestBody ColorEventData eventData) {
        try {
            logger.info("Received function callback with data: {}", eventData);
            String color = eventData.getColor();

            if (color != null && !color.isEmpty()) {
                logger.info("Setting color to: {}", color);
                colorService.setColor(color);
                return ResponseEntity.ok("Color updated successfully");
            } else {
                logger.warn("Color not found in event data");
                return ResponseEntity.badRequest().body("Color not found in event data");
            }
        } catch (Exception e) {
            logger.error("Failed to process event", e);
            return ResponseEntity.badRequest().body("Failed to process event: " + e.getMessage());
        }
    }
}
