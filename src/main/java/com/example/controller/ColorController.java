package com.example.controller;

import com.example.service.ColorService;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/color")
public class ColorController {
    private static final Logger logger = LoggerFactory.getLogger(ColorController.class);
    private final ColorService colorService;

    public ColorController(ColorService colorService) {
        this.colorService = colorService;
    }

    @PostMapping("/next")
    public ResponseEntity<ColorResponse> nextColor() {
        String newColor = colorService.nextColor();
        return ResponseEntity.ok(new ColorResponse(newColor));
    }

    @PostMapping("/random")
    @RateLimiter(name = "randomColor", fallbackMethod = "randomColorFallback")
    public ResponseEntity<ColorResponse> randomColor() {
        String newColor = colorService.randomColor();
        return ResponseEntity.ok(new ColorResponse(newColor));
    }

    public ResponseEntity<ErrorResponse> randomColorFallback(RequestNotPermitted e) {
        logger.warn("Rate limit exceeded for random color request");
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new ErrorResponse("Too many random color requests. Please try again later."));
    }

    @GetMapping("/current")
    public ResponseEntity<ColorResponse> getCurrentColor() {
        String currentColor = colorService.getCurrentColor();
        return ResponseEntity.ok(new ColorResponse(currentColor));
    }

    // Response DTOs
    private static class ColorResponse {
        private final String color;

        public ColorResponse(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }
    }

    private static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
