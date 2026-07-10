package com.openclassrooms.projet5.dto;

import java.util.List;

public record FireDto(List<PersonFromFireDto> personFromFireDtoList, String numberStation) {
}
