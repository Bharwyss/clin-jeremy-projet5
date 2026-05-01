package com.openclassrooms.projet5.service;

import com.openclassrooms.projet5.model.MedicalRecord;
import com.openclassrooms.projet5.model.Person;
import com.openclassrooms.projet5.model.SafetyNetData;
import com.openclassrooms.projet5.utils.SafetyNetDataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MedicalRecordService {
    private static final Logger logger = LoggerFactory.getLogger(MedicalRecordService.class);
    private final SafetyNetDataLoader dataLoader;

    public MedicalRecordService(SafetyNetDataLoader dataLoader) {this.dataLoader = dataLoader;}

    public void addMedicalRecord(MedicalRecord medicalRecord) {
        logger.info("Adding a new medical record: {}", medicalRecord);
        SafetyNetData data = dataLoader.getSafetyNetData();
        data.getMedicalrecords().add(medicalRecord);
        dataLoader.saveSafetyData(data);
    }

    public void updateMedicalRecord(MedicalRecord medicalRecord) {
        logger.info("Updating a medical record : {}", medicalRecord);
        SafetyNetData data = dataLoader.getSafetyNetData();
        data.getMedicalrecords().stream()
                .filter(recordToUpdate -> recordToUpdate.getFirstName().equals(medicalRecord.getFirstName())
                        && recordToUpdate.getLastName().equals(medicalRecord.getLastName()))
                .findFirst()
                .ifPresent(recordToUpdate -> {
                    recordToUpdate.setBirthdate(medicalRecord.getBirthdate());
                    recordToUpdate.setMedications(medicalRecord.getMedications());
                    recordToUpdate.setAllergies(medicalRecord.getAllergies());
                });
        dataLoader.saveSafetyData(data);
    }

    public void deleteMedicalRecord(MedicalRecord medicalRecord) {
        logger.info("Deleting a medical record : {}", medicalRecord);
        SafetyNetData data = dataLoader.getSafetyNetData();
        data.getMedicalrecords().removeIf(recordToDelete -> (recordToDelete.getFirstName().equals(medicalRecord.getFirstName()) &&
                recordToDelete.getLastName().equals(medicalRecord.getLastName())));
        dataLoader.saveSafetyData(data);
    }

}
