package servlets.toasts;

import datatypes.toast.Toast;
import datatypes.user.User;
import manager.DaoManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/createToast")
public class CreateToastServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DaoManager manager = (DaoManager) request.getServletContext().getAttribute("manager");
        User user = (User) request.getSession().getAttribute("user");
        String title = request.getParameter("toastTitle");
        String toastText = request.getParameter("content");
        Toast toast = new Toast(user.getId(), title, toastText, LocalDateTime.now());
        if (manager.insert(toast)) {
            request.getSession().setAttribute("info", "Toast created successfully");
        }else {
            request.getSession().setAttribute("error", "Error adding toast");
        }
        response.sendRedirect("/");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

    }
}
