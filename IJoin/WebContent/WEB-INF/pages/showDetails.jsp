
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<link rel="stylesheet"
	href='<c:out value="${pageContext.request.contextPath}"/>/css/bootstrap.css'>
<link rel="stylesheet"
	href='<c:out value="${pageContext.request.contextPath}"/>/css/bootstrap-theme.css'>
<script
	src='<c:out value="${pageContext.request.contextPath}"/>/js/jquery.min.js' /></script>
<script
	src='<c:out value="${pageContext.request.contextPath}"/>/js/bootstrap.min.js'></script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<script>

	
	$(document).ready(function() {
		$('#update').on('click', function() {
			$.ajax({
				url : "updateOrder",
				data : {
					"msisdn" : $("#msisdn").val(),
					"act_status" : $("#active").val(),
					"order_id" : $("#order_id").html(),
					"iccid" : $("#iccid").val()
				},
				method : "POST"
			}).done(function(data) {
				if (null != data.Status && data.Status === "SUCCESS") {
					//alert("Succesfully Updated");
					$("#msg").removeClass("text-danger");
					$("#msg").addClass("text-success");
					$("#msg").html("SUCCESS"); 
				} else {
					//alert("Please try again");
					$("#msg").removeClass("text-success");
					$("#msg").addClass("text-danger");
					$("#msg").html("FAILURE");
				}
			});
		});
		$('#back').on('click', function() {
			location.href = "/IJoin/order";
		});
		
			$.ajax({
				url : "getImage",
				data : {
					"order_id" : $("#order_id").html()
				},
				method : "POST"
			}).done(function(data) {
				if (null != data.Status && data.Status === "SUCCESS") {
					console.log(data.Status);
				} else {
					console.log(data.Status);
				}
			});
		});
	
</script>
<title>Show Details</title>
</head>

