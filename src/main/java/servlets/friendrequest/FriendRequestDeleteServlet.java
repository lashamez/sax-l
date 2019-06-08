package servlets.friendrequest;

import dao.FriendRequestDao;
import datatypes.User;
import datatypes.messages.FriendRequest;
import enums.DaoType;
import enums.RequestStatus;
import manager.DaoManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@WebServlet("/FriendRequestDeleteServlet")
public class FriendRequestDeleteServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        DaoManager manager = (DaoManager) request.getServletContext().getAttribute("manager");
        FriendRequestDao friendRequestDao = manager.getDao(DaoType.FriendRequest);
        User user = (User) request.getSession().getAttribute("user");
        int receiverId = Integer.parseInt(request.getParameter("receiverId"));
        FriendRequest request1 = friendRequestDao.findBySenderReceiverId(user.getId(), receiverId);
        friendRequestDao.deleteById(request1.getId());
        request.getRequestDispatcher("user-profile?userid=" + receiverId).forward(request, response);
    }
}
