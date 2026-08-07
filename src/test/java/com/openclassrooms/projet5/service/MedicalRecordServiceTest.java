package com.openclassrooms.projet5.service;

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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock
    private SafetyNetDataLoader dataLoader;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    private SafetyNetData data;

    @BeforeEach
    void setUp() {
        List<MedicalRecord> medicalrecords = new ArrayList<>(List.of(
                new MedicalRecord("John", "Boyd", "03/06/1984",
                        new ArrayList<>(List.of("aznol:350mg")),
                        new ArrayList<>(List.of("nillacilan"))),
                new MedicalRecord("Tenley", "Boyd", "02/18/2012",
                        new ArrayList<>(),
                        new ArrayList<>(List.of("peanut"))),
                new MedicalRecord("Jonanathan", "Marrack", "01/03/1989",
                        new ArrayList<>(),
                        new ArrayList<>())
        ));

        data = new SafetyNetData(new ArrayList<Person>(), new ArrayList<FireStation>(), medicalrecords);
        when(dataLoader.getSafetyNetData()).thenReturn(data);
    }

    // ----------------------------------------------------------------
    // addMedicalRecord
    // ----------------------------------------------------------------

    @Test
    @DisplayName("addMedicalRecord ajoute le dossier à la liste")
    void addMedicalRecord_shouldAddRecordToList() {
        // GIVEN
        MedicalRecord newRecord = new MedicalRecord("Bertrand", "Dupont", "05/14/1990",
                List.of("doliprane:500mg"), List.of("pollen"));

        // WHEN
        medicalRecordService.addMedicalRecord(newRecord);

        // THEN
        assertEquals(4, data.getMedicalrecords().size());
        assertTrue(data.getMedicalrecords().contains(newRecord));
    }

    // ----------------------------------------------------------------
    // updateMedicalRecord
    // ----------------------------------------------------------------

    @Test
    @DisplayName("updateMedicalRecord modifie la date, les médicaments et les allergies d'un dossier existant")
    void updateMedicalRecord_shouldUpdateExistingRecord() {
        // GIVEN
        MedicalRecord update = new MedicalRecord("John", "Boyd", "01/01/1985",
                List.of("ibuprofene:200mg"), List.of("arachide"));

        // WHEN
        medicalRecordService.updateMedicalRecord(update);

        // THEN
        MedicalRecord updated = data.getMedicalrecords().get(0);
        assertEquals("01/01/1985", updated.getBirthdate());
        assertEquals(List.of("ibuprofene:200mg"), updated.getMedications());
        assertEquals(List.of("arachide"), updated.getAllergies());
        assertEquals(3, data.getMedicalrecords().size());
    }

    @Test
    @DisplayName("updateMedicalRecord conserve le prénom et le nom du dossier")
    void updateMedicalRecord_shouldKeepIdentity() {
        // GIVEN
        MedicalRecord update = new MedicalRecord("John", "Boyd", "01/01/1985",
                List.of(), List.of());

        // WHEN
        medicalRecordService.updateMedicalRecord(update);

        // THEN
        MedicalRecord updated = data.getMedicalrecords().get(0);
        assertEquals("John", updated.getFirstName());
        assertEquals("Boyd", updated.getLastName());
    }

    @Test
    @DisplayName("updateMedicalRecord ne modifie rien si la personne est inconnue")
    void updateMedicalRecord_shouldDoNothingWhenPersonUnknown() {
        // GIVEN
        MedicalRecord update = new MedicalRecord("Inconnu", "Personne", "01/01/2000",
                List.of("placebo:1mg"), List.of("gluten"));

        // WHEN
        medicalRecordService.updateMedicalRecord(update);

        // THEN
        assertEquals(3, data.getMedicalrecords().size());
        assertEquals("03/06/1984", data.getMedicalrecords().get(0).getBirthdate());
        assertEquals("02/18/2012", data.getMedicalrecords().get(1).getBirthdate());
    }

    @Test
    @DisplayName("updateMedicalRecord ne modifie pas un homonyme portant le même nom de famille")
    void updateMedicalRecord_shouldNotUpdateSameLastNameDifferentFirstName() {
        // GIVEN
        MedicalRecord update = new MedicalRecord("Tenley", "Boyd", "12/25/2010",
                List.of("sirop:5ml"), List.of());

        // WHEN
        medicalRecordService.updateMedicalRecord(update);

        // THEN
        assertEquals("12/25/2010", data.getMedicalrecords().get(1).getBirthdate());
        assertEquals("03/06/1984", data.getMedicalrecords().get(0).getBirthdate());
        assertEquals(List.of("aznol:350mg"), data.getMedicalrecords().get(0).getMedications());
    }

    // ----------------------------------------------------------------
    // deleteMedicalRecord
    // ----------------------------------------------------------------

    @Test
    @DisplayName("deleteMedicalRecord supprime le dossier identifié par prénom et nom")
    void deleteMedicalRecord_shouldRemoveRecord() {
        // GIVEN
        MedicalRecord toDelete = new MedicalRecord("John", "Boyd", null, null, null);

        // WHEN
        medicalRecordService.deleteMedicalRecord(toDelete);

        // THEN
        assertEquals(2, data.getMedicalrecords().size());
        assertFalse(data.getMedicalrecords().stream()
                .anyMatch(r -> r.getFirstName().equals("John") && r.getLastName().equals("Boyd")));
    }

    @Test
    @DisplayName("deleteMedicalRecord ne supprime pas les homonymes portant le même nom de famille")
    void deleteMedicalRecord_shouldOnlyRemoveMatchingRecord() {
        // GIVEN
        MedicalRecord toDelete = new MedicalRecord("Tenley", "Boyd", null, null, null);

        // WHEN
        medicalRecordService.deleteMedicalRecord(toDelete);

        // THEN
        assertEquals(2, data.getMedicalrecords().size());
        assertTrue(data.getMedicalrecords().stream()
                .anyMatch(r -> r.getFirstName().equals("John") && r.getLastName().equals("Boyd")));
    }

    @Test
    @DisplayName("deleteMedicalRecord ne supprime rien si la personne est inconnue")
    void deleteMedicalRecord_shouldDoNothingWhenPersonUnknown() {
        // GIVEN
        MedicalRecord toDelete = new MedicalRecord("Inconnu", "Personne", null, null, null);

        // WHEN
        medicalRecordService.deleteMedicalRecord(toDelete);

        // THEN
        assertEquals(3, data.getMedicalrecords().size());
    }
}