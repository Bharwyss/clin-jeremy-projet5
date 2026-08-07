package com.openclassrooms.projet5.service;

import com.openclassrooms.projet5.dto.FireDto;
import com.openclassrooms.projet5.dto.FireStationCoverageDto;
import com.openclassrooms.projet5.dto.FloodHousehold;
import com.openclassrooms.projet5.dto.PersonFromFireDto;
import com.openclassrooms.projet5.dto.PersonFromStationDto;
import com.openclassrooms.projet5.model.FireStation;
import com.openclassrooms.projet5.model.MedicalRecord;
import com.openclassrooms.projet5.model.Person;
import com.openclassrooms.projet5.model.SafetyNetData;
import com.openclassrooms.projet5.utils.SafetyNetDataLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FireStationServiceTest {

    @Mock
    private SafetyNetDataLoader dataLoader;

    @InjectMocks
    private FireStationService fireStationService;

    private SafetyNetData data;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    /**
     * Génère une date de naissance produisant exactement l'âge demandé,
     * quelle que soit la date d'exécution du test.
     * Indispensable car AgeCalculator s'appuie sur LocalDate.now().
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
                new Person("Jonanathan", "Marrack", "29 15th St", "Culver", "97451",
                        "841-874-6514", "drk@email.com")
        ));

        List<FireStation> firestations = new ArrayList<>(List.of(
                new FireStation("1509 Culver St", "3"),
                new FireStation("29 15th St", "2")
        ));

        List<MedicalRecord> medicalrecords = new ArrayList<>(List.of(
                new MedicalRecord("John", "Boyd", birthdateForAge(40),
                        List.of("aznol:350mg"), List.of("nillacilan")),
                new MedicalRecord("Tenley", "Boyd", birthdateForAge(12),
                        List.of(), List.of("peanut")),
                new MedicalRecord("Jonanathan", "Marrack", birthdateForAge(35),
                        List.of(), List.of())
        ));

        data = new SafetyNetData(persons, firestations, medicalrecords);
        when(dataLoader.getSafetyNetData()).thenReturn(data);
    }

    // ----------------------------------------------------------------
    // addFireStation
    // ----------------------------------------------------------------

    @Test
    @DisplayName("addFireStation ajoute le mapping à la liste")
    void addFireStation_shouldAddMappingToList() {
        // GIVEN
        FireStation newStation = new FireStation("12 Rue des Lilas", "4");

        // WHEN
        fireStationService.addFireStation(newStation);

        // THEN
        assertEquals(3, data.getFirestations().size());
        assertTrue(data.getFirestations().contains(newStation));
    }

    // ----------------------------------------------------------------
    // updateFireStation
    // ----------------------------------------------------------------

    @Test
    @DisplayName("updateFireStation modifie le numéro de station d'une adresse existante")
    void updateFireStation_shouldUpdateStationNumber() {
        // GIVEN
        FireStation update = new FireStation("1509 Culver St", "9");

        // WHEN
        fireStationService.updateFireStation(update);

        // THEN
        assertEquals("9", data.getFirestations().get(0).getStation());
        assertEquals(2, data.getFirestations().size());
    }

    @Test
    @DisplayName("updateFireStation ne modifie rien si l'adresse est inconnue")
    void updateFireStation_shouldDoNothingWhenAddressUnknown() {
        // GIVEN
        FireStation update = new FireStation("Adresse inexistante", "9");

        // WHEN
        fireStationService.updateFireStation(update);

        // THEN
        assertEquals("3", data.getFirestations().get(0).getStation());
        assertEquals("2", data.getFirestations().get(1).getStation());
    }

    // ----------------------------------------------------------------
    // deleteFireStation
    // ----------------------------------------------------------------

    @Test
    @DisplayName("deleteFireStation supprime le mapping quand adresse et station correspondent")
    void deleteFireStation_shouldRemoveMapping() {
        // GIVEN
        FireStation toDelete = new FireStation("1509 Culver St", "3");

        // WHEN
        fireStationService.deleteFireStation(toDelete);

        // THEN
        assertEquals(1, data.getFirestations().size());
        assertEquals("29 15th St", data.getFirestations().get(0).getAddress());
    }

    @Test
    @DisplayName("deleteFireStation ne supprime rien si le numéro de station ne correspond pas")
    void deleteFireStation_shouldNotRemoveWhenStationDiffers() {
        // GIVEN
        FireStation toDelete = new FireStation("1509 Culver St", "7");

        // WHEN
        fireStationService.deleteFireStation(toDelete);

        // THEN
        assertEquals(2, data.getFirestations().size());
    }

    // ----------------------------------------------------------------
    // getStationCoverage
    // ----------------------------------------------------------------

    @Test
    @DisplayName("getStationCoverage retourne les habitants couverts avec le décompte adultes/enfants")
    void getStationCoverage_shouldReturnPersonsWithCounts() {
        // WHEN
        FireStationCoverageDto result = fireStationService.getStationCoverage("3");

        // THEN
        assertEquals(2, result.persons().size());
        assertEquals(1, result.adultCount());
        assertEquals(1, result.childCount());

        PersonFromStationDto first = result.persons().get(0);
        assertEquals("John", first.firstName());
        assertEquals("Boyd", first.lastName());
        assertEquals("1509 Culver St", first.address());
        assertEquals("841-874-6512", first.phone());
    }

    @Test
    @DisplayName("getStationCoverage retourne une couverture vide pour une station inconnue")
    void getStationCoverage_shouldReturnEmptyWhenStationUnknown() {
        // WHEN
        FireStationCoverageDto result = fireStationService.getStationCoverage("99");

        // THEN
        assertTrue(result.persons().isEmpty());
        assertEquals(0, result.adultCount());
        assertEquals(0, result.childCount());
    }

    @Test
    @DisplayName("getStationCoverage n'incrémente aucun compteur pour une personne sans dossier médical")
    void getStationCoverage_shouldNotCountPersonWithoutMedicalRecord() {
        // GIVEN
        data.getPersons().add(new Person("Sans", "Dossier", "1509 Culver St", "Culver",
                "97451", "841-000-0000", "sans.dossier@email.com"));

        // WHEN
        FireStationCoverageDto result = fireStationService.getStationCoverage("3");

        // THEN
        assertEquals(3, result.persons().size());
        assertEquals(1, result.adultCount());
        assertEquals(1, result.childCount());
    }

    // ----------------------------------------------------------------
    // getPhoneAlert
    // ----------------------------------------------------------------

    @Test
    @DisplayName("getPhoneAlert retourne les téléphones des résidents de la station")
    void getPhoneAlert_shouldReturnPhonesOfCoveredResidents() {
        // WHEN
        List<String> result = fireStationService.getPhoneAlert("3");

        // THEN
        assertEquals(2, result.size());
        assertTrue(result.contains("841-874-6512"));
        assertTrue(result.contains("841-874-6513"));
    }

    @Test
    @DisplayName("getPhoneAlert retourne une liste vide pour une station inconnue")
    void getPhoneAlert_shouldReturnEmptyListWhenStationUnknown() {
        // WHEN
        List<String> result = fireStationService.getPhoneAlert("99");

        // THEN
        assertTrue(result.isEmpty());
    }

    // ----------------------------------------------------------------
    // getFireAddress
    // ----------------------------------------------------------------

    @Test
    @DisplayName("getFireAddress retourne les habitants et le numéro de caserne de l'adresse")
    void getFireAddress_shouldReturnResidentsAndStationNumber() {
        // WHEN
        FireDto result = fireStationService.getFireAddress("1509 Culver St");

        // THEN
        assertEquals("3", result.numberStation());
        assertEquals(2, result.personFromFireDtoList().size());

        PersonFromFireDto john = result.personFromFireDtoList().get(0);
        assertEquals("Boyd", john.lastName());
        assertEquals("841-874-6512", john.phone());
        assertEquals(40, john.age());
        assertEquals(List.of("aznol:350mg"), john.medications());
        assertEquals(List.of("nillacilan"), john.allergies());
    }

    @Test
    @DisplayName("getFireAddress retourne une liste vide et une station nulle pour une adresse inconnue")
    void getFireAddress_shouldReturnEmptyWhenAddressUnknown() {
        // WHEN
        FireDto result = fireStationService.getFireAddress("Adresse inexistante");

        // THEN
        assertTrue(result.personFromFireDtoList().isEmpty());
        assertNull(result.numberStation());
    }

    @Test
    @DisplayName("getFireAddress ignore les habitants sans dossier médical")
    void getFireAddress_shouldSkipPersonWithoutMedicalRecord() {
        // GIVEN
        data.getPersons().add(new Person("Sans", "Dossier", "1509 Culver St", "Culver",
                "97451", "841-000-0000", "sans.dossier@email.com"));

        // WHEN
        FireDto result = fireStationService.getFireAddress("1509 Culver St");

        // THEN
        assertEquals(2, result.personFromFireDtoList().size());
    }

    // ----------------------------------------------------------------
    // getFloodHousehold
    // ----------------------------------------------------------------

    @Test
    @DisplayName("getFloodHousehold regroupe les habitants par adresse pour plusieurs stations")
    void getFloodHousehold_shouldGroupResidentsByAddress() {
        // WHEN
        List<FloodHousehold> result = fireStationService.getFloodHousehold(List.of("3", "2"));

        // THEN
        assertEquals(2, result.size());

        FloodHousehold culver = result.get(0);
        assertEquals("1509 Culver St", culver.address());
        assertEquals(2, culver.members().size());

        FloodHousehold fifteenth = result.get(1);
        assertEquals("29 15th St", fifteenth.address());
        assertEquals(1, fifteenth.members().size());
        assertEquals("Marrack", fifteenth.members().get(0).lastName());
        assertEquals(35, fifteenth.members().get(0).age());
    }

    @Test
    @DisplayName("getFloodHousehold ne retourne qu'un foyer pour une station unique")
    void getFloodHousehold_shouldReturnSingleHouseholdForOneStation() {
        // WHEN
        List<FloodHousehold> result = fireStationService.getFloodHousehold(List.of("2"));

        // THEN
        assertEquals(1, result.size());
        assertEquals("29 15th St", result.get(0).address());
    }

    @Test
    @DisplayName("getFloodHousehold retourne une liste vide pour une station inconnue")
    void getFloodHousehold_shouldReturnEmptyListWhenStationUnknown() {
        // WHEN
        List<FloodHousehold> result = fireStationService.getFloodHousehold(List.of("99"));

        // THEN
        assertTrue(result.isEmpty());
    }
}