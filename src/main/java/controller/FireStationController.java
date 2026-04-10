package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import service.FireStationService;

@RestController
public class FireStationController {
    private static final Logger logger = LoggerFactory.getLogger(FireStationController.class);
    private final FireStationService service;

    public FireStationController(FireStationService service) {
        this.service = service;
    }
}
