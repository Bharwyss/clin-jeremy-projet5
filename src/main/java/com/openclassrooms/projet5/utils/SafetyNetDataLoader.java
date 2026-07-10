package com.openclassrooms.projet5.utils;

import com.openclassrooms.projet5.model.SafetyNetData;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;

@Component
public class SafetyNetDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(SafetyNetDataLoader.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private SafetyNetData data;

    @PostConstruct
    public void loadData() {
        try (InputStream inputStream = getClass().getResourceAsStream("/data.json")) {
            this.data = objectMapper.readValue(inputStream, SafetyNetData.class);
            logger.info("Data loaded from JSON file");
        } catch (Exception e) {
            logger.error("Error while reading Json file", e);
        }
    }

    public SafetyNetData getSafetyNetData() {
        return data;
    }
}