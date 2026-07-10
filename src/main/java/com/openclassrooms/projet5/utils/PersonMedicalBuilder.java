package com.openclassrooms.projet5.utils;

import com.openclassrooms.projet5.dto.PersonFromFireDto;
import com.openclassrooms.projet5.model.MedicalRecord;
import com.openclassrooms.projet5.model.Person;
import com.openclassrooms.projet5.model.SafetyNetData;

import static com.openclassrooms.projet5.utils.AgeCalculator.calculateAge;

public class PersonMedicalBuilder {

    public static PersonFromFireDto buildPersonMedicalDto(SafetyNetData data, Person person) {
        for (MedicalRecord record : data.getMedicalrecords()) {
            if (record.getFirstName().equals(person.getFirstName())
                    && record.getLastName().equals(person.getLastName())) {
                int age = calculateAge(record.getBirthdate());
                return new PersonFromFireDto(person.getLastName(), person.getPhone(),
                        age, record.getMedications(), record.getAllergies());
            }
        }
        return null;
    }
}
