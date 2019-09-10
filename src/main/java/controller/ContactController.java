package controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContactController {
    private static final Logger logger = LogManager.getLogger(ContactController.class);

    @GetMapping("/contact")
    public String loadPage(Model model) {
        return "contact";
    }
}
