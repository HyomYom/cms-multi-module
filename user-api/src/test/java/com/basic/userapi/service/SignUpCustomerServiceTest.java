package com.basic.userapi.service;

import com.basic.userapi.domain.SignUpForm;
import com.basic.userapi.domain.model.Customer;
import com.basic.userapi.service.customer.SignUpCustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.time.LocalDate;


@SpringBootTest
class SignUpCustomerServiceTest {
    @Autowired
    private SignUpCustomerService service;

    @Test
    void signUp() {
        SignUpForm form = SignUpForm.builder()
                .name("name")
                .birth(LocalDate.now())
                .email("abc@gmail.com")
                .password("1")
                .phone("01000000000")
                .build();

        Customer c = service.signUp(form);
        Assert.isTrue(service.signUp(form).getId() != null);
    }

}