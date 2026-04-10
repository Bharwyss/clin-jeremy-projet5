package utils;
import model.SafetyNetData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;


public class SafetyNetDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(SafetyNetDataLoader.class);

    public SafetyNetData getSafetyNetData() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = getClass().getResourceAsStream("/data.json");
            return objectMapper.readValue(inputStream, SafetyNetData.class);
        } catch (Exception e) {
            logger.error("Error while reading Json file");
            return null;
        }
    }
}
