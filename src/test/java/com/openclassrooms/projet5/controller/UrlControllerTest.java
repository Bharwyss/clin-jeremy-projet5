package com.openclassrooms.projet5.controller;

import com.openclassrooms.projet5.dto.ChildAlertDto;
import com.openclassrooms.projet5.dto.ChildDto;
import com.openclassrooms.projet5.dto.FireDto;
import com.openclassrooms.projet5.dto.FireStationCoverageDto;
import com.openclassrooms.projet5.dto.FloodHousehold;
import com.openclassrooms.projet5.dto.HouseholdMemberDto;
import com.openclassrooms.projet5.dto.PersonFromFireDto;
import com.openclassrooms.projet5.dto.PersonFromStationDto;
import com.openclassrooms.projet5.dto.PersonLastName;
import com.openclassrooms.projet5.service.FireStationService;
import com.openclassrooms.projet5.service.PersonService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FireStationService fireStationService;

    @MockitoBean
    private PersonService personService;

    // ----------------------------------------------------------------
    // GET /firestation?stationNumber=
    // ----------------------------------------------------------------

    @Test
    @DisplayName("GET /firestation retourne la couverture avec le décompte adultes/enfants")
    void getStationCoverage_shouldReturnCoverage() throws Exception {
        // GIVEN
        FireStationCoverageDto coverage = new FireStationCoverageDto(
                List.of(new PersonFromStationDto("John", "Boyd", "1509 Culver St", "841-874-6512")),
                1, 0);
        when(fireStationService.getStationCoverage("3")).thenReturn(coverage);

        // WHEN
        mockMvc.perform(get("/firestation").param("stationNumber", "3"))
                // THEN
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.adultCount").value(1))
                .andExpect(jsonPath("$.childCount").value(0))
                .andExpect(jsonPath("$.persons", hasSize(1)))
                .andExpect(jsonPath("$.persons[0].firstName").value("John"))
                .andExpect(jsonPath("$.persons[0].lastName").value("Boyd"))
                .andExpect(jsonPath("$.persons[0].address").value("1509 Culver St"))
                .andExpect(jsonPath("$.persons[0].phone").value("841-874-6512"));

        verify(fireStationService).getStationCoverage("3");
    }

    @Test
    @DisplayName("GET /firestation retourne 400 sans le paramètre stationNumber")
    void getStationCoverage_shouldReturnBadRequestWhenParamMissing() throws Exception {
        // WHEN
        mockMvc.perform(get("/firestation"))
                // THEN
                .andExpect(status().isBadRequest());

        verifyNoInteractions(fireStationService);
    }

    // ----------------------------------------------------------------
    // GET /childAlert?address=
    // ----------------------------------------------------------------

    @Test
    @DisplayName("GET /childAlert retourne les enfants et les membres du foyer")
    void getChildAlert_shouldReturnChildrenAndFamily() throws Exception {
        // GIVEN
        ChildAlertDto childAlert = new ChildAlertDto(
                List.of(new ChildDto("Tenley", "Boyd", 12)),
                List.of(new HouseholdMemberDto("John", "Boyd")));
        when(personService.getChildAlert("1509 Culver St")).thenReturn(childAlert);

        // WHEN
        mockMvc.perform(get("/childAlert").param("address", "1509 Culver St"))
                // THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.children", hasSize(1)))
                .andExpect(jsonPath("$.children[0].firstName").value("Tenley"))
                .andExpect(jsonPath("$.children[0].age").value(12))
                .andExpect(jsonPath("$.family", hasSize(1)))
                .andExpect(jsonPath("$.family[0].firstName").value("John"));

        verify(personService).getChildAlert("1509 Culver St");
    }

    @Test
    @DisplayName("GET /childAlert retourne des listes vides quand le foyer ne compte aucun enfant")
    void getChildAlert_shouldReturnEmptyListsWhenNoChild() throws Exception {
        // GIVEN
        when(personService.getChildAlert(anyString()))
                .thenReturn(new ChildAlertDto(List.of(), List.of()));

        // WHEN
        mockMvc.perform(get("/childAlert").param("address", "489 Manchester St"))
                // THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.children", hasSize(0)))
                .andExpect(jsonPath("$.family", hasSize(0)));
    }

    // ----------------------------------------------------------------
    // GET /phoneAlert?firestation=
    // ----------------------------------------------------------------

    @Test
    @DisplayName("GET /phoneAlert retourne les numéros de téléphone des résidents")
    void getPhoneAlert_shouldReturnPhoneNumbers() throws Exception {
        // GIVEN
        when(fireStationService.getPhoneAlert("3"))
                .thenReturn(List.of("841-874-6512", "841-874-6513"));

        // WHEN
        mockMvc.perform(get("/phoneAlert").param("firestation", "3"))
                // THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]").value("841-874-6512"))
                .andExpect(jsonPath("$[1]").value("841-874-6513"));

        verify(fireStationService).getPhoneAlert("3");
    }

    @Test
    @DisplayName("GET /phoneAlert exige le paramètre nommé firestation")
    void getPhoneAlert_shouldRequireFirestationParamName() throws Exception {
        // WHEN - le paramètre porte le nom interne, pas celui attendu par l'URL
        mockMvc.perform(get("/phoneAlert").param("stationNumber", "3"))
                // THEN
                .andExpect(status().isBadRequest());

        verifyNoInteractions(fireStationService);
    }

    // ----------------------------------------------------------------
    // GET /fire?address=
    // ----------------------------------------------------------------

    @Test
    @DisplayName("GET /fire retourne les habitants et le numéro de caserne")
    void getFire_shouldReturnResidentsAndStation() throws Exception {
        // GIVEN
        FireDto fireDto = new FireDto(
                List.of(new PersonFromFireDto("Boyd", "841-874-6512", 40,
                        List.of("aznol:350mg"), List.of("nillacilan"))),
                "3");
        when(fireStationService.getFireAddress("1509 Culver St")).thenReturn(fireDto);

        // WHEN
        mockMvc.perform(get("/fire").param("address", "1509 Culver St"))
                // THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberStation").value("3"))
                .andExpect(jsonPath("$.personFromFireDtoList", hasSize(1)))
                .andExpect(jsonPath("$.personFromFireDtoList[0].lastName").value("Boyd"))
                .andExpect(jsonPath("$.personFromFireDtoList[0].phone").value("841-874-6512"))
                .andExpect(jsonPath("$.personFromFireDtoList[0].age").value(40))
                .andExpect(jsonPath("$.personFromFireDtoList[0].medications[0]").value("aznol:350mg"))
                .andExpect(jsonPath("$.personFromFireDtoList[0].allergies[0]").value("nillacilan"));

        verify(fireStationService).getFireAddress("1509 Culver St");
    }

    // ----------------------------------------------------------------
    // GET /flood/stations?stations=
    // ----------------------------------------------------------------

    @Test
    @DisplayName("GET /flood/stations regroupe les foyers par adresse")
    void getFloodHousehold_shouldReturnHouseholdsGroupedByAddress() throws Exception {
        // GIVEN
        List<FloodHousehold> households = List.of(
                new FloodHousehold("1509 Culver St",
                        List.of(new PersonFromFireDto("Boyd", "841-874-6512", 40,
                                List.of("aznol:350mg"), List.of("nillacilan")))),
                new FloodHousehold("29 15th St",
                        List.of(new PersonFromFireDto("Marrack", "841-874-6513", 35,
                                List.of(), List.of()))));
        when(fireStationService.getFloodHousehold(List.of("1", "2"))).thenReturn(households);

        // WHEN
        mockMvc.perform(get("/flood/stations").param("stations", "1", "2"))
                // THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].address").value("1509 Culver St"))
                .andExpect(jsonPath("$[0].members", hasSize(1)))
                .andExpect(jsonPath("$[0].members[0].lastName").value("Boyd"))
                .andExpect(jsonPath("$[1].address").value("29 15th St"))
                .andExpect(jsonPath("$[1].members[0].age").value(35));

        verify(fireStationService).getFloodHousehold(List.of("1", "2"));
    }

    @Test
    @DisplayName("GET /flood/stations accepte une liste de stations séparées par des virgules")
    void getFloodHousehold_shouldAcceptCommaSeparatedStations() throws Exception {
        // GIVEN
        when(fireStationService.getFloodHousehold(anyList())).thenReturn(List.of());

        // WHEN
        mockMvc.perform(get("/flood/stations").param("stations", "1,2,3"))
                // THEN
                .andExpect(status().isOk());

        verify(fireStationService).getFloodHousehold(List.of("1", "2", "3"));
    }

    // ----------------------------------------------------------------
    // GET /personInfo?lastName=
    // ----------------------------------------------------------------

    @Test
    @DisplayName("GET /personInfo retourne toutes les personnes portant le nom demandé")
    void getPersonInfo_shouldReturnPersonsWithSameLastName() throws Exception {
        // GIVEN
        List<PersonLastName> persons = List.of(
                new PersonLastName("Boyd", "1509 Culver St", 40, "jaboyd@email.com",
                        List.of("aznol:350mg"), List.of("nillacilan")),
                new PersonLastName("Boyd", "1509 Culver St", 12, "tenz@email.com",
                        List.of(), List.of("peanut")));
        when(personService.getPersonLastNames("Boyd")).thenReturn(persons);

        // WHEN
        mockMvc.perform(get("/personInfo").param("lastName", "Boyd"))
                // THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].lastName").value("Boyd"))
                .andExpect(jsonPath("$[0].address").value("1509 Culver St"))
                .andExpect(jsonPath("$[0].age").value(40))
                .andExpect(jsonPath("$[0].mail").value("jaboyd@email.com"))
                .andExpect(jsonPath("$[1].age").value(12))
                .andExpect(jsonPath("$[1].allergies[0]").value("peanut"));

        verify(personService).getPersonLastNames("Boyd");
    }

    @Test
    @DisplayName("GET /personInfo retourne une liste vide pour un nom inconnu")
    void getPersonInfo_shouldReturnEmptyListWhenLastNameUnknown() throws Exception {
        // GIVEN
        when(personService.getPersonLastNames("Inconnu")).thenReturn(List.of());

        // WHEN
        mockMvc.perform(get("/personInfo").param("lastName", "Inconnu"))
                // THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ----------------------------------------------------------------
    // GET /communityEmail?city=
    // ----------------------------------------------------------------

    @Test
    @DisplayName("GET /communityEmail retourne les e-mails des habitants de la ville")
    void getCommunityEmail_shouldReturnEmails() throws Exception {
        // GIVEN
        when(personService.getCommunityEmails("Culver"))
                .thenReturn(List.of("jaboyd@email.com", "tenz@email.com"));

        // WHEN
        mockMvc.perform(get("/communityEmail").param("city", "Culver"))
                // THEN
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]").value("jaboyd@email.com"));

        verify(personService).getCommunityEmails("Culver");
    }

    @Test
    @DisplayName("GET /communityEmail retourne 400 sans le paramètre city")
    void getCommunityEmail_shouldReturnBadRequestWhenParamMissing() throws Exception {
        // WHEN
        mockMvc.perform(get("/communityEmail"))
                // THEN
                .andExpect(status().isBadRequest());

        verifyNoInteractions(personService);
    }
}