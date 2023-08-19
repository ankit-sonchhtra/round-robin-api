package com.example.application.controller;

import com.example.application.model.GameInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);
    public int counter = 0;
    @PostMapping("/api/game")
    GameInfo newGame(@RequestBody GameInfo game) throws InterruptedException {
        logger.info("Welcome to game api");
        counter++;
        logger.info("Latest Count: "+ counter);
        return game;
    }
}
