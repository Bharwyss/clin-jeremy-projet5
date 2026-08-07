package com.openclassrooms.projet5.controller;

import com.openclassrooms.projet5.model.MedicalRecord;
import com.openclassrooms.projet5.service.MedicalRecordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MedicalRecordController.class)
class MedicalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MedicalRecordService medicalRecordService;

    private static final String VALID_BODY = """
            {
              "firstName": "Bertrand",
              "lastName": "Dupont",
              "birthdate": "05/14/1990",
              "medications": ["doliprane:500mg", "aspirine:100mg"],
              "allergies": ["pollen"]
            }
            """;

    private static final MedicalRecord EXPECTED_RECORD = new MedicalRecord(
            "Bertrand", "Dupont", "05/14/1990",
            List.of("doliprane:500mg", "aspirine:100mg"),
            List.of("pollen"));

    // ----------------------------------------------------------------
    // POST /medicalrecord
    // ----------------------------------------------------------------

    @Test
    @DisplayName("POST /medicalrecord retourne 200 et délègue au service")
    void addMedicalRecord_shouldReturnOkAndCallService() throws Exception {
        // WHEN
        mockMvc.perform(post("/medicalrecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                // THEN
                .andExpect(status().isOk());

        verify(medicalRecordService).addMedicalRecord(EXPECTED_RECORD);
    }

    @Test
    @DisplayName("POST /medicalrecord désérialise les listes de médicaments et d'allergies")
    void addMedicalRecord_shouldBindMedicationsAndAllergies() throws Exception {
        // GIVEN
        ArgumentCaptor<MedicalRecord> captor = ArgumentCaptor.forClass(MedicalRecord.class);

        // WHEN
        mockMvc.perform(post("/medicalrecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk());

        // THEN
        verify(medicalRecordService).addMedicalRecord(captor.capture());
        MedicalRecord captured = captor.getValue();
        assertEquals("Bertrand", captured.getFirstName());
        assertEquals("Dupont", captured.getLastName());
        assertEquals("05/14/1990", captured.getBirthdate());
        assertEquals(List.of("doliprane:500mg", "aspirine:100mg"), captured.getMedications());
        assertEquals(List.of("pollen"), captured.getAllergies());
    }

    @Test
    @DisplayName("POST /medicalrecord accepte des listes vides")
    void addMedicalRecord_shouldAcceptEmptyLists() throws Exception {
        // GIVEN
        String bodyWithEmptyLists = """
                {
                  "firstName": "Sans",
                  "lastName": "Traitement",
                  "birthdate": "01/01/2000",
                  "medications": [],
                  "allergies": []
                }
                """;
        ArgumentCaptor<MedicalRecord> captor = ArgumentCaptor.forClass(MedicalRecord.class);

        // WHEN
        mockMvc.perform(post("/medicalrecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyWithEmptyLists))
                .andExpect(status().isOk());

        // THEN
        verify(medicalRecordService).addMedicalRecord(captor.capture());
        assertTrue(captor.getValue().getMedications().isEmpty());
        assertTrue(captor.getValue().getAllergies().isEmpty());
    }

    @Test
    @DisplayName("POST /medicalrecord retourne 400 si le JSON est invalide")
    void addMedicalRecord_shouldReturnBadRequestWhenJsonIsMalformed() throws Exception {
        // WHEN
        mockMvc.perform(post("/medicalrecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ ceci n'est pas du JSON }"))
                // THEN
                .andExpect(status().isBadRequest());

        verifyNoInteractions(medicalRecordService);
    }

    // ----------------------------------------------------------------
    // PUT /medicalrecord
    // ----------------------------------------------------------------

    @Test
    @DisplayName("PUT /medicalrecord retourne 200 et délègue au service")
    void updateMedicalRecord_shouldReturnOkAndCallService() throws Exception {
        // WHEN
        mockMvc.perform(put("/medicalrecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                // THEN
                .andExpect(status().isOk());

        verify(medicalRecordService).updateMedicalRecord(EXPECTED_RECORD);
    }

    // ----------------------------------------------------------------
    // DELETE /medicalrecord
    // ----------------------------------------------------------------

    @Test
    @DisplayName("DELETE /medicalrecord retourne 200 et délègue au service")
    void deleteMedicalRecord_shouldReturnOkAndCallService() throws Exception {
        // WHEN
        mockMvc.perform(delete("/medicalrecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                // THEN
                .andExpect(status().isOk());

        verify(medicalRecordService).deleteMedicalRecord(EXPECTED_RECORD);
    }

    @Test
    @DisplayName("DELETE /medicalrecord fonctionne avec un corps réduit au prénom et au nom")
    void deleteMedicalRecord_shouldAcceptIdentityOnlyBody() throws Exception {
        // GIVEN
        String identityOnlyBody = """
                {
                  "firstName": "Bertrand",
                  "lastName": "Dupont"
                }
                """;
        ArgumentCaptor<MedicalRecord> captor = ArgumentCaptor.forClass(MedicalRecord.class);

        // WHEN
        mockMvc.perform(delete("/medicalrecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(identityOnlyBody))
                .andExpect(status().isOk());

        // THEN
        verify(medicalRecordService).deleteMedicalRecord(captor.capture());
        assertEquals("Bertrand", captor.getValue().getFirstName());
        assertEquals("Dupont", captor.getValue().getLastName());
    }
}