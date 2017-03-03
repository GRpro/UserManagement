<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Create an account</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>
<body>

<div class="container">

    <c:if test="${pageContext.request.userPrincipal.name != null}">
        <form id="logoutForm" method="POST" action="${contextPath}/logout">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>

        <h2>Administrator account ${pageContext.request.userPrincipal.name} | <a onclick="document.forms['logoutForm'].submit()">Logout</a></h2>

    </c:if>

    <h2>List of Users</h2>

    <ul class="list-group">
        <c:forEach items="${users}" var="user">
            <li class="list-group-item ${!user.isPending() ? 'list-group-item-success' : 'list-group-item-warning'}">
                <div class="row">
                    <div class="col-sm-11">
                        <h5>Id: ${user.id}</h5>
                        <h5>Name: <strong>${user.username}</strong></h5>
                    </div>
                    <div class="col-sm-1">
                        <form id="userStateForm" method="POST" action="${contextPath}/admin/user/${user.id}/state?action=${user.isPending() ? "activate" : "block"}">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <c:if test="${user.isPending()}">
                                <button type="submit" class="btn btn-success">Confirm</button>
                            </c:if>
                            <c:if test="${!user.isPending()}">
                                <button type="submit" class="btn btn-warning">Block</button>
                            </c:if>
                        </form>
                    </div>
                </div>
            </li>
        </c:forEach>
    </ul>

</div>
<!-- /container -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script src="${contextPath}/resources/js/bootstrap.min.js"></script>
</body>
</html>
