package servlets.user;

import dao.UserDao;
import datatypes.User;
import enums.DaoType;
import enums.FormFields;
import enums.UserType;
import mail.PasswordRecovery;
import manager.DaoManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/CreateUserServlet")
public class CreateUserServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(CreateUserServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        DaoManager manager = (DaoManager) getServletContext().getAttribute("manager");
        UserDao userRepository = ((DaoManager) getServletContext().getAttribute("manager")).getDao(DaoType.User);
        String userName = request.getParameter(FormFields.username.getValue());
        String password = request.getParameter(FormFields.password.getValue());
        String confirmPassword = request.getParameter(FormFields.confirmpassword.getValue());
        String firstName = request.getParameter(FormFields.firstname.getValue());
        String lastName = request.getParameter(FormFields.lastname.getValue());
        String mail = request.getParameter(FormFields.mail.getValue());
        UserType userType = request.getParameter("usertype").equals("admin")? UserType.Admin:UserType.User;
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords don't match");
            logger.error("Passwords don't match");
            request.getRequestDispatcher("users-list").forward(request, response);
            return;
        }
        if (userRepository.findByUserName(userName) != null) {
            request.setAttribute("error", "Username is already taken");
            logger.error("Username is already taken, {}", userName);
            request.getRequestDispatcher("users-list").forward(request, response);
            return;
        }

        User user = new User(userName, password, firstName, lastName, mail);
        user.setUserType(userType);
        manager.insert(user);
        request.getRequestDispatcher("users-list").forward(request, response);
    }
}
