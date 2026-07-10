package com.openclassrooms.projet5.controller;

import com.openclassrooms.projet5.dto.*;
import com.openclassrooms.projet5.service.FireStationService;
import com.openclassrooms.projet5.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UrlController {

    private final FireStationService fireStationService;
    private final PersonService personService;

    public UrlController(FireStationService fireStationService, PersonService personService) {
        this.fireStationService = fireStationService;
        this.personService = personService;
    }

    @GetMapping("/firestation")
    public ResponseEntity<FireStationCoverageDto> getStationCoverage(@RequestParam String stationNumber) {
        FireStationCoverageDto coverage = fireStationService.getStationCoverage(stationNumber);
        return ResponseEntity.ok(coverage);
    }

    @GetMapping("/childAlert")
    public ResponseEntity<ChildAlertDto> getChildAlert(@RequestParam String address) {
        ChildAlertDto childAlertDto = personService.getChildAlert(address);
        return ResponseEntity.ok(childAlertDto);
    }

    @GetMapping("/phoneAlert")
    public ResponseEntity<List<String>> getPhoneAlert(@RequestParam("firestation") String stationNumber) {
       List<String> phoneAlerts = fireStationService.getPhoneAlert(stationNumber);
       return ResponseEntity.ok(phoneAlerts);
    }

    @GetMapping("/fire")
    public ResponseEntity<FireDto> getFire(@RequestParam String address) {
        FireDto fireDto = fireStationService.getFireAddress(address);
        return ResponseEntity.ok(fireDto);
    }

    @GetMapping("/flood/stations")
    public ResponseEntity<List<FloodHousehold>> getFloodHousehold(@RequestParam("stations") List<String> stations) {
        List<FloodHousehold> floodHousehold = fireStationService.getFloodHousehold(stations);
        return ResponseEntity.ok(floodHousehold);
    }

    @GetMapping("/personInfo")
    public ResponseEntity<List<PersonLastName>> getPersonLastName(@RequestParam String lastName) {
        List<PersonLastName> personLastNames = personService.getPersonLastNames(lastName);
        return ResponseEntity.ok(personLastNames);
    }

    @GetMapping("/communityEmail")
    public ResponseEntity<List<String>> getCommunityEmail(@RequestParam String city) {
        List<String> communityEmail = personService.getCommunityEmails(city);
        return ResponseEntity.ok(communityEmail);
    }
}