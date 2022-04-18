package com.endava.internship.mocking.service;

import com.endava.internship.mocking.model.Status;
import com.endava.internship.mocking.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class BasicValidationServiceTest {


    private BasicValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new BasicValidationService();
    }

    @Test
    void validateValidAmount() {
        //given
        final Double validAmount = 124d;

        //then
        assertDoesNotThrow(() -> validationService.validateAmount(validAmount),
                "This amount is valid because of it is positive and not null, test succeeds");
    }

    @Test
    void validateNullAmount() {
        //when
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validationService.validateAmount(null), "You can't put null values in this method");

        //then
        assertEquals("Amount must not be null", exception.getMessage());
    }

    @Test
    void validateInvalidAmount() {
        //given
        final Double invalidAmount = -124d;

        //when
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validationService.validateAmount(invalidAmount));

        //then
        assertEquals("Amount must be greater than 0", exception.getMessage(), "You can't put values < 0 in this method");
    }

    @Test
    void validatePaymentIdSucceeds() {
        //given
        final UUID validUUID = UUID.randomUUID();

        //then
        assertDoesNotThrow(() -> validationService.validatePaymentId(validUUID));
    }

    @Test
    void validatePaymentIdFails() {
        //when
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validationService.validatePaymentId(null));

        //then
        assertEquals("Payment id must not be null", exception.getMessage(), "You can't put null UUID in this method");
    }

    @Test
    void validateUserIdSucceeds() {
        //given
        final Integer validUserID = 6;

        //then
        validationService.validateUserId(validUserID);
    }

    @Test
    void validateUserIdFails() {
        //when
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validationService.validateUserId(null)
        );

        //then
        assertEquals("User id must not be null", exception.getMessage(),"User id must not be null");
    }

    @Test
    void validateUserSucceeds() {
        //given
        final User peteaUser = new User(0, "Petea", Status.ACTIVE);

        //then
        assertDoesNotThrow(() -> validationService.validateUser(peteaUser),
                "validateUser does not throws exceptions when user is valid");

    }

    @Test
    void validateUserFails() {
        //given
        final User inactiveUser = new User(0, "Petea", Status.INACTIVE);

        //when
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validationService.validateUser(inactiveUser));
        //then
        assertEquals("User with id " + inactiveUser.getId() + " not in ACTIVE status",
                exception.getMessage(),
                "User status should be active");

    }

    @Test
    void validateMessageFails() {
        //when
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validationService.validateMessage(null));

        //then
        assertEquals("Payment message must not be null", exception.getMessage(),"message should be not null");
    }

    @Test
    void validateMessageSucceeds() {
        //given
        final String STRING = "null";

        //then
        assertDoesNotThrow(() -> validationService.validateMessage(STRING));
    }
}