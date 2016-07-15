
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
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Change password</title>
</head>
<body>
	
<div class="container"><c:import url='/jsp/nav.jsp'></c:import>
		<div class="row">
			<div class="col-md-6 col-md-offset-3" style="padding-top: 10em;">
				<div class="panel panel-info">
					<div class="panel-heading">
						<h3 class="panel-title">Set new Password</h3>
					</div>
					<div class="panel-body">
						<form class="form-horizontal" action="changePassword?action=newPassowrd" method="post">

							<div align="center"> 
						 	<c:out value="${msg}" escapeXml="false" />
						 	</div>
							<div class="row">
								<div class="col-md-11 ">
									
								
									<div class="form-group">
										<label for="newPassword" class="col-sm-4 control-label">New
											Password:</label>
										<div class="col-sm-7">
											<input type="password" class="form-control" name="newPassword" id="newPassword" placeholder="New Password" required>
										</div>
									</div>
								</div>
							</div>


							<div class="row">
								<div class="col-md-11 ">
									<div class="form-group">
										<label for="ConfirmPassword" class="col-sm-4 control-label">Confirm Password:</label>
										<div class="col-sm-7">
											<input type="password" class="form-control" name="ConfirmPassword" id="ConfirmPassword" placeholder="Confirm Password" required>
										</div>
									</div>
								</div>
							</div>


							<div class="row">
								<div class="col-md-10 ">
									<div class="form-group">
										<label for="action" class="col-sm-6 control-label"></label>
										<div class="col-sm-12">
											<input type="hidden" class="form-control" name="action" id="action" value="update">
										</div>
									</div>
								</div>
							</div>

							<div class="row">
								<div class="col-md-6"></div>
								<div class="col-md-6">
									<div class="form-group">
										<div class="col-sm-12">
											<input class="btn btn-default" type="submit" value="submit" class="btn" /> &nbsp&nbsp <input class="btn btn-default"
												type="reset" value="Reset " class="btn" />

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


