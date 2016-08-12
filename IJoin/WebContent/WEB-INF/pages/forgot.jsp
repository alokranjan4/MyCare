
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
<title>Forget password</title>
</head>

<body>
	<div class="container">
		<div class="row">
			<div class="col-md-5 col-md-offset-3" style="padding-top: 10em;">
				<div class="panel panel-info">
					<div class="panel-heading">
						<h3 class="panel-title">Forgot password </h3>
					</div>
					<div class="panel-body">
						<form class="form-horizontal" action="forgot" method="post">

							<c:out value="${msg}" escapeXml="false" />
							<div class="row">
								<div class="col-md-10 ">
									<div class="form-group">
										<label for="LoginID" class="col-sm-4 control-label">Login
											ID:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" name="LoginID" id="LoginID" placeholder="Login ID" required>
										</div>
									</div>
								</div>
							</div>

							<div class="row">
								<div class="col-md-10 ">
									<div class="form-group">
										<label for="EmailID" class="col-sm-4 control-label">Email ID:</label>
										<div class="col-sm-8">
											<input type="email" class="form-control" name="EmailID" id="EmailID" placeholder="Email ID" required>
										</div>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-md-10 ">
									<div class="form-group">
										<label for="action" class="col-sm-4 control-label"></label>
										<div class="col-sm-8">
											<input type="hidden" class="form-control" name="action"
												id="action" value="forgot" />
										</div>
									</div>
								</div>
							</div>

							<div class="row">

								<div class="col-md-4"></div>
								<div class="col-md-8">
									<div class="form-group">
										<div class="col-sm-4">

											<input class="btn btn-default" type="submit" value="submit"
												class="btn" />


										</div>


										<div class="col-sm-3">

											<input class="btn btn-default" type="reset" value="Reset "
												class="btn" />

										</div>
									</div>
								</div>
							</div>





						</form>
					</div>
				</div>
			</div>
		</div>
	</div>


</body>
</html>


