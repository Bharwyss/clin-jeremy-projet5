package com.openclassrooms.projet5.utils;

import com.openclassrooms.projet5.model.FireStation;
import com.openclassrooms.projet5.model.MedicalRecord;
import com.openclassrooms.projet5.model.Person;
import com.openclassrooms.projet5.model.SafetyNetData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonFinderTest {

    private SafetyNetData data;

    @BeforeEach
    void setUp() {
        List<Person> persons = new ArrayList<>(List.of(
                new Person("John", "Boyd", "1509 Culver St", "Culver", "97451",
                        "841-874-6512", "jaboyd@email.com"),
                new Person("Tenley", "Boyd", "1509 Culver St", "Culver", "97451",
                        "841-874-6513", "tenz@email.com"),
                new Person("Jonanathan", "Marrack", "29 15th St", "Culver", "97451",
                        "841-874-6514", "drk@email.com")
        ));

        data = new SafetyNetData(persons, new ArrayList<FireStation>(), new ArrayList<MedicalRecord>());
    }

    @Test
    @DisplayName("getPersonByAddress retourne tous les habitants de l'adresse")
    void getPersonByAddress_shouldReturnAllResidentsOfAddress() {
        // WHEN
        List<Person> result = PersonFinder.getPersonByAddress(data, "1509 Culver St");

        // THEN
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getFirstName().equals("John")));
        assertTrue(result.stream().anyMatch(p -> p.getFirstName().equals("Tenley")));
    }

    @Test
    @DisplayName("getPersonByAddress retourne un seul habitant quand l'adresse n'en compte qu'un")
    void getPersonByAddress_shouldReturnSingleResident() {
        // WHEN
        List<Person> result = PersonFinder.getPersonByAddress(data, "29 15th St");

        // THEN
        assertEquals(1, result.size());
        assertEquals("Marrack", result.get(0).getLastName());
    }

    @Test
    @DisplayName("getPersonByAddress retourne une liste vide pour une adresse inconnue")
    void getPersonByAddress_shouldReturnEmptyListWhenAddressUnknown() {
        // WHEN
        List<Person> result = PersonFinder.getPersonByAddress(data, "Adresse inexistante");

        // THEN
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getPersonByAddress retourne une liste vide quand aucune personne n'est enregistrée")
    void getPersonByAddress_shouldReturnEmptyListWhenNoPersons() {
        // GIVEN
        SafetyNetData emptyData = new SafetyNetData(
                new ArrayList<Person>(), new ArrayList<FireStation>(), new ArrayList<MedicalRecord>());

        // WHEN
        List<Person> result = PersonFinder.getPersonByAddress(emptyData, "1509 Culver St");

        // THEN
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getPersonByAddress est sensible à la casse")
    void getPersonByAddress_shouldBeCaseSensitive() {
        // WHEN
        List<Person> result = PersonFinder.getPersonByAddress(data, "1509 culver st");

        // THEN
        assertTrue(result.isEmpty());
    }
}