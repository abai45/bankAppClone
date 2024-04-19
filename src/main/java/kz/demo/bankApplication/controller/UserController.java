package kz.demo.bankApplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.demo.bankApplication.dto.*;
import kz.demo.bankApplication.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Account Management API's")
public class UserController {
    @Autowired
    UserService userService;

    @Operation(
            summary = "Create New User Account",
            description = "Creating a new user and assigning an account ID"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Http Status 201 CREATED"
    )
    @PostMapping
    public AuthorizationRequestDto createAccount(@RequestBody UserRequestDto userRequestDto) {
        return userService.createAccount(userRequestDto);
    }

    @PostMapping("/login")
    public AuthorizationRequestDto login(@RequestBody LoginAccountDto loginAccountDto) {
        return userService.loginAccount(loginAccountDto);
    }
    @Operation(
            summary = "Balance Enquiry",
            description = "Given an account number and his account balance"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @GetMapping("balanceEnquiry")
    public BankResponseDto bankResponseDto(@RequestBody EnquiryRequestDto request) {
        return userService.balanceEnquiry(request);
    }

    @Operation(
            summary = "Name Enquiry",
            description = "Checking Account Name by account number"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @GetMapping("nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequestDto request) {
        return userService.nameEnquiry(request);
    }

    @Operation(
            summary = "Credit operation +",
            description = "Replenishment of client account"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @PostMapping("credit")
    public BankResponseDto creditAccount(@RequestBody CreditDebitRequestDto request) {
        return userService.creditAccount(request);
    }

    @Operation(
            summary = "Debit operation -",
            description = "Replenishment of client account"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @PostMapping("debit")
    public BankResponseDto debitAccount(@RequestBody CreditDebitRequestDto request) {
        return userService.debitAccount(request);
    }
    @Operation(
            summary = "Transfer operation between two clients",
            description = "Replenishment of client account"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @PostMapping("transfer")
    public BankResponseDto transferAccount(@RequestBody TransferRequestDto request) {
        return userService.transferAccount(request);
    }
}
