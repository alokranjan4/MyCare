
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
<script>
var request;  
function sendInfo(){   
	var v=document.offers.Offer_ID.value;  
	var url="getOfferAjaxOfferID?action="+v;  
	if(window.XMLHttpRequest){  
		request=new XMLHttpRequest();  
		}  
		else if(window.ActiveXObject){  
		request=new ActiveXObject("Microsoft.XMLHTTP");  
		}  
		try  
		{  
		request.onreadystatechange=getInfo;  
		request.open("GET",url,true);  
		request.send();  
		}  
		catch(e)  
		{  
		alert("Unable to connect to server");  
		}  
		}  
		  
		function getInfo(){  
		if(request.readyState==4){  
		var val=request.responseText;  
		document.getElementById('PACKAGE_Code').innerHTML=val;  
		} 
}  
</script>
<style type="text/css">     
    select {
        width:200px;
    }
</style>

<script>
$(document).ready(function(){
	// without image    
	 /*
	$("#submit").on('click',function(){
    	var v_offer_id=$("#offer_id").val();
   		var v_pack_code=$("#pack_code").val();
   		if(v_offer_id==""||v_offer_id==null){
		    document.getElementById("offer_id").style.borderColor = "red";
			return false;
		 }else if(v_pack_code==""||v_pack_code==null){
			 document.getElementById("offer_id").style.borderColor = "";
			 document.getElementById("pack_code").style.borderColor = "red";
			 return false;
		 }
    	
    	$.ajax({
			url : "newOffer",
			data : {
				"offer_id" : $("#offer_id").val(),
				"pack_code" : $("#pack_code").val(),
				"tariff" : $("#tariff").val(),
				"offer_Name_ID" : $("#offer_Name_ID").val(),
				"offer_Name_EN" : $("#offer_Name_EN").val(),
				"banefit_ID" : $("#banefit_ID").val(),
				"banner_Image_En" : $("#banner_Image_En").val(),
				"BUY_EXTRA_FLAG" : $("#BUY_EXTRA_FLAG").val(),
				"banefit_EN" : $("#banefit_EN").val(),
				"keyword" : $("#keyword").val(),
				"param" : $("#param").val(),
				"offer_Link" : $("#offer_Link").val(),
				"offer_Type" : $("#offer_Type").val(),
				"customer_Type" : $("#customer_Type").val(),
				"banner_Image_ID" : $("#banner_Image_ID").val(),
			},
			method : "POST"
		}).done(function(data) {
			if (null != data.Status && data.Status === "SUCCESS") {
				document.getElementById("offer_id").style.borderColor = "";
				document.getElementById("pack_code").style.borderColor = "";
				$("#Offer_ID").append('<option value='+$("#offer_id").val()+'>'+$("#offer_id").val()+'</option>');
	  		  	$("#PACKAGE_Code").append('<option value='+$("#pack_code").val()+'>'+$("#pack_code").val()+'</option>');
				
				$("#msg").removeClass("text-danger");
				$("#msg").addClass("text-success");
				$("#msg").html("Record Inserted Successfully."); 
				$("#offer_id").val('');
				$("#pack_code").val('');
				$("#tariff").val('');
				$("#offer_Name_ID").val('');
				$("#offer_Name_EN").val('');
				$("#banefit_ID").val('');
				$("#banner_Image_En").val('');
				$("#keyword").val('');
				$("#param").val('');
				$("#offer_Link").val('');
				$("#offer_Type").val('');
				$("#customer_Type").val('');
				$("#banner_Image_ID").val('');
			  } else {
				$("#msg").removeClass("text-success");
				$("#msg").addClass("text-danger");
				$("#msg").html("Record Insertion fail. Please try again.");
			}
	    });
	 }); 
  */
  
  
	$("#submit").on('click',function(){
    	var v_offer_id=$("#offer_id").val();
   		var v_pack_code=$("#pack_code").val();
   		if(v_offer_id==""||v_offer_id==null){
		    document.getElementById("offer_id").style.borderColor = "red";
			return false;
		 }else if(v_pack_code==""||v_pack_code==null){
			 document.getElementById("offer_id").style.borderColor = "";
			 document.getElementById("pack_code").style.borderColor = "red";
			 return false;
		 }
    	
   	  console.log($("#banner_Image_En").val());
   	  console.log($("#banner_Image_ID").val());
  	  var form =$("#newOffer").get(0); 
  	  var fd = new FormData(form);
  	  	fd.append("offer_id", $("#offer_id").val());
  	  	fd.append("pack_code",$("#pack_code").val());
  	  	fd.append("tariff",$("#tariff").val());
		fd.append("offer_Name_ID",$("#offer_Name_ID").val());
		fd.append("offer_Name_EN",$("#offer_Name_EN").val());
		fd.append("banefit_ID",$("#banefit_ID").val());
		fd.append("banner_Image_En",$("#banner_Image_En").val());
		fd.append("BUY_EXTRA_FLAG",$("#BUY_EXTRA_FLAG").val());
		fd.append("banefit_EN",$("#banefit_EN").val());
		fd.append("keyword",$("#keyword").val());
		fd.append("param",$("#param").val());
		fd.append("offer_Link",$("#offer_Link").val());
		fd.append("customer_Type",$("#customer_Type").val());
		fd.append("offer_Type",$("#offer_Type").val());
		fd.append("banner_Image_ID",$("#banner_Image_ID").val());
		
		$.ajax({
			url : "newOffer",
			data :fd,
			processData: false,  // tell jQuery not to process the data
          contentType: false,   // tell jQuery not to set contentType
			method : "POST"
		}).done(function(data) {
			if (null != data.Status && data.Status === "SUCCESS") {
				document.getElementById("offer_id").style.borderColor = "";
				document.getElementById("pack_code").style.borderColor = "";
				$("#Offer_ID").append('<option value='+$("#offer_id").val()+'>'+$("#offer_id").val()+'</option>');
	  		  	$("#PACKAGE_Code").append('<option value='+$("#pack_code").val()+'>'+$("#pack_code").val()+'</option>');
				
				$("#msg").removeClass("text-danger");
				$("#msg").addClass("text-success");
				$("#msg").html("Record Inserted Successfully."); 
				$("#offer_id").val('');
				$("#pack_code").val('');
				$("#tariff").val('');
				$("#offer_Name_ID").val('');
				$("#offer_Name_EN").val('');
				$("#banefit_ID").val('');
				$("#keyword").val('');
				$("#param").val('');
				$("#offer_Link").val('');
				$("#offer_Type").val('');
				$("#customer_Type").val('');
				$("#banner_Image_En").val('');
				$("#banner_Image_ID").val('');
			  } else {
				$("#msg").removeClass("text-success");
				$("#msg").addClass("text-danger");
				$("#msg").html("Record Insertion fail. Please try again.");
			}
	    });
	 }); 

  
  
$("#new").on('click',function(){
	  $("#showOfferForm").hide();
	  $("#editOfferForm").hide();
	  $("#newOfferForm").show();
		$.ajax({
			url : "getOffer?action=new",
			method : "POST"
		}).done(function(data) {
			if (null != data.Status && data.Status === "SUCCESS") {
				$("#msg").removeClass("text-danger");
				$("#msg").addClass("text-success");
				$("#msg").html("New Form "); 
				
			} else {
				console.log(data.Status);
			}
	    });
	 }); 


	$("#edit").on('click',function(){
		
		var offerID=$("#Offer_ID").val();
		var packageCode=$("#PACKAGE_Code").val();
		
 		if(offerID=="select"||offerID==null){
 			$("#msg").addClass("text-danger");
 			$("#msg").html("Please select an option.");
 		  document.getElementById("Offer_ID").style.borderColor = "red";
 			return false;
		}else if(packageCode=="select"||packageCode==null){
			$("#msg").addClass("text-danger");
 			$("#msg").html("Please select an option.");
	 	    document.getElementById("Offer_ID").style.borderColor = "";
	 	    document.getElementById("PACKAGE_Code").style.borderColor = "red";
	 	    return false;
		}else{
			$("#msg").html("");
	 	    document.getElementById("Offer_ID").style.borderColor = "";
	 	    document.getElementById("PACKAGE_Code").style.borderColor = "";
		}
		
	 $("#showOfferForm").hide();
 	 $("#newOfferForm").hide();
	 $("#editOfferForm").show();
		$.ajax({
			url : "getOffer?action=edit",
			data :{
				"Offer_ID": $("#Offer_ID").val(),
				"PACKAGE_Code": $("#PACKAGE_Code").val()
			},
			method : "POST"
		}).done(function(data) {
			if (null != data.Status && data.Status === "SUCCESS") {
				console.log(data);
				$("#msg").removeClass("text-danger");
				$("#msg").addClass("text-success");
				$("#msg").html("Record Retrived Successfully."); 
					$("#edit_OfferID").val(data.list[0]["OFFER_ID"]);
					$("#edit_PackageCode").val(data.list[0]["PACKAGE_CODE"]);
					$("#edit_Tariff").val(data.list[0]["TARIFF"]);
					$("#edit_OfferNameID").val(data.list[0]["OFFER_NAME_ID"]);
					$("#edit_OfferNameEN").val(data.list[0]["OFFER_NAME_EN"]);
					$("#edit_BenefitID").val(data.list[0]["BENEFIT_ID"]);
				//	$("#edit_BannerImageEN").val(data.list[0]["BANNER_IMAGE_EN"]);
					$("#edit_BenefitEN").val(data.list[0]["BENEFIT_EN"]);
					$("#edit_Keyword").val(data.list[0]["KEYWORD"]);
					$("#edit_Param").val(data.list[0]["PARAM"]);
					$("#edit_offerLink").val(data.list[0]["OFFER_LINK"]);
					$("#edit_OfferType").val(data.list[0]["OFFER_TYPE"]);
					$("#edit_CustomerType").val(data.list[0]["CUST_TYPE"]);
				//	$("#edit_BannerImageID").val(data.list[0]["BANNER_IMAGE_ID"]);  
					
					} else{
						$("#msg").removeClass("text-success");
						$("#msg").addClass("text-danger");
						$("#msg").html("Record Not Found. Please try again.");
						}
	    			});
  			});

	$("#update").on('click',function(){
	  console.log($("#edit_BannerImageEN").val());
	  console.log($("#edit_BannerImageID").val());
  	  var form =$("#editOffer").get(0); 
  	  var fd = new FormData(form);
  	  		fd.append("edit_OfferID", $("#edit_OfferID").val());
  	  		fd.append("edit_PackageCode",$("#edit_PackageCode").val());
  	  		fd.append("edit_Tariff",$("#edit_Tariff").val());
			fd.append("edit_OfferNameID",$("#edit_OfferNameID").val());
			fd.append("edit_OfferNameEN",$("#edit_OfferNameEN").val());
			fd.append("edit_BenefitID",$("#edit_BenefitID").val());
			fd.append("edit_BannerImageEN",$("#edit_BannerImageEN").val());
			fd.append("edit_BenefitEN",$("#edit_BenefitEN").val());
			fd.append("edit_Keyword",$("#edit_Keyword").val());
			fd.append("edit_Param",$("#edit_Param").val());
			fd.append("edit_offerLink",$("#edit_offerLink").val());
			fd.append("edit_OfferType",$("#edit_OfferType").val());
			fd.append("edit_CustomerType",$("#edit_CustomerType").val());
			fd.append("edit_BannerImageID",$("#edit_BannerImageID").val());
		$.ajax({
			url : "EditOffer",
			data :fd,
			processData: false,  // tell jQuery not to process the data
          contentType: false,   // tell jQuery not to set contentType
			method : "POST"
			}).done(function(data) {
				if (null != data.Status && data.Status === "SUCCESS") {
					$("#msg").removeClass("text-danger");
					$("#msg").addClass("text-success");
					$("#msg").html("Record Updated Successfully."); 
					$("#edit_OfferID").val('');
					$("#edit_PackageCode").val('');
					$("#edit_Tariff").val('');
					$("#edit_OfferNameID").val('');
					$("#edit_OfferNameEN").val('');
					$("#edit_BenefitID").val('');
					//$("#edit_BannerImageEN").val('');
					$("#edit_BenefitEN").val('');
					$("#edit_Keyword").val('');
					$("#edit_Param").val('');
					$("#edit_offerLink").val('');
					$("#edit_OfferType").val('');
					$("#edit_CustomerType").val('');
				//	$("#edit_Banner_Image_ID").val('');  

				}else{
					$("#msg").removeClass("text-success");
					$("#msg").addClass("text-danger");
					$("#msg").html("Record Updation Fail. Please try again.");
				}
	    });
	 }); 
 
	$("#delete").on('click',function(){	

//form validation start		
		var offerID=$("#Offer_ID").val();
		var packageCode=$("#PACKAGE_Code").val();
		
 		if(offerID=="select"||offerID==null){
 			$("#msg").addClass("text-danger");
 			$("#msg").html("Please select an option.");
 		  document.getElementById("Offer_ID").style.borderColor = "red";
 			return false;
		}else if(packageCode=="select"||packageCode==null){
			$("#msg").addClass("text-danger");
 			$("#msg").html("Please select an option.");
	 	    document.getElementById("Offer_ID").style.borderColor = "";
	 	    document.getElementById("PACKAGE_Code").style.borderColor = "red";
	 	    return false;
		}else{
			$("#msg").html("");
	 	    document.getElementById("Offer_ID").style.borderColor = "";
	 	    document.getElementById("PACKAGE_Code").style.borderColor = "";
		}

 //form validation end	
 
 // hide all forms
 		 $("#showOfferForm").hide();
 	 	 $("#newOfferForm").hide();
 		 $("#editOfferForm").hide();
 		
 //ajax call for edit		 
		$.ajax({
			url : "getOffer?action=delete",
			data :{
				"Offer_ID": $("#Offer_ID").val(),
				"PACKAGE_Code": $("#PACKAGE_Code").val()
			},
			method : "POST"
		}).done(function(data) {
			if (null != data.Status && data.Status === "SUCCESS") {
				$("#Offer_ID option:selected").remove();
				$("#PACKAGE_Code option:selected").remove();
				$("#msg").removeClass("text-danger");
				$("#msg").addClass("text-success");
				$("#msg").html("Record Deleted Successfully."); 
			}else{
				$("#msg").removeClass("text-success");
				$("#msg").addClass("text-danger");
				$("#msg").html("Record Deletion Fail. Please try again.");
			}
	    });
	
		
  });
	
	$("#show").on('click',function(){	
		
		var offerID=$("#Offer_ID").val();
		var packageCode=$("#PACKAGE_Code").val();
		
 		if(offerID=="select"||offerID==null){
 			$("#msg").addClass("text-danger");
 			$("#msg").html("Please select an option.");
 		  document.getElementById("Offer_ID").style.borderColor = "red";
 			return false;
		}else if(packageCode=="select"||packageCode==null){
			$("#msg").addClass("text-danger");
 			$("#msg").html("Please select an option.");
	 	    document.getElementById("Offer_ID").style.borderColor = "";
	 	    document.getElementById("PACKAGE_Code").style.borderColor = "red";
	 	    return false;
		}else{
			$("#msg").html("");
	 	    document.getElementById("Offer_ID").style.borderColor = "";
	 	    document.getElementById("PACKAGE_Code").style.borderColor = "";
		}
		
		 $("#editOfferForm").hide();
		 $("#newOfferForm").hide();
		 $("#showOfferForm").show();
			$.ajax({
			url : "getOffer?action=show",
			data :{
				"Offer_ID": $("#Offer_ID").val(),
				"PACKAGE_Code": $("#PACKAGE_Code").val()
			},	method : "POST"
		}).done(function(data) {
			if (null != data.Status && data.Status === "SUCCESS") {
				$("#msg").removeClass("text-danger");
				$("#msg").addClass("text-success");
				$("#msg").html("Record Retrived Successfully."); 
				$("#show_OfferID").val(data.list[0]["OFFER_ID"]);
				$("#show_PackageCode").val(data.list[0]["PACKAGE_CODE"]);
				$("#show_Tariff").val(data.list[0]["TARIFF"]);
				$("#show_OfferNameID").val(data.list[0]["OFFER_NAME_ID"]);
				$("#show_OfferNameEN").val(data.list[0]["OFFER_NAME_EN"]);
				$("#show_BenefitID").val(data.list[0]["BENEFIT_ID"]);
				$("#show_BannerImageEN").attr('src', 'data:image/jpeg;base64,'+data.list[0]["BANNER_IMAGE_EN"]);
				$("#show_BenefitEN").val(data.list[0]["BENEFIT_EN"]);
				$("#show_Keyword").val(data.list[0]["KEYWORD"]);
				$("#show_Param").val(data.list[0]["PARAM"]);
				$("#show_offerLink").val(data.list[0]["OFFER_LINK"]);
				$("#show_OfferType").val(data.list[0]["OFFER_TYPE"]);
				$("#show_CustomerType").val(data.list[0]["CUST_TYPE"]);
				$("#show_Banner_Image_ID").attr('src', 'data:image/jpeg;base64,'+data.list[0]["BANNER_IMAGE_ID"]);
			
			
			}else{
				$("#msg").removeClass("text-success");
				$("#msg").addClass("text-danger");
				$("#msg").html("Record Not Found. Please try again.");
			}
	    });
	  });
	$("li").each(function(){
			$(this).removeClass("active");
		});
	$("#showallOffer").addClass("active");
});
</script>



