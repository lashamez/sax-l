package config;

import dao.AdminMessageDao;
import dao.UserDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import service.InboxService;
import service.UserService;

@Configuration
@ComponentScan("config")
public class ServiceConfig {
    private static final Logger logger = LogManager.getLogger(ServiceConfig.class);
    private AdminMessageDao adminMessageDao;
    private UserDao userDao;
    @Autowired
    public void setAdminMessageDao(AdminMessageDao adminMessageDao) {
        this.adminMessageDao = adminMessageDao;
    }

    @Bean
    public InboxService inboxService(){
        return new InboxService(adminMessageDao);
    }

    @Bean
    public UserService userService(){
        return new UserService(userDao);
    }

}
