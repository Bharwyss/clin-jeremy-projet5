package com.openclassrooms.projet5.controller;

import com.openclassrooms.projet5.model.Person;
import com.openclassrooms.projet5.service.PersonService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonController.class)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonService personService;

    private static final String VALID_BODY = """
            {
              "firstName": "Bertrand",
              "lastName": "Dupont",
              "address": "12 Rue des Lilas",
              "city": "Culver",
              "zip": "97451",
              "phone": "841-874-1234",
              "email": "bertrand.dupont@email.com"
            }
            """;

    private static final Person EXPECTED_PERSON = new Person(
            "Bertrand", "Dupont", "12 Rue des Lilas", "Culver",
            "97451", "841-874-1234", "bertrand.dupont@email.com");

    // ----------------------------------------------------------------
    // POST /person
    // ----------------------------------------------------------------

    @Test
    @DisplayName("POST /person retourne 200 et délègue au service")
    void addPerson_shouldReturnOkAndCallService() throws Exception {
        // WHEN
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                // THEN
                .andExpect(status().isOk());

        verify(personService).addPerson(EXPECTED_PERSON);
    }

    @Test
    @DisplayName("POST /person désérialise l'ensemble des champs du corps de la requête")
    void addPerson_shouldBindAllFields() throws Exception {
        // GIVEN
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);

        // WHEN
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk());

        // THEN
        verify(personService).addPerson(captor.capture());
        Person captured = captor.getValue();
        assertEquals("Bertrand", captured.getFirstName());
        assertEquals("Dupont", captured.getLastName());
        assertEquals("12 Rue des Lilas", captured.getAddress());
        assertEquals("Culver", captured.getCity());
        assertEquals("97451", captured.getZip());
        assertEquals("841-874-1234", captured.getPhone());
        assertEquals("bertrand.dupont@email.com", captured.getEmail());
    }

    @Test
    @DisplayName("POST /person retourne 400 si le JSON est invalide")
    void addPerson_shouldReturnBadRequestWhenJsonIsMalformed() throws Exception {
        // WHEN
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ ceci n'est pas du JSON }"))
                // THEN
                .andExpect(status().isBadRequest());

        verifyNoInteractions(personService);
    }

    // ----------------------------------------------------------------
    // PUT /person
    // ----------------------------------------------------------------

    @Test
    @DisplayName("PUT /person retourne 200 et délègue au service")
    void updatePerson_shouldReturnOkAndCallService() throws Exception {
        // WHEN
        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                // THEN
                .andExpect(status().isOk());

        verify(personService).updatePerson(EXPECTED_PERSON);
    }

    // ----------------------------------------------------------------
    // DELETE /person
    // ----------------------------------------------------------------

    @Test
    @DisplayName("DELETE /person retourne 200 et délègue au service")
    void deletePerson_shouldReturnOkAndCallService() throws Exception {
        // WHEN
        mockMvc.perform(delete("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                // THEN
                .andExpect(status().isOk());

        verify(personService).deletePerson(EXPECTED_PERSON);
    }

    @Test
    @DisplayName("DELETE /person fonctionne avec un corps réduit au prénom et au nom")
    void deletePerson_shouldAcceptIdentityOnlyBody() throws Exception {
        // GIVEN
        String identityOnlyBody = """
                {
                  "firstName": "Bertrand",
                  "lastName": "Dupont"
                }
                """;
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);

        // WHEN
        mockMvc.perform(delete("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(identityOnlyBody))
                .andExpect(status().isOk());

        // THEN
        verify(personService).deletePerson(captor.capture());
        Person captured = captor.getValue();
        assertEquals("Bertrand", captured.getFirstName());
        assertEquals("Dupont", captured.getLastName());
        assertNull(captured.getAddress());
        assertNull(captured.getEmail());
    }
}