package controller.admin;

import dao.ActivityDao;
import dao.UserDao;
import datatypes.server.Activity;
import enums.DaoType;
import manager.DaoManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.ServletContext;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class ActivityController {
    private static final Logger logger = LogManager.getLogger(ActivityController.class);
    @Autowired
    ServletContext context;
    @GetMapping("/activities")
    public String loadPage(Model model) {
        DaoManager daoManager = (DaoManager) context.getAttribute("manager");
        ActivityDao activityDao = daoManager.getDao(DaoType.Activity);
        UserDao userDao = daoManager.getDao(DaoType.User);
        List<Activity> activities = new ArrayList<>(activityDao.findAll());
        activities.sort(Comparator.comparing(Activity::getDateTime).reversed());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss MMM dd, yyyy");
        model.addAttribute("formatter", formatter);
        model.addAttribute("activities", activities);
        model.addAttribute("userDao", userDao);
        return "activities";
    }
}