<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Offer Information</title>
</head>
<body>
	<div class="container"><c:import url='/jsp/nav.jsp'></c:import>
		<div class="row">
			<div class="col-md-12">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Offer Information</h3>
						</div>
						<div class="panel-body">
						<div >
<!-- Select_Form  -->
						   <form class="" style="padding-top: 1em;" action="getOffer" method="post" name="offers">
						<div class="row">
							  	<div>
								&nbsp&nbsp <font size="4"> Status: <span id="msg"></span></p> </font>
								</div>
							  
							<select name="Offer_ID" id="Offer_ID" onchange="sendInfo()">
								<option>select</option>
								<c:forEach items="${list}" var="entry">
									<option>${entry["OFFER_ID"]}</option>
								</c:forEach>
							</select>
							 <select name="PACKAGE_Code" id="PACKAGE_Code">
							  
							 	<c:forEach items="${Ajexlist}" var="entry">
									<option>${entry["PACKAGE_CODE"]}</option>
								</c:forEach>
							</select> 
					         <input class="btn btn-default" type="button" name="action" value="New" id="new"/>
							 <input class="btn btn-default" type="button" name="action" value="Edit" id="edit"/>
							 <input class="btn btn-default" type="button" name="action" value="delete" id="delete"/>
							 <input class="btn btn-default" type="button" name="action" value="show" id="show"/>
				         </div>
						</form>
						</div>
