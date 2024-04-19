package kz.demo.bankApplication.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/user")
public class GetUserPageController {
    @GetMapping
    public String getUserApiPage() {
        return "userApiPage";
    }
    @GetMapping("/login")
    public String getUserLoginPage() {
        return "userLoginPage";
    }
}
