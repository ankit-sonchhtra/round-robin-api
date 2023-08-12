package com.example.roundrobin.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

@Configuration
public class InstanceProperties {

    private static final Logger logger = LoggerFactory.getLogger(InstanceProperties.class);

    @Value("${instances.url:http://localhost:8081,http://localhost:8082,http://localhost:8083}")
    private String instanceURL;

    private final List<String> instances = new LinkedList<>();

    private final List<String> activeInstances = new LinkedList<>();


    public String getInstanceURL() {
        return instanceURL;
    }

    public void setInstanceURL(String instanceURL) {
        this.instanceURL = instanceURL;
    }

    public List<String> getInstances() {
        if (instances.isEmpty()) {
            Collections.addAll(instances, instanceURL.trim().split(","));
        }
        return instances;
    }

    public List<String> getActiveInstances() {
        return activeInstances;
    }

    public void populateActiveInstance(String url) {
        if (!activeInstances.contains(url)) {
            logger.info("Adding instance to active instances: " + url);
            activeInstances.add(url);
        }
    }

    public void removeInactiveInstance(String value) {
        if (activeInstances.contains(value)) {
            logger.info("Removing instance from active instances: " + value);
            activeInstances.remove(value);
        }
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}