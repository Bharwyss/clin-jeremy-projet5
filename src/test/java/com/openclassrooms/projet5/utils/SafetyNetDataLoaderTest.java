package com.openclassrooms.projet5.utils;

import com.openclassrooms.projet5.model.SafetyNetData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SafetyNetDataLoaderTest {

    private SafetyNetDataLoader dataLoader;

    @BeforeEach
    void setUp() {
        dataLoader = new SafetyNetDataLoader();
    }

    @Test
    @DisplayName("getSafetyNetData retourne null tant que loadData n'a pas été appelée")
    void getSafetyNetData_shouldReturnNullBeforeLoad() {
        // WHEN / THEN
        assertNull(dataLoader.getSafetyNetData());
    }

    @Test
    @DisplayName("loadData désérialise le fichier data.json du classpath")
    void loadData_shouldLoadJsonFile() {
        // WHEN
        dataLoader.loadData();

        // THEN
        SafetyNetData data = dataLoader.getSafetyNetData();
        assertNotNull(data);
        assertNotNull(data.getPersons());
        assertNotNull(data.getFirestations());
        assertNotNull(data.getMedicalrecords());
    }

    @Test
    @DisplayName("loadData alimente les trois listes du fichier de données")
    void loadData_shouldPopulateAllThreeLists() {
        // WHEN
        dataLoader.loadData();

        // THEN
        SafetyNetData data = dataLoader.getSafetyNetData();
        assertFalse(data.getPersons().isEmpty());
        assertFalse(data.getFirestations().isEmpty());
        assertFalse(data.getMedicalrecords().isEmpty());
    }

    @Test
    @DisplayName("loadData mappe correctement les champs d'une personne")
    void loadData_shouldMapPersonFields() {
        // WHEN
        dataLoader.loadData();

        // THEN
        SafetyNetData data = dataLoader.getSafetyNetData();
        assertTrue(data.getPersons().stream().anyMatch(p ->
                "John".equals(p.getFirstName())
                        && "Boyd".equals(p.getLastName())
                        && "1509 Culver St".equals(p.getAddress())
                        && "Culver".equals(p.getCity())
                        && "97451".equals(p.getZip())
                        && p.getPhone() != null
                        && p.getEmail() != null));
    }

    @Test
    @DisplayName("loadData mappe correctement les champs d'une caserne")
    void loadData_shouldMapFireStationFields() {
        // WHEN
        dataLoader.loadData();

        // THEN
        SafetyNetData data = dataLoader.getSafetyNetData();
        assertTrue(data.getFirestations().stream().anyMatch(f ->
                "1509 Culver St".equals(f.getAddress()) && "3".equals(f.getStation())));
    }

    @Test
    @DisplayName("loadData mappe correctement les listes d'un dossier médical")
    void loadData_shouldMapMedicalRecordLists() {
        // WHEN
        dataLoader.loadData();

        // THEN
        SafetyNetData data = dataLoader.getSafetyNetData();
        assertTrue(data.getMedicalrecords().stream().anyMatch(r ->
                "John".equals(r.getFirstName())
                        && "Boyd".equals(r.getLastName())
                        && r.getBirthdate() != null
                        && r.getMedications() != null
                        && !r.getMedications().isEmpty()
                        && r.getAllergies() != null));
    }

    @Test
    @DisplayName("getSafetyNetData retourne toujours la même instance après chargement")
    void getSafetyNetData_shouldReturnSameInstance() {
        // GIVEN
        dataLoader.loadData();

        // WHEN
        SafetyNetData first = dataLoader.getSafetyNetData();
        SafetyNetData second = dataLoader.getSafetyNetData();

        // THEN - garantit que les modifications en mémoire sont bien partagées
        assertTrue(first == second);
    }
}