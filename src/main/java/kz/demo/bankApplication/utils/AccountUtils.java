package kz.demo.bankApplication.utils;

import java.math.BigInteger;
import java.time.Year;
import java.util.Random;

public class AccountUtils {
    public static  final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = String.format("This email is already exists");

    public static final String ACCOUNT_CREATION_SUCCESS_CODE = "002";
    public static final String ACCOUNT_CREATION_SUCCESS_MESSAGE = "Account has been successfully created";

    public static final String ACCOUNT_NUMBER_DOES_NOT_EXISTS_CODE = "003";
    public static final String ACCOUNT_NUMBER_DOES_NOT_EXISTS_MESSAGE = "User with the provided Account Number does not exist";
    public static final String ACCOUNT_NUMBER_FOUND_CODE = "004";
    public static final String ACCOUNT_NUMBER_FOUND_MESSAGE = "User Account Found";

    public static final String ACCOUNT_CREDITED_SUCCESS_CODE = "005";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "User Account credited success";
    public static final String INSUFFICIENT_BALANCE_CODE = "006";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient balance";
    public static final String ACCOUNT_DEBITED_SUCCESS_CODE = "007";
    public static final String ACCOUNT_DEBITED_SUCCESS_MESSAGE = "Account has been debited succesfully";

    public static final String DESTINATION_ACCOUNT_NOT_EXIST_CODE = "008";
    public static final String DESTINATION_ACCOUNT_NOT_EXIST_MESSAGE = "Destination account is not exist";

    public static final String TRANSFER_SUCCESS_CODE = "009";
    public static final String TRANSFER_SUCCESS_MESSAGE = "Transfer successfully ended";

    public static final String OTP_ERROR_CODE = "010";
    public static final String OTP_ERROR_MESSAGE = "OTP Error: You have not sent an otp";

    public static final String OTP_CODE_SEND_CODE = "011";
    public static final String OTP_CODE_SEND_MESSAGE = "OTP code send to this email: ";

    public static final String OTP_EXPIRED_CODE = "012";
    public static final String OTP_EXPIRED_MESSAGE = "OTP is already expired";

    public static final String OTP_NOT_EQUAL_CODE = "013";
    public static final String OTP_NOT_EQUAL_MESSAGE = "OTP is not equal to another one";

    public static final String OTP_IS_CORRECT_CODE = "014";
    public static final String OTP_IS_CORRECT_MESSAGE= "OTP is already correct";
    public static String generateOtp() {
        final int OTP_LENGTH = 6;

        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    public static String generateIban() {
        final String KZ_IBAN_PREFIX = "KZ";
        final int KZ_IBAN_LENGTH = 20;

        Random random = new Random();
        StringBuilder bankAccountNumber = new StringBuilder();
        for (int i = 0; i < 14; i++) {
            bankAccountNumber.append(random.nextInt(10));
        }

        StringBuilder ibanBuilder = new StringBuilder(KZ_IBAN_PREFIX);
        ibanBuilder.append("00");
        ibanBuilder.append(bankAccountNumber);

        BigInteger ibanNumber = new BigInteger(ibanBuilder.toString().substring(4));
        BigInteger remainder = ibanNumber.mod(new BigInteger("97"));
        int checksum = 98 - remainder.intValue();

        ibanBuilder.insert(2, String.format("%02d", checksum));

        return ibanBuilder.toString();
    }
    String iban = generateIban();
}
