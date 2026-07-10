package com.openclassrooms.projet5.dto;

import java.util.List;

public record FloodHousehold(String address, List<PersonFromFireDto> members) {
}
