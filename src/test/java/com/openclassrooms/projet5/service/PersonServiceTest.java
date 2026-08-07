package com.openclassrooms.projet5.service;

import com.openclassrooms.projet5.dto.ChildAlertDto;
import com.openclassrooms.projet5.dto.ChildDto;
import com.openclassrooms.projet5.dto.HouseholdMemberDto;
import com.openclassrooms.projet5.dto.PersonLastName;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private SafetyNetDataLoader dataLoader;

    @InjectMocks
    private PersonService personService;

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
                // partage volontairement l'e-mail de John : vérifie le .distinct()
                new Person("Roger", "Boyd", "1509 Culver St", "Culver", "97451",
                        "841-874-6512", "jaboyd@email.com"),
                new Person("Lily", "Cooper", "489 Manchester St", "Culver", "97451",
                        "841-874-9845", "lily@email.com"),
                new Person("Eric", "Cadigan", "951 LoneTree Rd", "Paris", "75000",
                        "841-874-7458", "gramps@email.com"),
                // couple servant à tester la frontière exacte des 18 ans
                new Person("Chloe", "Martin", "7 Boundary Rd", "Culver", "97451",
                        "841-874-1818", "chloe@email.com"),
                new Person("Hugo", "Martin", "7 Boundary Rd", "Culver", "97451",
                        "841-874-1919", "hugo@email.com")
        ));

        List<MedicalRecord> medicalrecords = new ArrayList<>(List.of(
                new MedicalRecord("John", "Boyd", birthdateForAge(40),
                        List.of("aznol:350mg"), List.of("nillacilan")),
                new MedicalRecord("Tenley", "Boyd", birthdateForAge(12),
                        List.of(), List.of("peanut")),
                new MedicalRecord("Roger", "Boyd", birthdateForAge(8),
                        List.of(), List.of()),
                new MedicalRecord("Lily", "Cooper", birthdateForAge(32),
                        List.of("tetracyclaz:650mg"), List.of()),
                new MedicalRecord("Eric", "Cadigan", birthdateForAge(80),
                        List.of("tradoxidine:400mg"), List.of()),
                new MedicalRecord("Chloe", "Martin", birthdateForAge(18),
                        List.of(), List.of()),
                new MedicalRecord("Hugo", "Martin", birthdateForAge(19),
                        List.of(), List.of())
        ));

        data = new SafetyNetData(persons, new ArrayList<FireStation>(), medicalrecords);
        when(dataLoader.getSafetyNetData()).thenReturn(data);
    }

    // ----------------------------------------------------------------
    // addPerson
    // ----------------------------------------------------------------

    @Test
    @DisplayName("addPerson ajoute la personne à la liste")
    void addPerson_shouldAddPersonToList() {
        // GIVEN
        Person newPerson = new Person("Bertrand", "Dupont", "12 Rue des Lilas", "Culver",
                "97451", "841-874-1234", "bertrand.dupont@email.com");

        // WHEN
        personService.addPerson(newPerson);

        // THEN
        assertEquals(8, data.getPersons().size());
        assertTrue(data.getPersons().contains(newPerson));
    }

    // ----------------------------------------------------------------
    // updatePerson
    // ----------------------------------------------------------------

    @Test
    @DisplayName("updatePerson modifie les champs modifiables d'une personne existante")
    void updatePerson_shouldUpdateExistingPerson() {
        // GIVEN
        Person update = new Person("John", "Boyd", "99 Nouvelle Adresse", "Paris",
                "75000", "841-874-9999", "nouveau.mail@email.com");

        // WHEN
        personService.updatePerson(update);

        // THEN
        Person updated = data.getPersons().get(0);
        assertEquals("99 Nouvelle Adresse", updated.getAddress());
        assertEquals("Paris", updated.getCity());
        assertEquals("75000", updated.getZip());
        assertEquals("841-874-9999", updated.getPhone());
        assertEquals("nouveau.mail@email.com", updated.getEmail());
        assertEquals(7, data.getPersons().size());
    }

    @Test
    @DisplayName("updatePerson conserve le prénom et le nom de la personne")
    void updatePerson_shouldKeepIdentity() {
        // GIVEN
        Person update = new Person("John", "Boyd", "99 Nouvelle Adresse", "Paris",
                "75000", "841-874-9999", "nouveau.mail@email.com");

        // WHEN
        personService.updatePerson(update);

        // THEN
        Person updated = data.getPersons().get(0);
        assertEquals("John", updated.getFirstName());
        assertEquals("Boyd", updated.getLastName());
    }

    @Test
    @DisplayName("updatePerson ne modifie rien si la personne est inconnue")
    void updatePerson_shouldDoNothingWhenPersonUnknown() {
        // GIVEN
        Person update = new Person("Inconnu", "Personne", "Nulle Part", "Nulleville",
                "00000", "000-000-0000", "inconnu@email.com");

        // WHEN
        personService.updatePerson(update);

        // THEN
        assertEquals(7, data.getPersons().size());
        assertEquals("1509 Culver St", data.getPersons().get(0).getAddress());
    }

    @Test
    @DisplayName("updatePerson ne modifie pas un homonyme portant le même nom de famille")
    void updatePerson_shouldNotUpdateSameLastNameDifferentFirstName() {
        // GIVEN
        Person update = new Person("Tenley", "Boyd", "42 Rue Modifiee", "Culver",
                "97451", "841-000-0000", "tenley.modifie@email.com");

        // WHEN
        personService.updatePerson(update);

        // THEN
        assertEquals("42 Rue Modifiee", data.getPersons().get(1).getAddress());
        assertEquals("1509 Culver St", data.getPersons().get(0).getAddress());
        assertEquals("jaboyd@email.com", data.getPersons().get(0).getEmail());
    }

    // ----------------------------------------------------------------
    // deletePerson
    // ----------------------------------------------------------------

    @Test
    @DisplayName("deletePerson supprime la personne identifiée par prénom et nom")
    void deletePerson_shouldRemovePerson() {
        // GIVEN
        Person toDelete = new Person("John", "Boyd", null, null, null, null, null);

        // WHEN
        personService.deletePerson(toDelete);

        // THEN
        assertEquals(6, data.getPersons().size());
        assertFalse(data.getPersons().stream()
                .anyMatch(p -> p.getFirstName().equals("John") && p.getLastName().equals("Boyd")));
    }

    @Test
    @DisplayName("deletePerson ne supprime pas les homonymes portant le même nom de famille")
    void deletePerson_shouldOnlyRemoveMatchingPerson() {
        // GIVEN
        Person toDelete = new Person("Tenley", "Boyd", null, null, null, null, null);

        // WHEN
        personService.deletePerson(toDelete);

        // THEN
        assertEquals(6, data.getPersons().size());
        assertTrue(data.getPersons().stream()
                .anyMatch(p -> p.getFirstName().equals("John") && p.getLastName().equals("Boyd")));
        assertTrue(data.getPersons().stream()
                .anyMatch(p -> p.getFirstName().equals("Roger") && p.getLastName().equals("Boyd")));
    }

    @Test
    @DisplayName("deletePerson ne supprime rien si la personne est inconnue")
    void deletePerson_shouldDoNothingWhenPersonUnknown() {
        // GIVEN
        Person toDelete = new Person("Inconnu", "Personne", null, null, null, null, null);

        // WHEN
        personService.deletePerson(toDelete);

        // THEN
        assertEquals(7, data.getPersons().size());
    }

    // ----------------------------------------------------------------
    // getChildAlert
    // ----------------------------------------------------------------

    @Test
    @DisplayName("getChildAlert retourne les enfants et les autres membres du foyer")
    void getChildAlert_shouldReturnChildrenAndHouseholdMembers() {
        // WHEN
        ChildAlertDto result = personService.getChildAlert("1509 Culver St");

        // THEN
        assertEquals(2, result.children().size());
        assertTrue(result.children().contains(new ChildDto("Tenley", "Boyd", 12)));
        assertTrue(result.children().contains(new ChildDto("Roger", "Boyd", 8)));

        assertEquals(1, result.family().size());
        assertEquals(new HouseholdMemberDto("John", "Boyd"), result.family().get(0));
    }

    @Test
    @DisplayName("getChildAlert considère une personne de 18 ans comme un enfant")
    void getChildAlert_shouldTreatEighteenYearsOldAsChild() {
        // WHEN
        ChildAlertDto result = personService.getChildAlert("7 Boundary Rd");

        // THEN
        assertEquals(1, result.children().size());
        assertEquals(new ChildDto("Chloe", "Martin", 18), result.children().get(0));

        assertEquals(1, result.family().size());
        assertEquals(new HouseholdMemberDto("Hugo", "Martin"), result.family().get(0));
    }

    @Test
    @DisplayName("getChildAlert retourne un DTO vide quand le foyer ne compte aucun enfant")
    void getChildAlert_shouldReturnEmptyDtoWhenNoChild() {
        // WHEN
        ChildAlertDto result = personService.getChildAlert("489 Manchester St");

        // THEN
        assertTrue(result.children().isEmpty());
        assertTrue(result.family().isEmpty());
    }

    @Test
    @DisplayName("getChildAlert retourne un DTO vide pour une adresse inconnue")
    void getChildAlert_shouldReturnEmptyDtoWhenAddressUnknown() {
        // WHEN
        ChildAlertDto result = personService.getChildAlert("Adresse inexistante");

        // THEN
        assertTrue(result.children().isEmpty());
        assertTrue(result.family().isEmpty());
    }

    @Test
    @DisplayName("getChildAlert ignore les habitants sans dossier médical")
    void getChildAlert_shouldSkipPersonWithoutMedicalRecord() {
        // GIVEN
        data.getPersons().add(new Person("Sans", "Dossier", "1509 Culver St", "Culver",
                "97451", "841-000-0000", "sans.dossier@email.com"));

        // WHEN
        ChildAlertDto result = personService.getChildAlert("1509 Culver St");

        // THEN
        assertEquals(2, result.children().size());
        assertEquals(1, result.family().size());
    }

    // ----------------------------------------------------------------
    // getPersonLastNames
    // ----------------------------------------------------------------

    @Test
    @DisplayName("getPersonLastNames retourne toutes les personnes portant le nom demandé")
    void getPersonLastNames_shouldReturnAllPersonsWithSameLastName() {
        // WHEN
        List<PersonLastName> result = personService.getPersonLastNames("Boyd");

        // THEN
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(p -> p.lastName().equals("Boyd")));
    }

    @Test
    @DisplayName("getPersonLastNames retourne l'adresse, l'âge, l'e-mail et les antécédents médicaux")
    void getPersonLastNames_shouldReturnFullDetails() {
        // WHEN
        List<PersonLastName> result = personService.getPersonLastNames("Boyd");

        // THEN
        PersonLastName john = result.get(0);
        assertEquals("Boyd", john.lastName());
        assertEquals("1509 Culver St", john.address());
        assertEquals(40, john.age());
        assertEquals("jaboyd@email.com", john.mail());
        assertEquals(List.of("aznol:350mg"), john.medications());
        assertEquals(List.of("nillacilan"), john.allergies());
    }

    @Test
    @DisplayName("getPersonLastNames retourne une liste vide pour un nom inconnu")
    void getPersonLastNames_shouldReturnEmptyListWhenLastNameUnknown() {
        // WHEN
        List<PersonLastName> result = personService.getPersonLastNames("Inconnu");

        // THEN
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getPersonLastNames ignore les personnes sans dossier médical")
    void getPersonLastNames_shouldSkipPersonWithoutMedicalRecord() {
        // GIVEN
        data.getPersons().add(new Person("Sans", "Boyd", "1509 Culver St", "Culver",
                "97451", "841-000-0000", "sans.dossier@email.com"));

        // WHEN
        List<PersonLastName> result = personService.getPersonLastNames("Boyd");

        // THEN
        assertEquals(3, result.size());
    }

    // ----------------------------------------------------------------
    // getCommunityEmails
    // ----------------------------------------------------------------

    @Test
    @DisplayName("getCommunityEmails retourne les e-mails des habitants de la ville sans doublon")
    void getCommunityEmails_shouldReturnDistinctEmailsOfCity() {
        // WHEN
        List<String> result = personService.getCommunityEmails("Culver");

        // THEN
        // 6 habitants à Culver, mais John et Roger partagent le même e-mail
        assertEquals(5, result.size());
        assertTrue(result.contains("jaboyd@email.com"));
        assertTrue(result.contains("tenz@email.com"));
        assertTrue(result.contains("lily@email.com"));
        assertTrue(result.contains("chloe@email.com"));
        assertTrue(result.contains("hugo@email.com"));
    }

    @Test
    @DisplayName("getCommunityEmails n'inclut pas les habitants d'une autre ville")
    void getCommunityEmails_shouldExcludeOtherCities() {
        // WHEN
        List<String> result = personService.getCommunityEmails("Culver");

        // THEN
        assertFalse(result.contains("gramps@email.com"));
    }

    @Test
    @DisplayName("getCommunityEmails retourne une liste vide pour une ville inconnue")
    void getCommunityEmails_shouldReturnEmptyListWhenCityUnknown() {
        // WHEN
        List<String> result = personService.getCommunityEmails("Ville inexistante");

        // THEN
        assertTrue(result.isEmpty());
    }
}