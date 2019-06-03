<%--
  Created by IntelliJ IDEA.
  User: lasha
  Date: 5/27/19
  Time: 8:16 PM
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="description" content="">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <!-- The above 4 meta tags *must* come first in the head; any other head content must come *after* these tags -->

    <!-- Title -->
    <title>Sax-L - Quiz Website | Login</title>

    <!-- Favicon -->
    <link rel="icon" href="img/core-img/favicon.ico">

    <!-- Core Stylesheet -->
    <link href="style.css" rel="stylesheet">
    <link href="css/loginpanel.css" rel="stylesheet">
    <!-- Responsive CSS -->
    <link href="css/responsive.css" rel="stylesheet">
    <link href="css/toastr.css" rel="stylesheet">


</head>
<body>
<!-- ***** Preloader Start ***** -->
<div id="preloader">
    <div class="mosh-preloader"></div>
</div>
<!-- ***** Header Area Start ***** -->
<header class="header_area clearfix">
    <jsp:include page="components/header.jsp"/>
</header>
<!-- ***** Header Area End ***** -->
<!-- ***** Breadcumb Area Start ***** -->
<div class="mosh-breadcumb-area" style="background-image: url(img/core-img/breadcumb.png);">
    <div class="container h-100">
        <div class="row h-100 align-items-center">
            <div class="col-12">
                <div class="bradcumbContent">
                    <h2>Register Page</h2>
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item"><a href="/">Home</a></li>
                            <li class="breadcrumb-item active" aria-current="page">Registration Form</li>
                        </ol>
                    </nav>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- ***** Breadcumb Area End ***** -->
<!-- ***** Login Area Start ***** -->
<section class="mosh-aboutUs-area">
    <div class="login">
        <form action="RegisterServlet" method="post" id="signUpForm">
            <input type="text" placeholder="Username" name="username" required minlength="4">
            <input type="password" placeholder="Password" name="password" required minlength="4" >
            <input type="password" placeholder="Confirm password" name="confirmpassword" required minlength="4">
            <input type="email" placeholder="E-mail" name="mail" required >
            <input type="text" placeholder="First Name" name="firstname">
            <input type="text" placeholder="Last Name" name="lastname">
            <br>
            <button type="submit" class="btn btn-info btn-sm" style="display: block; margin: 0 auto;">
                <i class="fa fa-sign-in"></i> Register
            </button>
        </form>
        <script>
            ${"#signUpForm"}.validate();
        </script>
    </div>
</section>
<!-- ***** Login Area End ***** -->
<footer class="footer-area clearfix">
    <jsp:include page="components/footer.jsp"/>
</footer>

<!-- jQuery-2.2.4 js -->
<script src="js/jquery-2.2.4.min.js"></script>
<!-- Popper js -->
<script src="js/popper.min.js"></script>
<!-- Bootstrap js -->
<script src="js/bootstrap.min.js"></script>
<!-- All Plugins js -->
<script src="js/plugins.js"></script>
<!-- Active js -->
<script src="js/active.js"></script>

<!-- Sign up validation -->
<script src="js/jquery.validate.js"></script>

<script src="js/toastr.js"></script>
<%
    String error = (String) request.getAttribute("error");
    if (error != null) {%>
<script>
    toastr.options.closeButton = true;
    toastr.options.timeOut = 0;
    toastr.options.extendedTimeOut = 0;
    toastr.error("<%=error%>");
</script>
<%request.removeAttribute("error");
}%>
</body>
</html>
