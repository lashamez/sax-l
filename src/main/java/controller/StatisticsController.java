package controller;

import dao.AnnouncementDao;
import dao.UserAchievementDao;
import dao.UserDao;
import enums.DaoType;
import manager.DaoManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

@Controller
public class StatisticsController {
    private static final Logger logger = LogManager.getLogger(StatisticsController.class);
    @Autowired
    ServletContext context;
    @GetMapping("/statistics")
    public String loadPage(Model model) {
        DaoManager manager = (DaoManager) context.getAttribute("manager");
        Map<String, Integer> stats = new HashMap<>();

        UserDao userDao = manager.getDao(DaoType.User);
        stats.put("Users", userDao.findAll().size());

        AnnouncementDao announcementDao = manager.getDao(DaoType.Announcement);
        stats.put("Announcements", announcementDao.findAll().size());

        UserAchievementDao achievementDao = manager.getDao(DaoType.UserAchievement);
        stats.put("Achievements", achievementDao.findAll().size());
        model.addAttribute("stats", stats);
        return "statistics";
    }
}
