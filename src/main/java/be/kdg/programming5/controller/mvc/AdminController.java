package be.kdg.programming5.controller.mvc;

import be.kdg.programming5.config.security.AdminOnly;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin")
    @AdminOnly
    public String adminPage() {
        return "admin";
    }
}

