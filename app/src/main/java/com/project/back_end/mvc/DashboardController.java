package com.project.back_end.mvc;

import com.project.back_end.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private TokenService authService;

    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        Map<String, Object> result = authService.validateToken(token, "admin");
        if (result.isEmpty()) {
            return "admin/adminDashboard"; // Will resolve to src/main/resources/templates/admin/adminDashboard.html
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        Map<String, Object> result = authService.validateToken(token, "doctor");
        if (result.isEmpty()) {
            return "doctor/doctorDashboard"; // Will resolve to src/main/resources/templates/doctor/doctorDashboard.html
        } else {
            return "redirect:/";
        }
    }
}
