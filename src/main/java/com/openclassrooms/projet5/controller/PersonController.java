package com.openclassrooms.projet5.controller;

import com.openclassrooms.projet5.model.Person;
import com.openclassrooms.projet5.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    public ResponseEntity<Void> addPerson (@RequestBody Person person) {
        personService.addPerson(person);
        return ResponseEntity.ok().build();
    }

    @PutMapping

    public ResponseEntity<Void> updatePerson (@RequestBody Person person) {
        personService.updatePerson(person);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deletePerson (@RequestBody Person person) {
        personService.deletePerson(person);
        return ResponseEntity.ok().build();
    }
}
