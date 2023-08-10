package com.basic.userapi.client.service;

import com.basic.userapi.client.MailgunClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class EmailSendServiceTest {

    @Autowired
    private MailgunClient mailgunClient;

    @Test
    public void EmailTest(){
        // need test code
        mailgunClient.sendMail(null);

//       String response = emailSendService.sendEmail();
//        System.out.println(response);

    }

}