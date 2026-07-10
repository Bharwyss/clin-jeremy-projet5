package com.openclassrooms.projet5.dto;

import java.util.List;

public record ChildAlertDto(List<ChildDto> children, List<HouseholdMemberDto> family) {}

