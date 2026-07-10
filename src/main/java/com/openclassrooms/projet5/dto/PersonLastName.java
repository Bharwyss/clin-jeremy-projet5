package com.openclassrooms.projet5.dto;

import java.util.List;

public record PersonLastName(String lastName, String address, int age, String mail, List<String> medications, List<String> allergies) {
}
