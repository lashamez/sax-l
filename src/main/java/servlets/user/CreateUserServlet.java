package servlets.user;

import dao.UserDao;
import datatypes.User;
import enums.DaoType;
import enums.UserType;
import manager.DaoManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/CreateUserServlet")
public class CreateUserServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        UserDao userRepository = ((DaoManager) request.getServletContext().getAttribute("manager")).getDao(DaoType.User);
        String userName = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmpassword");
        String firstName = request.getParameter("firstname");
        String lastName = request.getParameter("lastname");
        String mail = request.getParameter("mail");
        UserType userType = request.getParameter("usertype").equals("admin")? UserType.Admin:UserType.User;
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords don't match");
            System.out.println("Passwords don't match");
            request.getRequestDispatcher("users-list").forward(request, response);
            return;
        }
        if (userRepository.findByUserName(userName) != null) {
            request.setAttribute("error", "Username is already taken");
            System.out.println("Username is already taken");
            request.getRequestDispatcher("users-list").forward(request, response);
            return;
        }

        User user = new User(userName, password, firstName, lastName, mail);
        user.setUserType(userType);
        userRepository.insert(user);
        request.getRequestDispatcher("users-list").forward(request, response);
    }
}
