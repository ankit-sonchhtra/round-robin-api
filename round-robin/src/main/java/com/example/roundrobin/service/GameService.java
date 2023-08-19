package com.example.roundrobin.service;

import com.example.roundrobin.config.InstanceProperties;
import com.example.roundrobin.model.GameInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class GameService {
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private InstanceProperties instanceProperties;

    @Autowired
    private RestTemplate restTemplate;

    private final AtomicInteger current_count = new AtomicInteger();

    private static final String API_URL = "/api/game";

    // Get instance url based on round-robin algorithm.
    public String getInstance() {
        List<String> activeInstances = instanceProperties.getActiveInstances();
        if (!activeInstances.isEmpty()) {
            return activeInstances.get(getCount(activeInstances.size()));
        }
        throw new RuntimeException("No active instance is available.");
    }

    synchronized int getCount(int sizeOfActiveInstances) {
        if (current_count.get() == sizeOfActiveInstances) {
            current_count.set(0);
        }
        return current_count.getAndIncrement();
    }

    // API call to application instance.
    public GameInfo executeAPI(String instance, GameInfo game) {
        logger.debug("Execute api with instance url: " + instance);
        ResponseEntity<GameInfo> response;
        try {
            long startTime = System.currentTimeMillis();
            response = restTemplate.postForEntity(instance + API_URL, game, GameInfo.class);
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            if (elapsedTime > 3000) {
                logger.warn("Alert: Application instance url: " + instance + " responding slowly, ElapsedTime:" + elapsedTime);
            }
            return response.getBody();
        } catch (ResourceAccessException e) {
            logger.error("ResourceAccessException occurred while calling to application uri: " + instance + " Error message:" + e.getMessage());
            instanceProperties.removeInactiveInstance(instance);
            return executeAPI(getInstance(), game);
        } catch (Exception e) {
            logger.error("Exception occurred while calling to application uri: " + instance + " Error message:" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
