package com.example.roundrobin.service;

import com.example.roundrobin.config.InstanceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class HealthCheckService {

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private InstanceProperties instanceProperties;

    private static final String HEALTH_ENDPOINT_URL = "/actuator/health";

    // Scheduled health check for all application instance on configurable time-interval.
    @Scheduled(fixedRateString = "${healthCheck.fixedRate.inMilliSeconds}")
    public void scheduleTask() {
        logger.info("Scheduler executed for instance health check");
        instanceProperties.getInstances().forEach((value) -> {
            String response = "";
            try {
                response = restTemplate.getForEntity(value + HEALTH_ENDPOINT_URL, String.class).getBody();
            } catch (ResourceAccessException e) {
                logger.error("ResourceAccessException while health-check to application uri: " + value + " Error message:" + e.getMessage());
                instanceProperties.removeInactiveInstance(value);
            } catch (Exception e) {
                logger.error("Exception while calling to health-check api: " + value + " Error message:" + e.getMessage());
                throw new RuntimeException(e);
            }
            String expectedRes = "{\"status\":\"UP\"}";
            if (response != null && response.equals(expectedRes)) {
                instanceProperties.populateActiveInstance(value);
            } else {
                instanceProperties.removeInactiveInstance(value);
            }
        });
    }
}
