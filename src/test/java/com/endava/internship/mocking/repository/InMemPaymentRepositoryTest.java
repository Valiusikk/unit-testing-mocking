package com.endava.internship.mocking.repository;

import com.endava.internship.mocking.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class InMemPaymentRepositoryTest {

    private InMemPaymentRepository paymentRepository;

    private static final Payment TEST_PAYMENT = new Payment(1, 258d, "Initial payment");

    private static final Payment PAYMENT_DOES_NOT_EXISTS = new Payment(1, 258d, "Initial payment");

    @BeforeEach
    void setUp() {
        paymentRepository = new InMemPaymentRepository();
    }

    @Test
    void findByIdOfSucceds() {
        //given
        paymentRepository.save(TEST_PAYMENT);

        //when
        final Optional<Payment> actualEmptyPayment = paymentRepository.findById(PAYMENT_DOES_NOT_EXISTS.getPaymentId());
        final Optional<Payment> actualPayment = paymentRepository.findById(TEST_PAYMENT.getPaymentId());

        //then
        assertEquals(Optional.empty(), actualEmptyPayment,
                "In case when user tries to find Payment by id that does not exists, method should return empty Optional");
        assertEquals(Optional.of(TEST_PAYMENT), actualPayment,
                "Method returns valid payment if it's id is already in repository");
    }

    @Test
    void findByIdOfFails() {
        //when
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> paymentRepository.findById(null));
        //then
        assertEquals("Payment id must not be null",
                exception.getMessage(),
                "findById throws exception with following message : Payment id must not be null");
    }

    @Test
    void saveNullPaymentShouldThrow() {

        //when
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> paymentRepository.save(null), "Payment id must not be null");

        //then
        assertEquals("Payment must not be null",
                exception.getMessage(),
                "In case when User is going to save null reference, method throws IllegalArgumentException");
    }

    @Test
    void saveValidPaymentSucceeds() {

        //when
        final Payment expectedPayment = paymentRepository.save(TEST_PAYMENT);

        //then
        assertEquals(expectedPayment, TEST_PAYMENT);
    }

    @Test
    void editMessageShouldThrow() {
        //given
        final UUID test_uuid = UUID.randomUUID();
        final String message = "MESSGAE";

        //when
        final NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> paymentRepository.editMessage(test_uuid, message));

        //then
        assertEquals("Payment with id " + test_uuid + " not found", exception.getMessage());
    }

    @Test
    void editMessageSucceeds() {
        //given
        paymentRepository.save(TEST_PAYMENT);

        //when
        final Payment expectedPayment = paymentRepository.editMessage(TEST_PAYMENT.getPaymentId(), TEST_PAYMENT.getMessage());

        //then
        assertEquals(expectedPayment, TEST_PAYMENT, "editMessage method should return payment with old message value");
    }

}