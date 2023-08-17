package com.basic.userapi.controller;


import com.basic.domain.common.UserVo;
import com.basic.domain.config.JwtAuthenticationProvider;
import com.basic.userapi.domain.customer.ChangeBalanceForm;
import com.basic.userapi.domain.customer.CustomerDto;
import com.basic.userapi.domain.model.Customer;
import com.basic.userapi.exception.CustomException;
import com.basic.userapi.service.customer.CustomerBalanceService;
import com.basic.userapi.service.customer.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.basic.userapi.exception.ErrorCode.NOT_FOUND_USER;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final JwtAuthenticationProvider provider;
    private final CustomerService customerService;
    private final CustomerBalanceService customerBalanceService;

    @GetMapping("/getInfo")
    public ResponseEntity<CustomerDto> getInfo(@RequestHeader(name = "X-AUTH-TOKEN") String token) {
        UserVo vo = provider.getUserVo(token);

        Customer c = customerService.findByIdAndEmail(vo.getId(), vo.getEmail()).orElseThrow(
                () -> new CustomException(NOT_FOUND_USER)
        );

        return ResponseEntity.ok(CustomerDto.from(c));
    }

    @PostMapping("/balance")
    public ResponseEntity<Integer> changeBalance(@RequestHeader(name = "X-AUTH-TOKEN") String token
            , @RequestBody ChangeBalanceForm form
    ) {
        UserVo vo = provider.getUserVo(token);
        return ResponseEntity.ok(customerBalanceService.changeBalance(vo.getId(), form).getCurrentMoney());
    }
}
