package controller.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
    private static final Logger logger = LogManager.getLogger(AdminController.class);

    @GetMapping("/admin")
    public String loadPage(Model model) {
        return "admin";
    }
}
