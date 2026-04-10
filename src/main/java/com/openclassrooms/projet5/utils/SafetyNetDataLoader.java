package com.openclassrooms.projet5.utils;
import com.openclassrooms.projet5.model.SafetyNetData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.InputStream;

@Component
public class SafetyNetDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(SafetyNetDataLoader.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Read a JSON and return its value into a SafetyNetData object
    public SafetyNetData getSafetyNetData() {
        try (InputStream inputStream = getClass().getResourceAsStream("/data.json");) {
            return objectMapper.readValue(inputStream, SafetyNetData.class);
        } catch (Exception e) {
            logger.error("Error while reading Json file", e);
            return null;
        }
    }

    // Update the JSON from objects through an ObjectMapper
    public void saveSafetyData(SafetyNetData data) {
        try {
            objectMapper.writeValue(new File(getClass().getResource("/data.json").getFile()), data);
        } catch (Exception e) {
            logger.error("Error while overwriting the JSON file", e);
        }
    }
}
