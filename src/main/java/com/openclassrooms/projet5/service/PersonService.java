package com.openclassrooms.projet5.service;

import com.openclassrooms.projet5.model.Person;
import com.openclassrooms.projet5.model.SafetyNetData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.openclassrooms.projet5.utils.SafetyNetDataLoader;

@Service
public class PersonService {

    private static final Logger logger = LoggerFactory.getLogger(PersonService.class);
    private final SafetyNetDataLoader dataLoader;

    public PersonService(SafetyNetDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    public void addPerson(Person person) {
        logger.info("Adding person: {}", person);
        SafetyNetData data = dataLoader.getSafetyNetData();
        data.getPersons().add(person);
        dataLoader.saveSafetyData(data);
    }
}
