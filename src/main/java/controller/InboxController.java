package controller;

import dao.AdminMessageDao;
import datatypes.messages.AdminMessage;
import enums.DaoType;
import manager.DaoManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.ServletContext;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class InboxController {
    private static final Logger logger = LogManager.getLogger(InboxController.class);
    @Autowired
    ServletContext context;
    @GetMapping("/inbox")
    public String loadPage(Model model) {
        DaoManager daoManager = (DaoManager) context.getAttribute("manager");
        AdminMessageDao adminMessageDao = daoManager.getDao(DaoType.AdminMessage);
        List<AdminMessage> notSeen = adminMessageDao.findAll().stream().filter(s->!s.isSeen()).collect(Collectors.toList());
        List<AdminMessage> seen = adminMessageDao.findAll().stream().filter(AdminMessage::isSeen).collect(Collectors.toList());

        notSeen.sort(Comparator.comparing(AdminMessage::getTime).reversed());
        seen.sort(Comparator.comparing(AdminMessage::getTime).reversed());

        model.addAttribute("seen", seen);
        model.addAttribute("notSeen", notSeen);
        return "inbox";
    }
}
