package com.openclassrooms.projet5.controller;

import com.openclassrooms.projet5.model.FireStation;
import com.openclassrooms.projet5.service.FireStationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FireStationController.class)
class FireStationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FireStationService fireStationService;

    private static final String VALID_BODY = """
            {
              "address": "12 Rue des Lilas",
              "station": "4"
            }
            """;

    // ----------------------------------------------------------------
    // POST /firestation
    // ----------------------------------------------------------------

    @Test
    @DisplayName("POST /firestation retourne 200 et délègue au service")
    void addFireStation_shouldReturnOkAndCallService() throws Exception {
        // WHEN
        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                // THEN
                .andExpect(status().isOk());

        verify(fireStationService).addFireStation(new FireStation("12 Rue des Lilas", "4"));
    }

    @Test
    @DisplayName("POST /firestation désérialise correctement le corps de la requête")
    void addFireStation_shouldBindRequestBody() throws Exception {
        // GIVEN
        ArgumentCaptor<FireStation> captor = ArgumentCaptor.forClass(FireStation.class);

        // WHEN
        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk());

        // THEN
        verify(fireStationService).addFireStation(captor.capture());
        assertEquals("12 Rue des Lilas", captor.getValue().getAddress());
        assertEquals("4", captor.getValue().getStation());
    }

    @Test
    @DisplayName("POST /firestation retourne 400 si le JSON est invalide")
    void addFireStation_shouldReturnBadRequestWhenJsonIsMalformed() throws Exception {
        // WHEN
        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ ceci n'est pas du JSON }"))
                // THEN
                .andExpect(status().isBadRequest());

        verifyNoInteractions(fireStationService);
    }

    // ----------------------------------------------------------------
    // PUT /firestation
    // ----------------------------------------------------------------

    @Test
    @DisplayName("PUT /firestation retourne 200 et délègue au service")
    void updateFireStation_shouldReturnOkAndCallService() throws Exception {
        // WHEN
        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                // THEN
                .andExpect(status().isOk());

        verify(fireStationService).updateFireStation(new FireStation("12 Rue des Lilas", "4"));
    }

    // ----------------------------------------------------------------
    // DELETE /firestation
    // ----------------------------------------------------------------

    @Test
    @DisplayName("DELETE /firestation retourne 200 et délègue au service")
    void deleteFireStation_shouldReturnOkAndCallService() throws Exception {
        // WHEN
        mockMvc.perform(delete("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                // THEN
                .andExpect(status().isOk());

        verify(fireStationService).deleteFireStation(new FireStation("12 Rue des Lilas", "4"));
    }
}