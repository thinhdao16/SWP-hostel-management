package com.example.hostelmanage.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('user') or hasRole('admin')")
    public String userAccess() {
        return "User Content.";
    }


    @GetMapping("/admin")
    @PreAuthorize("hasRole('admin')")
    public String adminAccess() {
        return "Admin Board.";
    }
}