<!-- New_Form -->
						<div id="newOfferForm" style="display:none">
								<form class="form-horizontal" id="newOffer" style="padding-top: 2em;" action="UploadOffer" method="post" enctype="multipart/form-data" >
                               
							<div class="row">
								<div class="col-md-6 ">
									<div class="form-group">
										<label for="OfferID" class="col-sm-3 control-label">Offer ID:</label>
										<div class="col-sm-6">
											<input type="text"  id="offer_id" class="form-control" name="OfferID" placeholder="OfferID">
										</div>
									</div>
									<div class="form-group">
										<label for="PackageCode" class="col-sm-3 control-label">Package Code:</label>
										<div class="col-sm-6">
											<input type="text" id="pack_code" class="form-control" name="PackageCode" placeholder="PackageCode">
										</div>
									</div>
									<div class="form-group">
										<label for="Tariff" class="col-sm-3 control-label">Tariff:</label>
										<div class="col-sm-6">
											<input type="text" id="tariff" class="form-control" name="Tariff" placeholder="Tariff">
										</div>
									</div>
									<div class="form-group">
										<label for="OfferNameID" class="col-sm-3 control-label">Offer Name ID:</label>
										<div class="col-sm-6">
											<input type="text" id="offer_Name_ID" class="form-control" name="OfferNameID" placeholder="OfferNameID">
										</div>
									</div>
									<div class="form-group">
										<label for="OfferNameEN" class="col-sm-3 control-label">Offer Name EN:</label>
										<div class="col-sm-6">
											<input type="text" id="offer_Name_EN" class="form-control" name="OfferNameEN" placeholder="OfferNameEN">
										</div>
									</div>
									<div class="form-group">
										<label for="BenefitID" class="col-sm-3 control-label">Benefit ID:</label>
										<div class="col-sm-6">
											<input type="text" id="banefit_ID" class="form-control" name="BenefitID" placeholder="BenefitID">
										</div>
									</div>
									<div class="form-group">
										<label for="BannerImageEN" class="col-sm-3 control-label">Banner EN:</label>
										<div class="col-sm-6">
											<input type="file" id="banner_Image_En" class="form-control" name="banner_Image_En" placeholder="BannerImageEN">
										</div>
									</div>
								</div>
								<div class="col-md-6 ">

									<div class="form-group">
										<label for="BenefitEN" class="col-sm-3 control-label">Benefit EN:</label>
										<div class="col-sm-6">
											<input type="text" id="banefit_EN" class="form-control" name="BenefitEN" placeholder="BenefitEN">
										</div>
									</div>
									<div class="form-group">
										<label for="Keyword" class="col-sm-3 control-label">Keyword :</label>
										<div class="col-sm-6">
											<input type="text" id="keyword" class="form-control" name="Keyword" placeholder="Keyword">
										</div>
									</div>
									<div class="form-group">
										<label for="Param" class="col-sm-3 control-label">Param :</label>
										<div class="col-sm-6">
											<input type="text" id="param" class="form-control" name="Param" placeholder="Param">
										</div>
									</div>
									<div class="form-group">
										<label for="offerLink" class="col-sm-3 control-label">offer Link:</label>
										<div class="col-sm-6">
											<input type="text" id="offer_Link" class="form-control" name="offerLink" placeholder="offerLink">
										</div>
									</div>
									<div class="form-group">
										<label for="OfferType" class="col-sm-3 control-label">Offer Type:</label>
										<div class="col-sm-6">
											<input type="text" id="offer_Type" class="form-control" name="OfferType" placeholder="OfferType">
										</div>
									</div>
									<div class="form-group">
										<label for="CustomerType" class="col-sm-3 control-label">Customer Type:</label>
										<div class="col-sm-6">
											<input type="text" id="customer_Type" class="form-control" name="CustomerType" placeholder="CustomerType">
										</div>
									</div>
									<div class="form-group">
										<label for="BannerImageID" class="col-sm-3 control-label">Banner ID:</label>
										<div class="col-sm-6">
											<input type="file" id="banner_Image_ID" class="form-control" name="banner_Image_ID" placeholder="banner_Image_ID">
										</div>
									</div>
									<br>
									<div class="form-group">
										<div class="col-sm-8" align="right">
											<input type="button" class="btn btn-default" class="form-control" value="submit" id="submit" /> &nbsp &nbsp &nbsp
											 <input type="reset" class="btn btn-default" class="form-control" value="cancel" />
										</div>
									</div>
								</div>
							</div>
						</form>
							
						
						</div>
						
