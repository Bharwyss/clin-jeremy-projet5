package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import service.MedicalRecordService;

@RestController
public class MedicalRecordController {
    private static final Logger logger = LoggerFactory.getLogger(MedicalRecordController.class);
    private final MedicalRecordService service;

    public MedicalRecordController (MedicalRecordService service) {
        this.service = service;
    }
}
