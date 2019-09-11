<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="dao.UserDao" %>
<%@ page import="datatypes.formfields.EditFormField" %>
<%@ page import="datatypes.formfields.FormField" %>
<%@ page import="datatypes.formfields.SelectField" %>
<%@ page import="datatypes.user.User" %>
<%@ page import="enums.DaoType" %>
<%@ page import="enums.FormFields" %>
<%@ page import="enums.InputType" %>
<%@ page import="enums.UserType" %>
<%@ page import="manager.DaoManager" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">

    <title>Sax-L Quiz Website</title>
    <link rel="stylesheet" href="css/font-awesome.min.css">
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/sidebar.css">
    <link rel="stylesheet" href="css/segment.css">
    <link rel="stylesheet" href="css/label.min.css">
    <link rel="stylesheet" href="css/button.min.css">
    <link rel="stylesheet" type="text/css" href="css/dataTables.bootstrap4.min.css"/>

</head>

<body>
<%
    DaoManager daoManager = (DaoManager) request.getServletContext().getAttribute("manager");
    UserDao userDao = daoManager.getDao(DaoType.User);
    List<FormField> formFields = new ArrayList<>();
    formFields.add(new FormField("Username", FormFields.username.getValue(), InputType.text, true, 4));
    formFields.add(new FormField("Password", FormFields.password.getValue(), InputType.password, true, 4));
    formFields.add(new FormField("Confirm Password", FormFields.confirmpassword.getValue(), InputType.password, true, 4));
    formFields.add(new FormField("Mail", FormFields.mail.getValue(), InputType.email, true, 4));
    formFields.add(new FormField("First Name", FormFields.firstname.getValue(), InputType.text, true, 0));
    formFields.add(new FormField("Last Name", FormFields.lastname.getValue(), InputType.text, true, 0));
    SelectField createSelectField = new SelectField("User Type", "usertype", Arrays.asList("admin", "user"));
