package com.endava.internship.mocking.service;

import com.endava.internship.mocking.model.Payment;
import com.endava.internship.mocking.model.Status;
import com.endava.internship.mocking.model.User;
import com.endava.internship.mocking.repository.PaymentRepository;
import com.endava.internship.mocking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    private static final Payment PAYMENT = new Payment(1, 258d, "Initial payment");

    private static final User PETEA_USER = new User(0, "Petea", Status.ACTIVE);

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private PaymentService paymentService;

    @Captor
    private ArgumentCaptor<Payment> argumentCaptor;

    @Test
    void ShouldCreatePayment() {
        //given
        when(userRepository.findById(PETEA_USER.getId())).thenReturn(Optional.of(PETEA_USER));

        //when
        paymentService.createPayment(PETEA_USER.getId(), 251d);
        verify(validationService).validateUserId(PETEA_USER.getId());
        verify(validationService).validateAmount(251d);
        verify(userRepository).findById(PETEA_USER.getId());
        verify(validationService).validateUser(PETEA_USER);
        verify(paymentRepository).save(argumentCaptor.capture());

        //then
        assertEquals(PETEA_USER.getId(), argumentCaptor.getValue().getUserId(),
                "payment should be processed only for id's of saved users");
        assertEquals(251d, argumentCaptor.getValue().getAmount(),
                "Amount should e equal to amount of payment was made before");
        assertTrue(argumentCaptor.getValue().getMessage().contains(PETEA_USER.getName()));

    }

    @Test
    void editMessageSucceeds() {
        //given
        when(paymentRepository.editMessage(PAYMENT.getPaymentId(), PAYMENT.getMessage())).thenReturn(PAYMENT);

        //when
        final Payment expectedPayment = paymentService.editPaymentMessage(PAYMENT.getPaymentId(), PAYMENT.getMessage());

        //then
        verify(validationService).validatePaymentId(PAYMENT.getPaymentId());
        verify(validationService).validateMessage(PAYMENT.getMessage());
        verify(paymentRepository).editMessage(PAYMENT.getPaymentId(), PAYMENT.getMessage());

        assertEquals(PAYMENT, expectedPayment, "Payment Repository Mock should return the same PAYMENT");
    }

    @Test
    void getAllByAmountExceeding() {
        //given
        final double AMOUNT = 562.7;
        final List<Payment> allPayments = Arrays.asList(
                new Payment(1, 1204d, "PAYMENT_1"),
                new Payment(2, 120d, "PAYMENT_2"),
                new Payment(3, 104d, "PAYMENT_3"),
                new Payment(4, 124804d, "PAYMENT_4")
        );
        final List<Payment> expectedPayments = new ArrayList<>();
        expectedPayments.add(allPayments.get(0));
        expectedPayments.add(allPayments.get(3));
        when(paymentRepository.findAll()).thenReturn(allPayments);

        //when
        final List<Payment> actualPayments= paymentService.getAllByAmountExceeding(AMOUNT);

        //then
        assertEquals(expectedPayments,actualPayments,"There are only 2 appropriate payments");
        verify(paymentRepository).findAll();
    }
}
