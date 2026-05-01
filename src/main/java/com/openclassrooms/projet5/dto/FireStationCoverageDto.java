package com.openclassrooms.projet5.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FireStationCoverageDto {
    List<PersonFromStationDto> persons;
    int adultCount;
    int childCount;

}
