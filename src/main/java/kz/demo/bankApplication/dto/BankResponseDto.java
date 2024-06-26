package kz.demo.bankApplication.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankResponseDto {
    private String responseCode;
    private String responseMessage;
    private AccountInfoDto accountInfo;
    private OtpResponseDto otpResponse;
}
