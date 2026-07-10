package com.openclassrooms.projet5.utils;

import com.openclassrooms.projet5.model.Person;
import com.openclassrooms.projet5.model.SafetyNetData;

import java.util.List;

public class PersonFinder {
    private PersonFinder() {}
    public static List<Person> getPersonByAddress(SafetyNetData data, String address) {
        return data.getPersons().stream()
                .filter(p -> p.getAddress().equals(address))
                .toList();
    }
}
