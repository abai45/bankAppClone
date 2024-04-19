package kz.demo.bankApplication.service.impl;

import kz.demo.bankApplication.dto.*;
import kz.demo.bankApplication.entity.OtpEntity;
import kz.demo.bankApplication.repository.OtpRepository;
import kz.demo.bankApplication.utils.AccountUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class OtpServiceImpl {
    private final OtpRepository otpRepository;
    private final EmailService emailService;

    public BankResponseDto sendOtp(OtpRequestDto request) {
        OtpEntity existingOtp = otpRepository.findByEmail(request.getEmail());
        if(existingOtp != null) {
            otpRepository.delete(existingOtp);
        }
        String otp = AccountUtils.generateOtp();
        log.info("Sending OTP : {}", otp);

        otpRepository.save(OtpEntity.builder()
                        .email(request.getEmail())
                        .otp(otp)
                        .expiresAt(LocalDateTime.now().plusMinutes(1))
                .build());

        emailService.sendEmailAlert(EmailDetailsDto.builder()
                .subject("Private information")
                .recipient(request.getEmail())
                .messageBody("Our bank has sent a new OTP. This OTP expires in 1 minute " + otp)
                .build());

        return BankResponseDto.builder()
                .responseCode(AccountUtils.OTP_CODE_SEND_CODE)
                .responseMessage(AccountUtils.OTP_CODE_SEND_MESSAGE + request.getEmail())
                .build();
    }

    public BankResponseDto validateOtp(OtpValidationRequestDto otpValidationRequest) {

        OtpEntity otp = otpRepository.findByEmail(otpValidationRequest.getEmail());

        log.info("Validating OTP : {}", otpValidationRequest.getEmail());

        if (otp == null) {
            return BankResponseDto.builder()
                    .responseCode(AccountUtils.OTP_ERROR_CODE)
                    .responseMessage(AccountUtils.OTP_ERROR_MESSAGE)
                    .build();
        }
        if(otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            return BankResponseDto.builder()
                    .responseCode(AccountUtils.OTP_EXPIRED_CODE)
                    .responseMessage(AccountUtils.OTP_EXPIRED_MESSAGE)
                    .build();
        }
        if(!otp.getOtp().equals(otpValidationRequest.getOtp())) {
            return BankResponseDto.builder()
                    .responseCode(AccountUtils.OTP_NOT_EQUAL_CODE)
                    .responseMessage(AccountUtils.OTP_NOT_EQUAL_MESSAGE)
                    .build();
        }
        return BankResponseDto.builder()
                .responseCode(AccountUtils.OTP_IS_CORRECT_CODE)
                .responseMessage(AccountUtils.OTP_IS_CORRECT_MESSAGE)
                .otpResponse(OtpResponseDto.builder()
                        .isOtpValid(true)
                        .build())
                .build();
    }
}
