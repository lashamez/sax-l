package config;

import dao.*;
import dao.impl.*;
import manager.DaoManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
@Configuration
public class DaoConfig implements WebMvcConfigurer {
    private static final Logger logger = LogManager.getLogger(DaoConfig.class);

    @Bean
    public DaoManager daoManager() {
        return new DaoManager();
    }

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(64);
    }

    @Bean
    public AdminMessageDao adminMessageDao() {
        return new AdminMessageDaoImpl(new Cao<>());
    }

    @Bean
    public AdminReplyMessageDao adminReplyMessageDao() {
        return new AdminReplyMessageDaoImpl(new Cao<>());
    }

    @Bean
    public AnnouncementDao announcementDao() {
        return new AnnouncementDaoImpl(new Cao<>());
    }

    @Bean
    public FriendRequestDao friendRequestDao() {
        return new FriendRequestDaoImpl(new Cao<>());
    }

    @Bean
    public TextMessageDao textMessageDao() {
        return new TextMessageDaoImpl(new Cao<>());
    }

    @Bean
    public ToastDao toastDao() {
        return new ToastDaoImpl(new Cao<>());
    }

    @Bean
    public UserAchievementDao userAchievementDao() {
        return new UserAchievementDaoImpl(new Cao<>());
    }

    @Bean
    public UserDao userDao() {
        return new UserDaoImpl(new Cao<>());
    }

    @Bean
    public ActivityDao activityDao() {
        return new ActivityDaoImpl(threadPoolExecutor(), new Cao<>());
    }

}