<!-- Edit_Form -->
						<div id="editOfferForm" style="display:none">
							<form class="form-horizontal" id="editOffer"  style="padding-top: 2em;" action="EditOffer" method="post" enctype="multipart/form-data" modelAttribute="PackageInfor">
 												
						     <div class="row">
								<div class="col-md-6 ">
									<div class="form-group">
										<label for="OfferID" class="col-sm-3 control-label">Offer ID:</label>
										<div class="col-sm-6">
											<input type="text" id="edit_OfferID" class="form-control" name="OfferID" placeholder="OfferID" value='' disabled>
										</div>
									</div>
									<div class="form-group">
										<label for="PackageCode" class="col-sm-3 control-label">Package Code:</label>
										<div class="col-sm-6">
											<input type="text" id="edit_PackageCode" class="form-control" name="PackageCode" placeholder="PackageCode" value='' disabled>
										</div>
									</div>
									<div class="form-group">
										<label for="Tariff" class="col-sm-3 control-label">Tariff:</label>
										<div class="col-sm-6">
											<input type="text" id="edit_Tariff" class="form-control" name="Tariff" placeholder="Tariff" value=''>
										</div>
									</div>
									<div class="form-group">
										<label for="OfferNameID" class="col-sm-3 control-label">Offer Name ID:</label>
										<div class="col-sm-6">
											<input type="text" id="edit_OfferNameID" class="form-control" name="OfferNameID" placeholder="OfferNameID" value=''>
										</div>
									</div>
									<div class="form-group">
										<label for="OfferNameEN" class="col-sm-3 control-label">Offer Name EN:</label>
										<div class="col-sm-6">
											<input type="text" id="edit_OfferNameEN" class="form-control" name="OfferNameEN" placeholder="OfferNameEN" value=''>
										</div>
									</div>
									<div class="form-group">
										<label for="BenefitID" class="col-sm-3 control-label">Benefit ID:</label>
										<div class="col-sm-6">
											<input type="text" id="edit_BenefitID" class="form-control" name="BenefitID" placeholder="BenefitID" value=''>
										</div>
									</div>
									<div class="form-group">
										<label for="BannerImageEN" class="col-sm-3 control-label">Banner EN:</label>
										<div class="col-sm-6">
											<input type="file" id="edit_BannerImageEN" class="form-control" name="edit_BannerImageEN"  placeholder="BannerImageEN" value=''>
										</div>
									</div>
								</div>
								<div class="col-md-6 ">
									<div class="form-group">
										<label for="BenefitEN" class="col-sm-3 control-label">Benefit EN:</label>
										<div class="col-sm-6">
											<input type="text" id="edit_BenefitEN" class="form-control" name="BenefitEN" placeholder="BenefitEN" value=''>
										</div>
									</div>
									<div class="form-group">
										<label for="Keyword" class="col-sm-3 control-label">Keyword :</label>
										<div class="col-sm-6">
											<input type="text" id="edit_Keyword" class="form-control" name="Keyword" placeholder="Keyword" value=''>
										</div>
									</div>
									<div class="form-group">
										<label for="Param" class="col-sm-3 control-label">Param :</label>
										<div class="col-sm-6">
											<input type="text" id="edit_Param" class="form-control" name="Param" placeholder="Param" value=''>
										</div>
									</div>
									<div class="form-group">
										<label for="offerLink" class="col-sm-3 control-label">offer Link:</label>
										<div class="col-sm-6">
											<input type="text" id="edit_offerLink" class="form-control" name="offerLink" placeholder="offerLink" value=''>
										</div>
									</div>
									<div class="form-group">
										<label for="OfferType" class="col-sm-3 control-label">Offer Type:</label>
										<div class="col-sm-6">
											<input type="text" id="edit_OfferType" class="form-control" name="OfferType" placeholder="OfferType" value=''>
										</div>
									</div>
									<div class="form-group">
										<label for="CustomerType" class="col-sm-3 control-label">Customer Type:</label>
										<div class="col-sm-6">
											<input type="text" id="edit_CustomerType" class="form-control" name="CustomerType" placeholder="CustomerType" value=''>
										</div>
									</div>
									<div class="form-group">
										<label for="edit_Banner_Image_ID"  class="col-sm-3 control-label">Banner ID:</label>
										<div class="col-sm-6">
											<input type="file" id="edit_BannerImageID" class="form-control" name="edit_BannerImageID" placeholder="BannerImageID" value=''>
										</div>
									</div>
									<br>
									<div class="form-group">
										<div class="col-sm-8" align="right">
											<input type="button" class="btn btn-default" class="form-control" value="update"  id="update"/> &nbsp &nbsp &nbsp
											 <input type="reset" class="btn btn-default" class="form-control" value="cancel" />
										</div>
									</div>
								</div>
							</div>
						</form>
						</div>
