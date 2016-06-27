<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" href='<c:out value="${pageContext.request.contextPath}"/>/css/bootstrap.css'>
<link rel="stylesheet" href='<c:out value="${pageContext.request.contextPath}"/>/css/bootstrap-theme.css'>

<script src='<c:out value="${pageContext.request.contextPath}"/>/js/jquery.min.js'> </script>
<script src='<c:out value="${pageContext.request.contextPath}"/>/js/bootstrap.min.js'> </script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Register</title>
</head>
<body>
<div class="container">
  <div class="row">
  <div class="col-md-12">
	  <form class="form-horizontal" action="getOrderlist" method="post">
	  <div class="col-md-2">
	  	<div class="form-group">
		    <label for="orderid">ORDER ID</label>
		    <input type="text" class="form-control" id="orderid" placeholder="orderid" name="name"/>
		   </div>
	   </div> 
	   <div class="col-md-2">
	   	<div class="form-group">
		    <label for="invoice">INVOICE</label>
		    <input type="text" class="form-control" id="invoice" placeholder="INVOICE" name="invoice" required="true"/>
	   	</div>
	   </div>
	   <div class="col-md-2">
	   	<div class="form-group">
		    <label for="msisdn">MSISDN</label>
		    <input type="text" class="form-control" id="msisdn" placeholder="MSISDN" name="msisdn"/>
		 </div>
		</div>
		<div class="col-md-2">
			<div class="form-group">
			    <label for="activation">ACTIVATION</label>
		    	<input type="text" class="form-control" id="activation" placeholder="Activation" name="activation"/>
			</div>
		</div>
		<div class="col-md-2">
			<div class="form-group">
		    	<label for="addr">DELIVERY STATUS</label>
		    	<input type="text" class="form-control" id="disabledInput" placeholder="DELIVERY STATUS" name="delivery Status" disabled/>
			</div>
		</div>
		<div class="col-md-2">
			<div class="form-group">
	     		<label for="action">ACTION</label>
   				<input type="button" class="form-control" id="action" placeholder="ACTION" name="action"/>
   			</div>   		
   		</div>
	</form>
	</div>
    </div>
 </div>
</body>
</html>