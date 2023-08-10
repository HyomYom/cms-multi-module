package com.basic.userapi.client;


import com.basic.userapi.client.mailgun.SendMailForm;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;


@FeignClient(name = "mailgun", url = "https://api.mailgun.net/v3/")
@Qualifier
public interface MailgunClient {
    @PostMapping("sandboxa70a18c149924e89a4151da778ed535c.mailgun.org/messages")
    ResponseEntity<String> sendMail(@SpringQueryMap SendMailForm form);
}
