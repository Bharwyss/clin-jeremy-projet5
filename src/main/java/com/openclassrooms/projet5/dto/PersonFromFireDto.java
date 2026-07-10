package com.openclassrooms.projet5.dto;

import java.util.List;

public record PersonFromFireDto(String lastName, String phone, int age, List<String> medications, List<String> allergies) {
}
