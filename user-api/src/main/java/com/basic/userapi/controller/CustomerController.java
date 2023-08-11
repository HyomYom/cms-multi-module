package com.basic.userapi.controller;


import com.basic.domain.common.UserVo;
import com.basic.domain.config.JwtAuthenticationProvider;
import com.basic.userapi.domain.customer.CustomerDto;
import com.basic.userapi.domain.model.Customer;
import com.basic.userapi.exception.CustomException;
import com.basic.userapi.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.basic.userapi.exception.ErrorCode.*;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final JwtAuthenticationProvider provider;
    private final CustomerService customerService;

    @GetMapping("/getInfo")
    public ResponseEntity<CustomerDto> getInfo(@RequestHeader(name = "X-AUTH-TOKEN") String token) {
        UserVo vo = provider.getUserVo(token);

        Customer c = customerService.findByIdAndEmail(vo.getId(), vo.getEmail()).orElseThrow(
                () -> new CustomException(NOT_FOUND_USER)
        );

        return ResponseEntity.ok(CustomerDto.from(c));
    }
}
