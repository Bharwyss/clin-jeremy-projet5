package com.openclassrooms.projet5.service;

import com.openclassrooms.projet5.dto.*;
import com.openclassrooms.projet5.model.FireStation;
import com.openclassrooms.projet5.model.MedicalRecord;
import com.openclassrooms.projet5.model.Person;
import com.openclassrooms.projet5.model.SafetyNetData;
import com.openclassrooms.projet5.utils.PersonFinder;
import com.openclassrooms.projet5.utils.SafetyNetDataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.openclassrooms.projet5.utils.AgeCalculator.calculateAge;

@Service
public class FireStationService {
    private static final Logger logger = LoggerFactory.getLogger(FireStationService.class);
    private final SafetyNetDataLoader dataLoader;

    public FireStationService(SafetyNetDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    public void addFireStation(FireStation fireStation) {
        logger.info("Adding a fire station: {}", fireStation);
        SafetyNetData data = dataLoader.getSafetyNetData();
        data.getFirestations().add(fireStation);
        dataLoader.saveSafetyData(data);
    }

    public void updateFireStation(FireStation fireStation) {
        logger.info("Updating fire station number: {}", fireStation);
        SafetyNetData data = dataLoader.getSafetyNetData();
        data.getFirestations().stream()
                .filter(fireStation1 -> fireStation1.getAddress().equals(fireStation.getAddress()))
                .findFirst()
                .ifPresent(fireStation1 -> {
                    fireStation1.setStation(fireStation.getStation());
                });
        dataLoader.saveSafetyData(data);
    }

    public void deleteFireStation(FireStation fireStation) {
        logger.info("Deleting a fire station : {}", fireStation);
        SafetyNetData data = dataLoader.getSafetyNetData();
        data.getFirestations().removeIf(station -> (station.getAddress().equals(fireStation.getAddress())
                && station.getStation().equals(fireStation.getStation())));
        dataLoader.saveSafetyData(data);
    }

    public FireStationCoverageDto getStationCoverage(String stationNumber) {
        logger.info("Requiring data from station number {} coverage", stationNumber);
        SafetyNetData data = dataLoader.getSafetyNetData();

        List<String> addresses = getAddressesFromFireStation(data, stationNumber);

        List<PersonFromStationDto> persons = getPersonFromFireStation(data, addresses);

        int adultCount = 0;
        int childCount = 0;

        for (PersonFromStationDto person : persons) {
            for (MedicalRecord record : data.getMedicalrecords()) {
                if (record.getFirstName().equals(person.firstName())
                        && record.getLastName().equals(person.lastName())) {
                    int age = calculateAge(record.getBirthdate());
                    if (age <= 18) {
                        childCount++;
                    } else {
                        adultCount++;
                    }
                    break;
                }
            }
        }
        return new FireStationCoverageDto(persons, adultCount, childCount);
    }

    public List<String> getPhoneAlert(String stationNumber) {
        logger.info("Requiring data from phone alert {}", stationNumber);

        SafetyNetData data = dataLoader.getSafetyNetData();

        List<String> addresses = getAddressesFromFireStation(data, stationNumber);
        List<PersonFromStationDto> persons = getPersonFromFireStation(data, addresses);
        List<String> phoneAlerts = new ArrayList<>();

        for (PersonFromStationDto person : persons) {
            phoneAlerts.add(person.phone());
        }
        return phoneAlerts;
    }

    public FireDto getFireAddress(String address) {
        logger.info("Requiring data from fire address");
        SafetyNetData data = dataLoader.getSafetyNetData();
        List<Person> personByAddress = PersonFinder.getPersonByAddress(data, address);
        List<PersonFromFireDto> personFromFireDtoList = new ArrayList<>();
        for (Person person : personByAddress) {
            PersonFromFireDto dto = buildPersonMedicalDto(data, person);
            if (dto != null) {
                personFromFireDtoList.add(dto);
            }
        }
        return new FireDto(personFromFireDtoList, getStationNumberFromAddress(data, address));
    }

    public List<FloodHousehold> getFloodHousehold(List<String> stations) {
        logger.info("Requiring data from flood household {}", stations);
        SafetyNetData data = dataLoader.getSafetyNetData();

        List<String> addresses = new ArrayList<>();
        for (String station : stations) {
            addresses.addAll(getAddressesFromFireStation(data, station));
        }

        List<FloodHousehold> floodHouseholds = new ArrayList<>();
        for (String address : addresses) {
            List<Person> personByAddress = PersonFinder.getPersonByAddress(data, address);
            List<PersonFromFireDto> personsFromFireDtoList = new ArrayList<>();
            for (Person person : personByAddress) {
                PersonFromFireDto dto = buildPersonMedicalDto(data, person);
                if (dto != null) {
                    personsFromFireDtoList.add(dto);
                }
            }
            floodHouseholds.add(new FloodHousehold(address, personsFromFireDtoList));
        }
        return floodHouseholds;
    }


    private PersonFromFireDto buildPersonMedicalDto(SafetyNetData data, Person person) {
        for (MedicalRecord record : data.getMedicalrecords()) {
            if (record.getFirstName().equals(person.getFirstName())
                    && record.getLastName().equals(person.getLastName())) {
                int age = calculateAge(record.getBirthdate());
                return new PersonFromFireDto(person.getLastName(), person.getPhone(),
                        age, record.getMedications(), record.getAllergies());
            }
        }
        return null;
    }

    private List<String> getAddressesFromFireStation(SafetyNetData data, String stationNumber) {
        return data.getFirestations().stream()
                .filter(fireStation -> fireStation.getStation().equals(stationNumber))
                .map(FireStation::getAddress)
                .toList();
    }

    private List<PersonFromStationDto> getPersonFromFireStation(SafetyNetData data, List<String> addresses) {
        return data.getPersons().stream()
                .filter(person -> addresses.contains(person.getAddress()))
                .map(person -> new PersonFromStationDto(
                        person.getFirstName(),
                        person.getLastName(),
                        person.getAddress(),
                        person.getPhone()))
                .toList();
    }

    private String getStationNumberFromAddress(SafetyNetData data, String address) {
        return data.getFirestations().stream()
                .filter(fireStation -> fireStation.getAddress().equals(address))
                .map(FireStation::getStation)
                .findFirst()
                .orElse(null);
    }
}
