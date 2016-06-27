<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" href='<c:out value="${pageContext.request.contextPath}"/>/css/bootstrap.css'>
<link rel="stylesheet" href='<c:out value="${pageContext.request.contextPath}"/>/css/bootstrap-theme.css'>

<script src='<c:out value="${pageContext.request.contextPath}"/>/js/jquery.min.js' type="text/javascript"> </script>
<script src='<c:out value="${pageContext.request.contextPath}"/>/js/bootstrap.min.js'  type="text/javascript"> </script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Login</title>
</head>
<body>
<div class="container">

		<div class="row">
			<div class="col-md-12">
				<form class="" style="padding-top: 5em;" action="getpackage" method="post">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Get Package and Offer</h3>
						</div>
						
						<div class="panel-body">
						  <div class="row">
			             <div class="col-md-10 ">
							# Package information<br>
						    &nbsp&nbsp&nbsp <a href='getallpackage'>1. Get all package</a> <br>
							 
						</div>
						</div>
							 <br>
							 <br>
							 <br>
						  <div class="row">
			             <div class="col-md-10 ">
							
							# Offer information<br>
							  &nbsp&nbsp&nbsp <a href='order'>1. Order Staus</a><br>
							 
							  &nbsp&nbsp&nbsp <a href='UploadOffer'>2. Upload Offer</a> <br>
							  &nbsp&nbsp&nbsp <a href='changeHotOffer'>3. Change Hot Offer</a> <br>
							  &nbsp&nbsp&nbsp <a href='changePackge'>4. Change Package</a><br>
							  &nbsp&nbsp&nbsp <a href='changeStore'>5. change Store</a><br>
							  &nbsp&nbsp&nbsp <a href='showallOffer'>6. show all Offer</a><br>
							  &nbsp&nbsp&nbsp <a href='UpdateSspPackage_code'>7. Update SSP Package Code</a>
							 
						</div>
						</div>
							
							
						
						</div>
					</div>
				</form>				
			</div>
		</div>
	</div>
	
</body>
</html>