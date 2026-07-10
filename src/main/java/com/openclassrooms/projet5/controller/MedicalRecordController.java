package com.openclassrooms.projet5.controller;

import com.openclassrooms.projet5.model.MedicalRecord;
import com.openclassrooms.projet5.service.MedicalRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/medicalrecord")
public class MedicalRecordController {
    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController (MedicalRecordService service) {
        this.medicalRecordService = service;
    }

    @PostMapping
    public ResponseEntity<Void> addMedicalRecord (@RequestBody MedicalRecord medicalRecord) {
        medicalRecordService.addMedicalRecord(medicalRecord);
        return ResponseEntity.ok().build();
    }

    @PutMapping

    public ResponseEntity<Void> updateMedicalRecord (@RequestBody MedicalRecord medicalRecord) {
        medicalRecordService.updateMedicalRecord(medicalRecord);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMedicalRecord (@RequestBody MedicalRecord medicalRecord) {
        medicalRecordService.deleteMedicalRecord(medicalRecord);
        return ResponseEntity.ok().build();
    }
}
