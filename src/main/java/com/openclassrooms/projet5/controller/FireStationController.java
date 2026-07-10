package com.openclassrooms.projet5.controller;

import com.openclassrooms.projet5.model.FireStation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.openclassrooms.projet5.service.FireStationService;

@RestController
@RequestMapping("/firestation")
public class FireStationController {
    private static final Logger logger = LoggerFactory.getLogger(FireStationController.class);
    private final FireStationService fireStationService;

    public FireStationController(FireStationService fireStationService) {
        this.fireStationService = fireStationService;
    }

    @PostMapping
    public ResponseEntity<Void> addFireStation (@RequestBody FireStation fireStation) {
        fireStationService.addFireStation(fireStation);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> updateFireStation (@RequestBody FireStation fireStation) {
        fireStationService.updateFireStation(fireStation);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteFireStation (@RequestBody FireStation fireStation) {
        fireStationService.deleteFireStation(fireStation);
        return ResponseEntity.ok().build();
    }

}
