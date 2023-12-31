package com.example.roundrobin.controller;

import com.example.roundrobin.config.InstanceProperties;
import com.example.roundrobin.model.GameInfo;
import com.example.roundrobin.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private InstanceProperties instanceProperties;

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    @PostMapping("/api/game")
    Object newGame(@RequestBody GameInfo game) {
        logger.info("Request to game api");
        try {
            // Get Instance URL based on Round-robin algorithm.
            String instance = gameService.getInstance();

            // Execute Application API
            return gameService.executeAPI(instance, game);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
