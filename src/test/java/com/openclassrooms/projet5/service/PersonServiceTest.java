package com.openclassrooms.projet5.service;


import com.openclassrooms.projet5.model.Person;
import com.openclassrooms.projet5.model.SafetyNetData;
import com.openclassrooms.projet5.utils.SafetyNetDataLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @Mock
    private SafetyNetDataLoader dataLoader;

    @InjectMocks
    private PersonService personService;

    @Test
    void testAddPerson_ShouldAddPersonToList() {
        // GIVEN
        // New person
        Person person = new Person();
        person.setFirstName("Bertrand");
        person.setLastName("Dupont");
        person.setAddress("Rue des Champs");
        person.setCity("Paris");
        person.setZip("75000");
        person.setPhone("06 XX XX XX XX");
        person.setEmail("dupont.bertrand@gmail.com");

        // New Data set
        SafetyNetData data = new SafetyNetData();
        data.setPersons(new ArrayList<>());
        when(dataLoader.getSafetyNetData()).thenReturn(data);

        // WHEN
        personService.addPerson(person);

        // THEN -
        verify(dataLoader).saveSafetyData(any(SafetyNetData.class));
    }
}
