package servlets.user;

import datatypes.user.User;
import enums.FormFields;
import manager.DaoManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import security.Cracker;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/UpdateUserServlet")
public class UpdateUserServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(UpdateUserServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        DaoManager manager = (DaoManager) getServletContext().getAttribute("manager");
        User user = (User) request.getSession().getAttribute("user");
        String password = request.getParameter(FormFields.password.getValue());
        String confirmPassword = request.getParameter(FormFields.confirmpassword.getValue());
        String firstName = request.getParameter(FormFields.firstname.getValue());
        String lastName = request.getParameter(FormFields.lastname.getValue());
        if (!password.equals(confirmPassword)) {
            logger.debug("Passwords don't match, {} {}", password, confirmPassword);
            request.setAttribute("error", "Passwords don't match");
            request.getRequestDispatcher("profile").forward(request, response);
            return;
        }
        user.setPassword(Cracker.code(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        manager.update(user);
        request.setAttribute("info", "Profile updated successfully");
        request.getRequestDispatcher("profile").forward(request, response);

    }
}
