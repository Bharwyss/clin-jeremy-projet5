package com.openclassrooms.projet5.dto;

import java.util.List;


public record FireStationCoverageDto(List<PersonFromStationDto> persons,
                                     int adultCount,
                                     int childCount) {
}