%>
<div class="wrapper">
    <!-- Sidebar Holder -->
    <jsp:include page="components/sidebar.jsp"/>
    <!-- Page Content Holder -->
    <div id="content">
        <jsp:include page="components/topbar.jsp"/>
        <div class="container">
            <div class="ui segment">
                <div class="huge ui black ribbon label">
                    Users
                </div>

                <c:if test="${sessionScope.user!=null && sessionScope.user.userType == UserType.Admin}">
                    <div style="float:right">
                        <h:create entityName="User" selectFields="<%=createSelectField%>" actionServlet="createUser"
                                  formFields="<%=formFields%>" formId="createUserForm"/>
                    </div>
                </c:if>
                <table id="myTable" class="table table-striped table-bordered table-sm">
                    <thead>
                    <tr>
                        <th>#</th>
                        <th>Username</th>
                        <th>Status</th>
                        <th>Achievements</th>
                        <c:choose>
                            <c:when test="${sessionScope.user!=null && sessionScope.user.userType==UserType.Admin}">
                                <th>Action</th>
                            </c:when>
                        </c:choose>
                    </tr>
                    </thead>
                    <tbody>
                    <c:set var="i" value="0" scope="page"/>
                    <c:forEach items="<%=userDao.findAll()%>" var="currentUser">
                        <tr>
                            <td>${i+1}
                            </td>
                            <c:choose>
                                <c:when test="${currentUser.id == sessionScope.user.id}">
                                    <td>
                                        <a href="profile">${currentUser.userName}
                                            <c:if test="${applicationScope.onlineUsers.containsValue(currentUser)}">
                                                <span class="badge badge-success">Online</span>
                                            </c:if>
                                        </a>
                                    </td>
                                </c:when>
                                <c:when test="${currentUser.id != sessionScope.user.id}">
                                    <td>
                                        <a href="user-profile?userid=${currentUser.id}">${currentUser.userName}
                                            <c:if test="${applicationScope.onlineUsers.containsValue(currentUser)}">
                                                <span class="badge badge-success">Online</span>
                                            </c:if>
                                        </a>
                                    </td>
                                </c:when>
                            </c:choose>
                            <td>${currentUser.userType}</td>
                            <td>${currentUser.achievements.size()}</td>
                            <c:choose>
                                <c:when test="${sessionScope.user!=null && sessionScope.user.userType==UserType.Admin}">
                                    <td>
                                        <%
                                            User user1 = (User) pageContext.getAttribute("currentUser");
                                            List<EditFormField> editFormFields = new ArrayList<>();
                                            editFormFields.add(new EditFormField("Username", FormFields.username.getValue(), InputType.text, true, 4, user1.getUserName(), true));
                                            editFormFields.add(new EditFormField("Password", FormFields.password.getValue(), InputType.password, true, 4, "", false));
                                            editFormFields.add(new EditFormField("Confirm Password", FormFields.confirmpassword.getValue(), InputType.password, true, 4, "", false));
                                            editFormFields.add(new EditFormField("Mail", FormFields.mail.getValue(), InputType.email, true, 4, user1.getMail(), true));
                                            editFormFields.add(new EditFormField("First Name", FormFields.firstname.getValue(), InputType.text, true, 0, user1.getFirstName(), false));
                                            editFormFields.add(new EditFormField("Last Name", FormFields.lastname.getValue(), InputType.text, true, 0, user1.getLastName(), false));
                                            SelectField editSelectField = new SelectField("User Type", "usertype", Arrays.asList("admin", "user"));
                                        %>
                                        <!-- ***** delete user modal ***** -->
                                        <h:delete entityName="User"
                                                  actionServlet="deleteUser"
                                                  hiddenParameterName="deleteUserId"
                                                  hiddenParameterValue="${currentUser.id}">
                                        </h:delete>
                                        <!-- ***** update user modal ***** -->
                                        <h:edit entityName="User"
                                                actionServlet="editFromAdmin"
                                                hiddenParameterName="hiddenId"
                                                hiddenParameterValue="${currentUser.id}"
                                                formFields="<%=editFormFields%>"
                                                selectFields="<%=editSelectField%>">
                                        </h:edit>
                                        <c:if test="${currentUser.userType==UserType.User}">
                                            <form action="PromoteUserServlet" method="post" style="float:right">
                                                <button type="submit"  class="mini ui green button">
                                                    <i class="fa fa-coffee"></i> Promote
                                                </button>
                                                <input type="hidden" name="promotableUserId" value="${currentUser.id}">
                                            </form>
                                        </c:if>
                                    </td>
                                </c:when>
                            </c:choose>
                        </tr>
                        <c:set var="i" value="${i + 1}" scope="page"/>
                    </c:forEach>

                    </tbody>
                    <tfoot>
                    <tr>
                        <th>#</th>
                        <th>Username</th>
                        <th>Status</th>
                        <th>Achievements</th>
                        <c:choose>
                            <c:when test="${sessionScope.user!=null && sessionScope.user.userType==UserType.Admin}">
                                <th>Action</th>
                            </c:when>
                        </c:choose>
                    </tr>
                    </tfoot>
                </table>
            </div>
        </div>

    </div>
</div>

<script src="js/solid.js"></script>
<script src="js/fontawesome.js"></script>

<script src="js/jquery.min.js"></script>
<!-- Popper.JS -->
<script src="js/popper.min.js"></script>
<!-- Bootstrap JS -->
<script src="js/bootstrap.min.js"></script>
<script src="js/bootstrap.bundle.min.js"></script>
<script src="js/jquery.dataTables.min.js"></script>

<script src="js/dataTables.bootstrap4.min.js"></script>

<script type="text/javascript">
    $(document).ready(function () {
        $('#sidebarCollapse').on('click', function () {
            $('#sidebar').toggleClass('active');
            $(this).toggleClass('active');
        });
        $('.table').DataTable();
        $('.modal').appendTo("body");
        $('.toast').appendTo("body");
    });
</script>

<script src="//cdnjs.cloudflare.com/ajax/libs/jquery-form-validator/2.3.26/jquery.form-validator.min.js"></script>

</body>

</html>