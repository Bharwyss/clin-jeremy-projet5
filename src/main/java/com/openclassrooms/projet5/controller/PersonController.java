package com.openclassrooms.projet5.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import com.openclassrooms.projet5.service.PersonService;

@RestController
public class PersonController {
    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);
    private final PersonService service;

    public PersonController(PersonService service) {
        this.service = service;
    }
}
