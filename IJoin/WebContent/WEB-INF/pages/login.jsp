<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet"
	href='<c:out value="${pageContext.request.contextPath}"/>/css/bootstrap.css'>
<link rel="stylesheet"
	href='<c:out value="${pageContext.request.contextPath}"/>/css/bootstrap-theme.css'>

<script
	src='<c:out value="${pageContext.request.contextPath}"/>/js/jquery.min.js'
	type="text/javascript">
	
</script>
<script
	src='<c:out value="${pageContext.request.contextPath}"/>/js/bootstrap.min.js'
	type="text/javascript">
	
</script>

<style type="text/css">     
    select {
        width:200px;
    }
    label{
    	font-weight:100;
    }
</style>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Login</title>
</head>
<body>
	<div class="container">
		<div class="row">
			<div style="padding-top: 10em;">
				<div class="panel panel-info">
					<div class="panel-heading" align="center"></div>
					<div class="panel-body">
						<div class="row">
							<div>
								<font size="5" color="#b30000"><center>
										<b>I-Join Order Management</b>
									</center></font> <img src="images/images.png" alt="Smiley face" height="125" width="250" border="5">
							</div>
						</div>
						<div class="col-md-5 col-md-offset-4">
						<div>
									<c:out value="${msg}" escapeXml="false" />
								</div>
							<form action="login" method="post">
								
								<div class="form-group">
									<label for="userName">User Name</label> <input type="text" class="form-control" name="LoginID" placeholder="User Name">
								</div>
								<div class="form-group">
									<label for="exampleInputPassword1">Password</label> 
									<input type="password" class="form-control" name="Password" placeholder="Password">
								</div>
								<div class="form-group" align="right">
									<label for="forgetPWD"></label> 
									<a href='forgot?action=forgot'>Forgot password ?</a>
								</div>
								
								<div align="center">
									<button type="submit" class="btn btn-primary">
										Login <span class="glyphicon glyphicon-arrow-right"></span>
									</button>
								</div>
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>