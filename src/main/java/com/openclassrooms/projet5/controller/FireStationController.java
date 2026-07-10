package com.openclassrooms.projet5.controller;

import com.openclassrooms.projet5.model.FireStation;
import com.openclassrooms.projet5.service.FireStationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/firestation")
public class FireStationController {
    private final FireStationService fireStationService;

    public FireStationController(FireStationService fireStationService) {
        this.fireStationService = fireStationService;
    }

    @PostMapping
    public ResponseEntity<Void> addFireStation(@RequestBody FireStation fireStation) {
        fireStationService.addFireStation(fireStation);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> updateFireStation(@RequestBody FireStation fireStation) {
        fireStationService.updateFireStation(fireStation);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteFireStation(@RequestBody FireStation fireStation) {
        fireStationService.deleteFireStation(fireStation);
        return ResponseEntity.ok().build();
    }

}
