package com.openclassrooms.projet5.service;

import com.openclassrooms.projet5.dto.ChildAlertDto;
import com.openclassrooms.projet5.dto.ChildDto;
import com.openclassrooms.projet5.dto.HouseholdMemberDto;
import com.openclassrooms.projet5.model.MedicalRecord;
import com.openclassrooms.projet5.model.Person;
import com.openclassrooms.projet5.model.SafetyNetData;
import com.openclassrooms.projet5.utils.AgeCalculator;
import com.openclassrooms.projet5.utils.SafetyNetDataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.openclassrooms.projet5.utils.PersonFinder.getPersonByAddress;

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

    public ChildAlertDto getChildAlert(String addresses) {
        logger.info("Getting children from household for alert");

        SafetyNetData data = dataLoader.getSafetyNetData();

        List<Person> household = getPersonByAddress(data, addresses);
        List<ChildDto> childDtoList = new ArrayList<>();
        List<HouseholdMemberDto> memberDtoList = new ArrayList<>();

        for (Person person : household) {
            for (MedicalRecord record : data.getMedicalrecords()) {
                if (record.getFirstName().equals(person.getFirstName())
                        && record.getLastName().equals(person.getLastName())) {
                    int age = AgeCalculator.calculateAge(record.getBirthdate());
                    if (age <= 18) {
                        childDtoList.add(new ChildDto(person.getFirstName(), person.getLastName(), age));
                    } else {
                        memberDtoList.add(new HouseholdMemberDto(person.getFirstName(), person.getLastName()));
                    }
                    break;
                }
            }
        }
        if (childDtoList.isEmpty()) {
            return new ChildAlertDto(List.of(), List.of());
        }
        return new ChildAlertDto(childDtoList, memberDtoList);
    }
}
