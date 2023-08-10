package com.basic.userapi.application;


import com.basic.userapi.client.MailgunClient;
import com.basic.userapi.client.mailgun.SendMailForm;
import com.basic.userapi.domain.SignUpForm;
import com.basic.userapi.domain.model.Customer;
import com.basic.userapi.exception.CustomException;
import com.basic.userapi.exception.ErrorCode;
import com.basic.userapi.service.SignUpCustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class SignUpApplication {
    private final MailgunClient mailgunClient;
    private final SignUpCustomerService signUpCustomerService;


    public void customerVerify(String email, String code) {
        signUpCustomerService.verifyEmail(email, code);
    }

    public String customerSignUp(SignUpForm form) {
        if (signUpCustomerService.isEmailExit(form.getEmail())) {
            //exception
            throw new CustomException(ErrorCode.ALREADY_REGISTER_USER);
        } else {
            Customer c = signUpCustomerService.signUp(form);

            String code = getRandomCode();

            SendMailForm sendMailForm = SendMailForm.builder()
                    .from("pagooo@naver.com")
                    .to("hyomyang@gmail.com")
                    .subject("Verification Email")
                    .text(getVerificationEmailBody(form.getEmail(), form.getName(), code))
                    .build();
            log.info("Send Email result : " + mailgunClient.sendMail(sendMailForm).getBody());
            signUpCustomerService.changeCustomerValidationEmail(c.getId(), code);
            return "회원 가입에 성공하였습니다.";
        }
    }

    private String getRandomCode() {

        return RandomStringUtils.random(10, true, true);
    }

    private String getVerificationEmailBody(String email, String name, String code) {
        StringBuilder builder = new StringBuilder();
        return builder.append("Hello").append(name).append("! Please Click Link for verification.\n\n")
                .append("http://localhost:8080/customer/signup/verifiy?email=")
                .append(email)
                .append("&code=")
                .append(code).toString();
    }


}

