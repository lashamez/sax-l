package controller.admin;

import datatypes.messages.AdminMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import service.InboxService;

import java.util.Comparator;
import java.util.List;

@Controller
@DependsOn("inboxService")
public class InboxController {
    private static final Logger logger = LogManager.getLogger(InboxController.class);
    private InboxService inboxService;

    public InboxController(InboxService inboxService) {
        this.inboxService = inboxService;
    }

    @GetMapping("/inbox")
    public String loadPage(Model model) {
        List<AdminMessage> notSeen = inboxService.getNotSeenMessages();
        List<AdminMessage> seen = inboxService.getSeenMessages();

        notSeen.sort(Comparator.comparing(AdminMessage::getTime).reversed());
        seen.sort(Comparator.comparing(AdminMessage::getTime).reversed());

        model.addAttribute("seen", seen);
        model.addAttribute("notSeen", notSeen);
        return "inbox";
    }
}
