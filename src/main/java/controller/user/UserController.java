package controller.user;

import datatypes.promise.Promise;
import datatypes.user.User;
import enums.FormFields;
import enums.Level;
import enums.UserType;
import mail.PasswordRecovery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import security.Cracker;
import service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

@Controller
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);
    private UserService userService;
    private static final int PASSWORD_LENGTH = 12;
    private final Random random = new Random();

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/createUser")
    public String createUser(HttpServletRequest request) {
        String userName = request.getParameter(FormFields.username.getValue());
        String password = request.getParameter(FormFields.password.getValue());
        String confirmPassword = request.getParameter(FormFields.confirmpassword.getValue());
        String firstName = request.getParameter(FormFields.firstname.getValue());
        String lastName = request.getParameter(FormFields.lastname.getValue());
        String mail = request.getParameter(FormFields.mail.getValue());
        UserType userType = request.getParameter("usertype").equals("admin")? UserType.Admin:UserType.User;
        if (!password.equals(confirmPassword)) {
            request.getSession().setAttribute("error", "Passwords don't match");
            logger.error("Passwords don't match");
            return "users-list";
        }
        if (userService.findByUserName(userName) != null) {
            request.getSession().setAttribute("error", "Username is already taken");
            logger.error("Username is already taken, {}", userName);
            return "users-list";
        }

        User user = new User(userName, Cracker.code(password), firstName, lastName, mail);
        user.setUserType(userType);
        Promise promise = userService.save(user);
        request.getSession().setAttribute(promise.getLevel().getValue(), promise.getText());
        return "users-list";
    }

    @PostMapping("/deleteUser")
    public String deleteUser(HttpServletRequest request) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        int deleteUserId = Integer.parseInt(request.getParameter("deleteUserId"));
        User deleteUser = userService.findById(deleteUserId);

        if (user.getId() == deleteUserId) {
            request.getSession().setAttribute("error", "You can't delete yourself");
            return "users-list";
        }
        if (user.getId() != deleteUserId)  {
            Promise promise = userService.delete(deleteUser);
            request.getSession().setAttribute(promise.getLevel().getValue(), promise.getText());
        }
        return "users-list";
    }
    @PostMapping("/editFromAdmin")
    public String editFromAdmin(HttpServletRequest request) throws ServletException, IOException {
        String password = request.getParameter(FormFields.password.getValue());
        String confirmPassword = request.getParameter(FormFields.confirmpassword.getValue());
        String firstName = request.getParameter(FormFields.firstname.getValue());
        String lastName = request.getParameter(FormFields.lastname.getValue());
        UserType userType = request.getParameter("usertype").equals("admin")? UserType.Admin:UserType.User;
        int hiddenId = Integer.parseInt(request.getParameter("hiddenId"));
        User editedUser = userService.findById(hiddenId);
        if (!password.equals(confirmPassword)){
            request.getSession().setAttribute("error", "Passwords don't match");
            return "users-list";
        }
        editedUser.setUserType(userType);
        editedUser.setPassword(Cracker.code(password));
        editedUser.setFirstName(firstName);
        editedUser.setLastName(lastName);
        Promise promise = userService.save(editedUser);
        request.getSession().setAttribute(promise.getLevel().getValue(), promise.getText());
        return "users-list";
    }
    @PostMapping("/forgot")
    public String forgot(HttpServletRequest request) throws ServletException, IOException {
        String userName = request.getParameter("username");
        User user = userService.findByUserName(userName);
        if (user == null) {
            request.getSession().setAttribute("error", "Wrong username");
            return "forgot";
        }
        StringBuilder passwordBuilder = new StringBuilder();
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            passwordBuilder.append((char) ('a' + random.nextInt(26)));
        }
        String passwordHash = Cracker.code(passwordBuilder.toString());
        user.setPassword(passwordHash);
        userService.save(user);
        if (PasswordRecovery.send(user, passwordBuilder.toString())) {
            request.getSession().setAttribute("info", "Password recovery mail sent to " + user.getMail());
        } else {
            request.getSession().setAttribute("error", "Your mail is invalid. you have to create a new Account");
        }
        return "index";
    }
    @PostMapping("/login")
    public String login(HttpServletRequest request) throws ServletException, IOException {
        String userName = request.getParameter("username");
        String passwordHash = Cracker.code(request.getParameter("password"));
        User user = userService.findByUserName(userName);
        if (user == null) {
            request.getSession().setAttribute("error", "Wrong login credentials");
            logger.error("User with username {} deosn't exist", userName);
            return "index";
        }
        if (!user.getPassword().equals(passwordHash)) {
            request.getSession().setAttribute("error", "Wrong login credentials");
            logger.debug("Wrong login credentials");
            return "index";
        }
        request.getSession().setAttribute("user", user);
        request.getSession().setAttribute("info", "Successful login.\n"+user.getUserName()+", Welcome to Sax-L");
        logger.info("Successful login, {}", userName);
        Map<Integer, User> userMap = (Map<Integer, User>) request.getServletContext().getAttribute("onlineUsers");
        userMap.put(user.getId(), user);
        // TODO: 9/11/19
//        ActivityDao activityDao = manager.getDao(DaoType.Activity);
//        List<Activity> friendsActivities = new ArrayList<>();
//        user.getFriends().forEach(friend->friendsActivities.addAll(activityDao.findAllForUser(friend.getId())));
//
//        List<Activity> userActivities = activityDao.findAll().stream().filter(s -> s.getUserId() == user.getId()).
//                sorted(Comparator.comparing(Activity::getDateTime).reversed()).collect(Collectors.toList());
//        request.getSession().setAttribute("activities", userActivities);
        request.getSession().setAttribute("friendsIds", user.getFriends());
        return "index";
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        request.getSession().removeAttribute("user");
        Map<Integer, User> userMap = (Map<Integer, User>) request.getServletContext().getAttribute("onlineUsers");
        userMap.remove(user.getId());
        request.getSession().invalidate();
        request.getSession().setAttribute("info", "Bye, "+user.getUserName());
        return "index";
    }
//    @GetMapping("/profile")
//    public String profile(HttpServletRequest request) throws ServletException, IOException {
//        // TODO: 9/11/19
//        return "index";
//    }
    @PostMapping("/promote")
    public String promote(HttpServletRequest request) throws ServletException, IOException {
        int promotableUserId = Integer.parseInt(request.getParameter("promotableUserId"));
        User promotable = userService.findById(promotableUserId);
        promotable.setUserType(UserType.Admin);
        Promise promise = userService.save(promotable);
        if (promise.getLevel() == Level.INFO){
            logger.info("{} is promoted to Admin", promotable.getUserName());
            request.getSession().setAttribute("info", promotable.getUserName() +" is promoted to Admin");
        }else {
            logger.error("Error promoting {}", promotable.getUserName());
            request.getSession().setAttribute("error", "Error during promoting.. please try again");
        }
        return "users-list";
    }
}