<!-- Show_Form -->
						<div id="showOfferForm" style="display:none">
											<form class="form-horizontal" style="padding-top: 2em;" action="EditOffer" method="post" enctype="multipart/form-data" modelAttribute="PackageInfor">
							<div>
                               		<c:out value="${msg}" escapeXml="false" />
                               </div>
							<div class="row">
								<div class="col-md-6 ">
									<div class="form-group">
										<label for="OfferID" class="col-sm-3 control-label">Offer ID:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="show_OfferID" name="OfferID" placeholder="OfferID" value='${list[0]["OFFER_ID"]}' disabled>
										</div>
									</div>
									<div class="form-group">
										<label for="PackageCode" class="col-sm-3 control-label">Package Code:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="show_PackageCode" name="PackageCode" placeholder="PackageCode" value='${list[0]["PACKAGE_CODE"]}' disabled>
										</div>
									</div>
									<div class="form-group">
										<label for="Tariff" class="col-sm-3 control-label">Tariff:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="show_Tariff" name="Tariff" placeholder="Tariff" value='${list[0]["TARIFF"]}' disabled>
										</div>
									</div>
									<div class="form-group">
										<label for="OfferNameID" class="col-sm-3 control-label">Offer Name ID:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="show_OfferNameID" name="OfferNameID" placeholder="OfferNameID" value='${list[0]["OFFER_NAME_ID"]}' disabled>
										</div>
									</div>
									<div class="form-group">
										<label for="OfferNameEN" class="col-sm-3 control-label">Offer Name EN:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="show_OfferNameEN" name="OfferNameEN" placeholder="OfferNameEN" value='${list[0]["OFFER_NAME_EN"]}' disabled>
										</div>
									</div>
									<div class="form-group">
										<label for="BenefitID" class="col-sm-3 control-label">Benefit ID:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="show_BenefitID" name="BenefitID" placeholder="BenefitID" value='${list[0]["BENEFIT_ID"]}' disabled>
										</div>
									</div>
									<div class="form-group">
										<label for="BannerImageEN" class="col-sm-3 control-label">Banner EN:</label>
										<div class="col-sm-6">
											<img class="img-rounded" id="show_BannerImageEN"  src='' height='200' width='250' border='5' >
										</div>
									</div>
								</div>
								<div class="col-md-6 ">
									<div class="form-group">
										<label for="BenefitEN" class="col-sm-3 control-label">Benefit EN:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="show_BenefitEN" name="BenefitEN" placeholder="BenefitEN" value='${list[0]["BENEFIT_EN"]}' disabled>
										</div>
									</div>
									<div class="form-group">
										<label for="Keyword" class="col-sm-3 control-label">Keyword :</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="show_Keyword" name="Keyword" placeholder="Keyword" value='${list[0]["KEYWORD"]}' disabled>
										</div>
									</div>
									<div class="form-group">
										<label for="Param" class="col-sm-3 control-label">Param :</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="show_Param" name="Param" placeholder="Param" value='${list[0]["PARAM"]}' disabled>
										</div>
									</div>
									<div class="form-group">
										<label for="offerLink" class="col-sm-3 control-label">offer Link:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="show_offerLink" name="offerLink" placeholder="offerLink" value='${list[0]["OFFER_LINK"]}' disabled>
										</div>
									</div>
									<div class="form-group">
										<label for="OfferType" class="col-sm-3 control-label">Offer Type:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="show_OfferType" name="OfferType" placeholder="OfferType" value='${list[0]["OFFER_TYPE"]}' disabled>
										</div>
									</div>
									<div class="form-group">
										<label for="CustomerType" class="col-sm-3 control-label">Customer Type:</label>
										<div class="col-sm-6">
											<input type="text" class="form-control" id="show_CustomerType" name="CustomerType" placeholder="CustomerType" value='${list[0]["CUST_TYPE"]}' disabled>
										</div>
									</div>
									<div class="form-group">
										<label for="BannerImageID" class="col-sm-3 control-label">Banner ID:</label>
										<div class="col-sm-6">
											<img class="img-rounded" id="show_Banner_Image_ID" src='' height='200' width='250' border='5' >
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
	</div>
</body>
</html>