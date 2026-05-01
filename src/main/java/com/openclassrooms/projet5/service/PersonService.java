package com.openclassrooms.projet5.service;

import com.openclassrooms.projet5.model.Person;
import com.openclassrooms.projet5.model.SafetyNetData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.openclassrooms.projet5.utils.SafetyNetDataLoader;

import java.util.Iterator;

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

    public void updatePerson(Person person) {
        logger.info("Updating person : {}", person);
        SafetyNetData data = dataLoader.getSafetyNetData();
        data.getPersons().stream()
                .filter(p -> p.getFirstName().equals(person.getFirstName())
                        && p.getLastName().equals(person.getLastName()))
                .findFirst()
                .ifPresent(p -> {
                    p.setAddress(person.getAddress());
                    p.setCity(person.getCity());
                    p.setZip(person.getZip());
                    p.setPhone(person.getPhone());
                    p.setEmail(person.getEmail());
                });
        dataLoader.saveSafetyData(data);
    }

    public void deletePerson(Person person) {
        logger.info("Deleting person : {}", person);
        SafetyNetData data = dataLoader.getSafetyNetData();
        data.getPersons().removeIf(p -> (p.getFirstName().equals(person.getFirstName()) &&
                p.getLastName().equals(person.getLastName())));
        dataLoader.saveSafetyData(data);
    }
}
