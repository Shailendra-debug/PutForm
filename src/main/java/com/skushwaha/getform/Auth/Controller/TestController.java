package com.skushwaha.getform.Auth.Controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public String publicTest() {
        return "Public endpoint working";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public String userTest() {
        return "USER role working";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String adminTest() {
        return "ADMIN role working";
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/both")
    public String bothTest() {
        return "USER or ADMIN role working";
    }
}
