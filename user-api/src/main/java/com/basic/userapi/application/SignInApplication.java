package com.basic.userapi.application;

import com.basic.userapi.domain.SignInForm;
import com.basic.userapi.exception.CustomException;
import com.basic.userapi.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.basic.userapi.exception.ErrorCode.LOGIN_CHECK_FAIL;


@Service
@RequiredArgsConstructor
public class SignInApplication {
    private final CustomerService customerService;
    public String customerLoginToken(SignInForm form){
        //1. 로그인 가능 여부
        customerService.findValidCustomer(form.getEmail(), form.getPassword())
                .orElseThrow(() -> new CustomException(LOGIN_CHECK_FAIL));
        //2. 토큰을 발행하고
        //3. 토큰을 response한다.
         return "";
    }
}
