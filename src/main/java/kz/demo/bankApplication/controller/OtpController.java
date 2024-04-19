package kz.demo.bankApplication.controller;

import kz.demo.bankApplication.dto.BankResponseDto;
import kz.demo.bankApplication.dto.OtpRequestDto;
import kz.demo.bankApplication.dto.OtpValidationRequestDto;
import kz.demo.bankApplication.service.impl.OtpServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
@AllArgsConstructor
public class OtpController {

    private final OtpServiceImpl otpService;

    @PostMapping("otpSend")
    public BankResponseDto sendOtp(@RequestBody OtpRequestDto otpRequest) {
        return otpService.sendOtp(otpRequest);
    }
    @PostMapping("otpValidate")
    public BankResponseDto validateOtp(@RequestBody OtpValidationRequestDto otpValidationRequest) {
        return otpService.validateOtp(otpValidationRequest);
    }
}
