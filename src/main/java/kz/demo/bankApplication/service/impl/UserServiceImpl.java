package kz.demo.bankApplication.service.impl;

import kz.demo.bankApplication.config.JwtTokenProvider;
import kz.demo.bankApplication.dto.*;
import kz.demo.bankApplication.entity.RoleEntity;
import kz.demo.bankApplication.entity.UserEntity;
import kz.demo.bankApplication.repository.UserRepository;
import kz.demo.bankApplication.utils.AccountUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthorizationRequestDto createAccount(UserRequestDto userRequestDto) {
        /**
         * Сервис для добавления нового пользователя в ДБ
         */
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            AuthorizationRequestDto response = AuthorizationRequestDto.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .accessToken(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
            return response;
        }

        UserEntity newUserEntity = UserEntity.builder()
                .firstName(userRequestDto.getFirstName())
                .lastName(userRequestDto.getLastName())
                .gender(userRequestDto.getGender())
                .accountNumber(AccountUtils.generateIban())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequestDto.getEmail())
                .password(passwordEncoder.encode(userRequestDto.getPassword()))
                .phoneNumber(userRequestDto.getPhoneNumber())
                .status("ACTIVE")
                .role(RoleEntity.ROLE_USER)
                .enable(false)
                .build();

        UserEntity savedUserEntity = userRepository.save(newUserEntity);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userRequestDto.getEmail(),
                            userRequestDto.getPassword()
                    )
            );

            EmailDetailsDto emailDetailsDto = EmailDetailsDto.builder()
                    .recipient(savedUserEntity.getEmail())
                    .subject("Account creation")
                    .messageBody("Congratulations! Your Account has been successfully created. \n" +
                            "Your Account Details:\n" +
                            "Account Name: " + savedUserEntity.getFirstName() + " " + savedUserEntity.getLastName() + "\n" +
                            "Account Number: " + savedUserEntity.getAccountNumber())
                    .build();
            emailService.sendEmailAlert(emailDetailsDto);

            return AuthorizationRequestDto.builder()
                    .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS_CODE)
                    .accessToken(jwtTokenProvider.generateToken(authentication))
                    .accountInfo(AccountInfoDto.builder()
                            .accountName(savedUserEntity.getFirstName() + " " + savedUserEntity.getLastName())
                            .accountBalance(savedUserEntity.getAccountBalance())
                            .accountNumber(savedUserEntity.getAccountNumber())
                            .build())
                    .build();
        } catch (AuthenticationException e) {
            return AuthorizationRequestDto.builder()
                    .responseCode("Authentication Failed")
                    .accessToken("Failed to authenticate user after registration")
                    .build();
        }
    }

    @Override
    public AuthorizationRequestDto loginAccount(LoginAccountDto loginAccountDto) {
        Authentication authentication = null;

        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginAccountDto.getEmail(),
                        loginAccountDto.getPassword()
                )
        );
        EmailDetailsDto loginAlert = EmailDetailsDto.builder()
                .subject("You're logged in")
                .recipient(loginAccountDto.getEmail())
                .messageBody("You logged into your account.")
                .build();

        emailService.sendEmailAlert(loginAlert);
        return AuthorizationRequestDto.builder()
                .responseCode("Login Success")
                .accessToken(jwtTokenProvider.generateToken(authentication))
                .build();

    }

    @Override
    public BankResponseDto balanceEnquiry(EnquiryRequestDto request) {
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExists) {
            return BankResponseDto.builder()
                    .responseCode(AccountUtils.ACCOUNT_NUMBER_DOES_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NUMBER_DOES_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        UserEntity userEntity = userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponseDto.builder()
                .responseCode(AccountUtils.ACCOUNT_NUMBER_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_NUMBER_FOUND_MESSAGE)
                .accountInfo(AccountInfoDto.builder()
                        .accountBalance(userEntity.getAccountBalance())
                        .accountNumber(userEntity.getAccountNumber())
                        .accountName(userEntity.getFirstName() +" "+ userEntity.getLastName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequestDto request) {
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExists) {
            return AccountUtils.ACCOUNT_NUMBER_DOES_NOT_EXISTS_MESSAGE;
        }
        UserEntity userEntity = userRepository.findByAccountNumber(request.getAccountNumber());
        return userEntity.getFirstName() + " " + userEntity.getLastName();
    }

    @Override
    public BankResponseDto creditAccount(CreditDebitRequestDto request) {
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExists) {
            return BankResponseDto.builder()
                    .responseCode(AccountUtils.ACCOUNT_NUMBER_DOES_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NUMBER_DOES_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        UserEntity userEntityToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userEntityToCredit.setAccountBalance(userEntityToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userEntityToCredit);

        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(userEntityToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();

        transactionService.saveTransaction(transactionDto);

        return BankResponseDto.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfoDto.builder()
                        .accountBalance(userEntityToCredit.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(userEntityToCredit.getFirstName()+" "+ userEntityToCredit.getLastName())
                        .build())
                .build();

    }

    @Override
    public BankResponseDto debitAccount(CreditDebitRequestDto request) {
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExists) {
            return BankResponseDto.builder()
                    .responseCode(AccountUtils.ACCOUNT_NUMBER_DOES_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NUMBER_DOES_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        UserEntity userEntityToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        BigInteger avilableBalance = userEntityToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();

        if(avilableBalance.intValue() < debitAmount.intValue()) {
            return BankResponseDto.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        } else {
            userEntityToDebit.setAccountBalance(userEntityToDebit.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(userEntityToDebit);

            TransactionDto transactionDto = TransactionDto.builder()
                    .accountNumber(userEntityToDebit.getAccountNumber())
                    .transactionType("DEBIT")
                    .amount(request.getAmount())
                    .build();

            transactionService.saveTransaction(transactionDto);

            return BankResponseDto.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                    .accountInfo(AccountInfoDto.builder()
                            .accountNumber(request.getAccountNumber())
                            .accountName(userEntityToDebit.getFirstName()+" "+ userEntityToDebit.getLastName())
                            .accountBalance(userEntityToDebit.getAccountBalance())
                            .build())
                    .build();
        }
    }

    @Override
    public BankResponseDto transferAccount(TransferRequestDto request) {
        boolean isDestinationAccountExists = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());

        if(!isDestinationAccountExists) {
            return BankResponseDto.builder()
                    .responseCode(AccountUtils.DESTINATION_ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.DESTINATION_ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        UserEntity sourceAccount = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        UserEntity destinationAccount = userRepository.findByAccountNumber(request.getDestinationAccountNumber());

        if(request.getAmount().compareTo(sourceAccount.getAccountBalance()) > 0) {
            return BankResponseDto.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        sourceAccount.setAccountBalance(sourceAccount.getAccountBalance().subtract(request.getAmount()));
        destinationAccount.setAccountBalance(destinationAccount.getAccountBalance().add(request.getAmount()));
        String sourceUsername = sourceAccount.getFirstName()+ " "+ sourceAccount.getLastName();
        String destinationUsername = destinationAccount.getFirstName()+ " "+ destinationAccount.getLastName();
        EmailDetailsDto debitAlert = EmailDetailsDto.builder()
                .subject("DEBIT ALERT")
                .recipient(sourceAccount.getEmail())
                .messageBody("The sum of "+ request.getAmount() + " has been debited from your account to " + destinationUsername +
                        "\nYour current balance is " + sourceAccount.getAccountBalance())
                .build();
        userRepository.save(sourceAccount);
        EmailDetailsDto creditAlert = EmailDetailsDto.builder()
                .subject("CREDIT ALERT")
                .recipient(destinationAccount.getEmail())
                .messageBody("The sum of "+ request.getAmount() + " has been sent to your account from " + sourceUsername +
                        "\nYour current balance is " + destinationAccount.getAccountBalance())
                .build();
        userRepository.save(destinationAccount);

        emailService.sendEmailAlert(debitAlert);
        emailService.sendEmailAlert(creditAlert);

        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(destinationAccount.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();

        transactionService.saveTransaction(transactionDto);

        return BankResponseDto.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESS_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESS_MESSAGE)
                .accountInfo(null)
                .build();
    }
}
