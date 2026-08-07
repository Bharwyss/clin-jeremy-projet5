package com.openclassrooms.projet5.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AgeCalculatorTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private static String format(LocalDate date) {
        return date.format(FORMATTER);
    }

    // ----------------------------------------------------------------
    // Calcul nominal
    // ----------------------------------------------------------------

    @Test
    @DisplayName("calculateAge retourne l'âge d'une personne née il y a plusieurs années")
    void calculateAge_shouldReturnCorrectAge() {
        // GIVEN
        String birthdate = format(LocalDate.now().minusYears(40).minusDays(10));

        // WHEN
        int age = AgeCalculator.calculateAge(birthdate);

        // THEN
        assertEquals(40, age);
    }

    @Test
    @DisplayName("calculateAge retourne 0 pour une personne née aujourd'hui")
    void calculateAge_shouldReturnZeroForNewborn() {
        // GIVEN
        String birthdate = format(LocalDate.now());

        // WHEN
        int age = AgeCalculator.calculateAge(birthdate);

        // THEN
        assertEquals(0, age);
    }

    // ----------------------------------------------------------------
    // Frontières autour de l'anniversaire
    // ----------------------------------------------------------------

    @Test
    @DisplayName("calculateAge retourne l'âge plein le jour même de l'anniversaire")
    void calculateAge_shouldReturnFullAgeOnBirthday() {
        // GIVEN
        String birthdate = format(LocalDate.now().minusYears(30));

        // WHEN
        int age = AgeCalculator.calculateAge(birthdate);

        // THEN
        assertEquals(30, age);
    }

    @Test
    @DisplayName("calculateAge retourne l'âge plein le lendemain de l'anniversaire")
    void calculateAge_shouldReturnFullAgeTheDayAfterBirthday() {
        // GIVEN
        String birthdate = format(LocalDate.now().minusYears(30).minusDays(1));

        // WHEN
        int age = AgeCalculator.calculateAge(birthdate);

        // THEN
        assertEquals(30, age);
    }

    @Test
    @DisplayName("calculateAge retourne l'âge moins un la veille de l'anniversaire")
    void calculateAge_shouldReturnAgeMinusOneBeforeBirthday() {
        // GIVEN - l'anniversaire tombe demain, il n'a donc pas encore eu lieu
        String birthdate = format(LocalDate.now().minusYears(30).plusDays(1));

        // WHEN
        int age = AgeCalculator.calculateAge(birthdate);

        // THEN
        assertEquals(29, age);
    }

    // ----------------------------------------------------------------
    // Format de date
    // ----------------------------------------------------------------

    @Test
    @DisplayName("calculateAge interprète la date au format américain MM/dd/yyyy")
    void calculateAge_shouldParseAmericanDateFormat() {
        // GIVEN - le 3 juin 2000, et non le 6 mars
        LocalDate reference = LocalDate.of(2000, 6, 3);
        int expected = java.time.Period.between(reference, LocalDate.now()).getYears();

        // WHEN
        int age = AgeCalculator.calculateAge("06/03/2000");

        // THEN
        assertEquals(expected, age);
    }

    @Test
    @DisplayName("calculateAge lève une exception pour une date au format jour/mois/année")
    void calculateAge_shouldThrowForDayMonthYearFormat() {
        // WHEN / THEN - 25 ne peut pas être un mois
        assertThrows(DateTimeParseException.class,
                () -> AgeCalculator.calculateAge("25/12/2000"));
    }

    @Test
    @DisplayName("calculateAge lève une exception pour une date illisible")
    void calculateAge_shouldThrowForUnparsableDate() {
        // WHEN / THEN
        assertThrows(DateTimeParseException.class,
                () -> AgeCalculator.calculateAge("pas une date"));
    }
}