<body>
	<div class="container">
	<c:import url='/jsp/nav.jsp'></c:import>
		<div class="row">
			<div class="col-md-14 ">
				<div class="panel panel-info">
					<div class="panel-heading">
						<h3 class="panel-title">
							Detail Order : <span id="order_id">${list[0]["ORDER_ID"]}</span>
						</h3>
					</div>
					<div class="panel-body">
					<div class="row">
					<p class="col-md-4">Status: <span id="msg"></span></p>
					<div class="pull-right" style="padding-right:30px;">
						<input type="button" class='btn btn-danger' id="update"	value="Execute"> 
						<input type="button" class='btn btn-info' id="back" value="Back">
						</div>
					</div>
						<div class="row">
							<div class="col-md-2">

								<form action="updateOrder" method="post">
									<br>
									<div class="form-group">
										<label for="msisdn">MSISDN</label> <input type="text"
											class="form-control" id="msisdn" value='${list[0]["MSISDN"]}'>
									</div>

									<div class="form-group">
										<label for="invoice">Invoice</label> <input type="text"
											class="form-control" id="invoice" value=${list[0]["INVOICE"]}
											disabled="disabled">
									</div>

									<div class="form-group">
										<label for="Activation">Activation</label> <select id="active"
											class="form-control">
											<option>${list[0]["ACT_STATUS"]}</option>
											<option>Pending</option>
											<option>Approved</option>
										</select>
									</div>

									<div class="form-group">
										<label for="TrackingNum">Delivery Status</label> <input
											type="text" class="form-control" id="DeliveryNum"
											value=${list[0]["DELIVERY_STATUS"]} disabled="disabled">
									</div>

									<div class="form-group">
										<label for="TrackingNum">Tracking Number</label> <input
											type="text" class="form-control" id="TrackingNum"
											value=${list[0]["TRACKING_NUM"]} disabled="disabled">
									</div>
								</form>

							</div>
							<div class="col-md-2">
								<br>
								<form>
									<div class="form-group">
										<label for="ICCID">ICCID</label> <input type="text"
											class="form-control" id="iccid" value=${list[0]["ICC_ID"]}>
									</div>

									<div class="form-group">
										<label for="orderDate">Order Date</label> <input type="text"
											class="form-control" id="orderDate"
											value=${list[0]["ORDER_DATE"]} disabled="disabled">
									</div>

									<div class="form-group">
										<label for="ActivationDate">Activation Date</label> <input
											type="text" class="form-control" id="ActivationDate"
											value=${list[0]["ACT_DATE"]} disabled="disabled">
									</div>

									<div class="form-group">
										<label for="deliveryStatusDate">Delivery Status Date</label> <input
											type="text" class="form-control" id="deliveryStatusDate"
											value=${list[0]["DELIVERY_DATE"]} disabled="disabled">
									</div>

									<div class="form-group">
										<label for="3PLName">3PL Name</label> <input type="text"
											class="form-control" id="3PLName" value=${list[0]["PL_NAME"]}
											disabled="disabled">
									</div>
									<div class="form-group">
										<a href="" style="font-size:12px">Click here for tracking in web</a>
									</div>
								</form>
							</div>
							<div class="col-md-3">
								<form>
									<br>
									<div class="form-group">
										<label for="CustomerName">Customer Name</label> <input
											type="text" class="form-control" id="CustomerName"
											value=${list[0]["NAME"]} disabled="disabled">
									</div>
									<div class="form-group">
										<label for="customerEmail">Customer Email ID</label> <input
											type="text" class="form-control" id="customerEmail"
											value=${list[0]["EMAIL"]} disabled="disabled">
									</div>
									<div class="form-group">
										<label for="CustomerPhone">Customer Phone</label> <input
											type="text" class="form-control" id="CustomerPhone"
											value=${list[0]["ALT_NUMBER"]} disabled="disabled">
									</div>
									<div class="form-group">
										<label for="MotherMiddleName">Mother Middle Name</label> 
										<input type="text" class="form-control" id="MotherMiddleName" value=${list[0]["MAIDEN_NAME"]} disabled="disabled">
									</div>
									<div class="form-group">
										<label for="ShippingAddress">Shipping Address</label>
										<textarea rows="2" cols="37" disabled="disabled">${list[0]["SHIP_ADDRESS"]}</textarea>
									</div>
								</form>
							</div>
							<div class="row">
								<div class="col-md-5">
									<div class="col-md-7">
										<form>
											<br>
											<div class="form-group">
												<label for="IDNumber">ID Number</label> <input type="text"
													class="form-control" id="IDNumber"
													value=${list[0]["ID_NUMBER"]} disabled="disabled">
											</div>
											<div class="form-group">
												<label for="birthPlace">Place Of Birth</label> 
												<input type="text" class="form-control" id="birthPlace"	value=${list[0]["PLACE_OF_BIRTH"]} disabled="disabled">
											</div>
											<div>
												<label for="custPic">Customer Picture</label><br>
												<img src='data:image/jpeg;base64,${list[0]["cust_img"]}' height="130" width="250" border="5" id="getImage">
											</div>
											<div class="form-group">
												<label for="customerAddress">Customer Address</label>
												<textarea rows="2" cols="65" disabled="disabled">${list[0]["ADDRESS"]}</textarea>
											</div>
										</form>
									</div>
									<div class="col-md-5">
										<form>
										<br>
											<div class="form-group">
													<label for="Gender">Gender</label> 
													<input type="text" class="form-control" id="Gender" value=${list[0]["GENDER"]}	disabled="disabled">
											</div>
											<div class="form-group">
													<label for="DateOFBirth">Date OF Birth</label> 
													<input type="text" class="form-control" id="DateOFBirth" value=${list[0]["DOB"]} disabled="disabled">
											</div>
													<label for="idPic"></label><br> 
													<img src='data:image/jpeg;base64,${list[0]["id_img"]}' height="130" width="150" border="5" id="getImage">
	
											<input type="button" class="btn btn-primary" id="getImage" value="view Image" />
										</form>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>