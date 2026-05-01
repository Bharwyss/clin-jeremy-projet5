package com.openclassrooms.projet5.service;

import com.openclassrooms.projet5.dto.FireStationCoverageDto;
import com.openclassrooms.projet5.dto.PersonFromStationDto;
import com.openclassrooms.projet5.model.FireStation;
import com.openclassrooms.projet5.model.MedicalRecord;
import com.openclassrooms.projet5.model.SafetyNetData;
import com.openclassrooms.projet5.utils.SafetyNetDataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FireStationService {
    private static final Logger logger = LoggerFactory.getLogger(FireStationService.class);
    private final SafetyNetDataLoader dataLoader;

    public FireStationService(SafetyNetDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    public void addFireStation (FireStation fireStation) {
        logger.info("Adding a fire station: {}", fireStation);
        SafetyNetData data = dataLoader.getSafetyNetData();
        data.getFirestations().add(fireStation);
        dataLoader.saveSafetyData(data);
    }

    public void updateFireStation (FireStation fireStation) {
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

    public void deleteFireStation (FireStation fireStation) {
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

        List<PersonFromStationDto> persons = getPersonFromFireStation (data, addresses);

        int adultCount = 0;
        int childCount = 0;

        for (PersonFromStationDto person : persons) {
            for (MedicalRecord record : data.getMedicalrecords()) {
                if (record.getFirstName().equals(person.firstName())
                        && record.getLastName().equals(person.lastName())) {
                    LocalDate birthDate = LocalDate.parse(record.getBirthdate(),
                            DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                    int age = Period.between(birthDate, LocalDate.now()).getYears();
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

    public List<String> getAddressesFromFireStation (SafetyNetData data, String stationNumber) {
        return data.getFirestations().stream()
                .filter(fireStation -> fireStation.getStation().equals(stationNumber))
                .map(FireStation::getAddress)
                .toList();
    }

    public List<PersonFromStationDto> getPersonFromFireStation (SafetyNetData data, List<String> addresses) {
        return data.getPersons().stream()
                .filter(person -> addresses.contains(person.getAddress()))
                .map(person -> new PersonFromStationDto(
                        person.getFirstName(),
                        person.getLastName(),
                        person.getAddress(),
                        person.getPhone()))
                .toList();
    }
}
