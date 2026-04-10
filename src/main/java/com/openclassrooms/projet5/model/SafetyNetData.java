package com.openclassrooms.projet5.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SafetyNetData {

    private List<Person> persons;
    private List<FireStation> firestations;
    private List<MedicalRecord> medicalrecords;

}
