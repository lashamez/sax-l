<%@ page import="enums.UserType" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<nav id="sidebar">
    <div class="sidebar-header">
        <h3>Sax-L</h3>
    </div>

    <ul class="list-unstyled components">
        <p>Navigation</p>
        <li>
            <a href="${pageContext.request.contextPath}/"><i class="fas fa-home"></i> Home</a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/toasts.jsp"><i class="fas fa-ambulance"></i> Toasts</a>
        </li>
        <c:if test="${sessionScope.user !=null}">
            <li><a href="profile"><i class="fas fa-id-badge"></i> Profile</a></li>
            <li><a href="users-list"><i class="fas fa-users"></i> Users</a></li>
            <li>
                <jsp:include page="menu-modals/edit-profile.jsp"/>
            </li>
            <li>
                <jsp:include page="menu-modals/achievements.jsp"/>
            </li>
            <li>
                <jsp:include page="menu-modals/recent-activities.jsp"/>
            </li>
            <li>
                <jsp:include page="menu-modals/friends-modal.jsp"/>
            </li>
        </c:if>
        <c:if test="${sessionScope.user !=null && sessionScope.user.userType == UserType.Admin}">
            <li><a href="admin"><i class="fas fa-tools"></i> Administration</a></li>
        </c:if>
        <li>
            <a href="contact"><i class="fas fa-question"></i> Contact</a>
        </li>
        <c:choose>
            <c:when test="${sessionScope.user ==null}">
                <li>
                    <jsp:include page="menu-modals/login-form.jsp"/>
                </li>
                <li>
                    <jsp:include page="menu-modals/register-form.jsp"/>
                </li>
            </c:when>
            <c:otherwise>
                <li>
                    <a href="logout">
                        <i class="fas fa-sign-out-alt"></i> Sign out
                    </a>
                </li>
            </c:otherwise>
        </c:choose>
    </ul>
</nav>
