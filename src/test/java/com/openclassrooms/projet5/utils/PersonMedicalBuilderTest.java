package com.openclassrooms.projet5.utils;

import com.openclassrooms.projet5.dto.PersonFromFireDto;
import com.openclassrooms.projet5.model.FireStation;
import com.openclassrooms.projet5.model.MedicalRecord;
import com.openclassrooms.projet5.model.Person;
import com.openclassrooms.projet5.model.SafetyNetData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonMedicalBuilderTest {

    private SafetyNetData data;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    /**
     * Génère une date de naissance produisant exactement l'âge demandé,
     * quelle que soit la date d'exécution du test.
     */
    private static String birthdateForAge(int age) {
        return LocalDate.now().minusYears(age).minusDays(1).format(FORMATTER);
    }

    @BeforeEach
    void setUp() {
        List<Person> persons = new ArrayList<>(List.of(
                new Person("John", "Boyd", "1509 Culver St", "Culver", "97451",
                        "841-874-6512", "jaboyd@email.com"),
                new Person("Tenley", "Boyd", "1509 Culver St", "Culver", "97451",
                        "841-874-6513", "tenz@email.com"),
                new Person("Sans", "Dossier", "12 Rue des Lilas", "Culver", "97451",
                        "841-000-0000", "sans.dossier@email.com")
        ));

        List<MedicalRecord> medicalrecords = new ArrayList<>(List.of(
                new MedicalRecord("John", "Boyd", birthdateForAge(40),
                        List.of("aznol:350mg", "hydrapermazol:100mg"), List.of("nillacilan")),
                new MedicalRecord("Tenley", "Boyd", birthdateForAge(12),
                        List.of(), List.of("peanut"))
        ));

        data = new SafetyNetData(persons, new ArrayList<FireStation>(), medicalrecords);
    }

    @Test
    @DisplayName("buildPersonMedicalDto construit le DTO avec les informations médicales")
    void buildPersonMedicalDto_shouldBuildDtoWithMedicalData() {
        // GIVEN
        Person john = data.getPersons().get(0);

        // WHEN
        PersonFromFireDto result = PersonMedicalBuilder.buildPersonMedicalDto(data, john);

        // THEN
        assertNotNull(result);
        assertEquals("Boyd", result.lastName());
        assertEquals("841-874-6512", result.phone());
        assertEquals(40, result.age());
        assertEquals(List.of("aznol:350mg", "hydrapermazol:100mg"), result.medications());
        assertEquals(List.of("nillacilan"), result.allergies());
    }

    @Test
    @DisplayName("buildPersonMedicalDto conserve les listes vides de médicaments")
    void buildPersonMedicalDto_shouldKeepEmptyMedicationList() {
        // GIVEN
        Person tenley = data.getPersons().get(1);

        // WHEN
        PersonFromFireDto result = PersonMedicalBuilder.buildPersonMedicalDto(data, tenley);

        // THEN
        assertNotNull(result);
        assertTrue(result.medications().isEmpty());
        assertEquals(List.of("peanut"), result.allergies());
        assertEquals(12, result.age());
    }

    @Test
    @DisplayName("buildPersonMedicalDto retourne null quand la personne n'a pas de dossier médical")
    void buildPersonMedicalDto_shouldReturnNullWhenNoMedicalRecord() {
        // GIVEN
        Person sansDossier = data.getPersons().get(2);

        // WHEN
        PersonFromFireDto result = PersonMedicalBuilder.buildPersonMedicalDto(data, sansDossier);

        // THEN
        assertNull(result);
    }

    @Test
    @DisplayName("buildPersonMedicalDto retourne null quand aucun dossier n'est enregistré")
    void buildPersonMedicalDto_shouldReturnNullWhenNoRecordsAtAll() {
        // GIVEN
        SafetyNetData emptyRecords = new SafetyNetData(
                data.getPersons(), new ArrayList<FireStation>(), new ArrayList<MedicalRecord>());
        Person john = data.getPersons().get(0);

        // WHEN
        PersonFromFireDto result = PersonMedicalBuilder.buildPersonMedicalDto(emptyRecords, john);

        // THEN
        assertNull(result);
    }

    @Test
    @DisplayName("buildPersonMedicalDto ne confond pas deux homonymes du même nom de famille")
    void buildPersonMedicalDto_shouldMatchOnFirstAndLastName() {
        // GIVEN - Tenley porte le même nom que John mais un prénom différent
        Person tenley = data.getPersons().get(1);

        // WHEN
        PersonFromFireDto result = PersonMedicalBuilder.buildPersonMedicalDto(data, tenley);

        // THEN - on doit récupérer le dossier de Tenley, pas celui de John
        assertNotNull(result);
        assertEquals(12, result.age());
        assertTrue(result.medications().isEmpty());
    }
}