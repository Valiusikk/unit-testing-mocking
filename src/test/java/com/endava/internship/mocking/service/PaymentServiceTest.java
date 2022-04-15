package com.endava.internship.mocking.service;

import com.endava.internship.mocking.model.Payment;
import com.endava.internship.mocking.model.Status;
import com.endava.internship.mocking.model.User;
import com.endava.internship.mocking.repository.PaymentRepository;
import com.endava.internship.mocking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    private  static final Payment PAYMENT = new Payment(1,258d,"Initial payment");

    private static final User PETEA_USER = new User(0, "Petea", Status.ACTIVE);

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(userRepository, paymentRepository, validationService);
    }

    @Test
    void ShouldCreatePaymentAndSaveInRepositoryAndReturnIt() {
        //given
        ArgumentCaptor<Payment> argumentCaptor = ArgumentCaptor.forClass(Payment.class);

        //when
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(PETEA_USER));
        paymentService.createPayment(PETEA_USER.getId(), 251d);
        verify(validationService,times(1)).validateUserId(PETEA_USER.getId());
        verify(validationService,times(1)).validateAmount(251d);
        verify(userRepository,times(1)).findById(PETEA_USER.getId());
        verify(validationService,times(1)).validateUser(PETEA_USER);
        verify(paymentRepository,times(1)).save(argumentCaptor.capture());

        //then
        assertEquals(argumentCaptor.getValue().getUserId(), PETEA_USER.getId());
        assertEquals(argumentCaptor.getValue().getAmount(), 251d);
        assertTrue(argumentCaptor.getValue().getMessage().contains(PETEA_USER.getName()));

    }

    @Test
    void editMessageSucceeds() {
        //when
        when(paymentRepository.editMessage(any(),any())).thenReturn(PAYMENT);
       Payment expectedPayment= paymentService.editPaymentMessage(PAYMENT.getPaymentId(),PAYMENT.getMessage());
        //then
        verify(validationService,times(1)).validatePaymentId(PAYMENT.getPaymentId());
        verify(validationService,times(1)).validateMessage(PAYMENT.getMessage());
        verify(paymentRepository,times(1)).editMessage(PAYMENT.getPaymentId(),PAYMENT.getMessage());

        assertEquals(PAYMENT,expectedPayment,"Payment Repository Mock should return the same PAYMENT");
    }

    @Test
    void getAllByAmountExceeding() {
        //given
        final  double AMOUNT = 562.7;
        final List<Payment> ALL_PAYMENTS = Arrays.asList(
                new Payment(1,1204d,"PAYMENT_1"),
                new Payment(2,120d,"PAYMENT_2"),
                new Payment(3,104d,"PAYMENT_3"),
                new Payment(4,124804d,"PAYMENT_4")
        );
        final List<Payment> EXPECTED_PAYMENTS = new ArrayList<>();
        EXPECTED_PAYMENTS.add(ALL_PAYMENTS.get(0));
        EXPECTED_PAYMENTS.add(ALL_PAYMENTS.get(3));

        //when
        when(paymentRepository.findAll()).thenReturn(ALL_PAYMENTS);

        //then
        assertEquals(EXPECTED_PAYMENTS,
                        paymentService.getAllByAmountExceeding(AMOUNT),
                "There are only 2 appropriate payments");
        verify(paymentRepository,times(1)).findAll();
    }
}
