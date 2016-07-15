
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



$(document).ready(function(){
		// $('#submit').validator()
	      $("#submit").on('click',function(){
	    	//  var Package_Code=document.NewSspPackage.PackageCode.value; 
	    	var Package_Code=$("#PackageCode").val();
	    	if(Package_Code==""||Package_Code==null){
	    	document.getElementById("PackageCode").style.borderColor = "red";
			return false;
	    	}
    		$.ajax({
  				url : "NewSspPackage",
  				data : {
					"PackageCode" : $("#PackageCode").val(),
					"Keyword" : $("#Keyword").val(),
					"ShortCode" : $("#ShortCode").val(),
				},
  				method : "POST"
  			}).done(function(data) {
  				if (null != data.Status && data.Status === "SUCCESS") {
  					document.getElementById("PackageCode").style.borderColor = "";
  					$("#PACK_CODE").append('<option value='+$("#PackageCode").val()+'>'+$("#PackageCode").val()+'</option>');
  					$("#msg").removeClass("text-danger");
					$("#msg").addClass("text-success");
					$("#msg").html("Record Inserted Successfully."); 
					$("#PackageCode").val('');
					$("#Keyword").val('');
					$("#ShortCode").val('');
				}else{
					$("#msg").removeClass("text-success");
					$("#msg").addClass("text-danger");
					$("#msg").html("Record Insertion fail. Please try again.");
  				}
		    });
     	 }); 
      
      $("#new").on('click',function(){
    	  $("#editSSPPage").hide();
    	  $("#showSSPPage").hide();
    	  $("#newSSPPage").show();
    		$.ajax({
    			url : "getSSPOffer?action=new",
  				method : "POST"

  			}).done(function(data) {
  				if (null != data.Status && data.Status === "SUCCESS") {
  					$("#msg").removeClass("text-danger");
					$("#msg").addClass("text-success");
					$("#msg").html("New Form "); 
   					}
  				
		    });
     	 }); 
  	
      
  		$("#edit").on('click',function(){

/*select box validation .*/
 			var Package_Code=$("#PACK_CODE").val();
	 		if(Package_Code=="select"||Package_Code==null){
	 			$("#msg").addClass("text-danger");
	 			$("#msg").html("Please select an option.");
	 		  document.getElementById("PACK_CODE").style.borderColor = "red";
     			return false;
    		}else{
    			 $("#msg").html("");
   	 	    	  document.getElementById("PACK_CODE").style.borderColor = "";
    		}
  			 $("#newSSPPage").hide();
	   		 $("#showSSPPage").hide();
			 $("#editSSPPage").show();
    		 $.ajax({
  				url : "getSSPOffer?action=edit",
  				data :{
  					"PACK_CODE": $("#PACK_CODE").val()
  				},
  				method : "POST"
  			}).success(function(data){
  				if (null != data.Status && data.Status === "SUCCESS") {
  					$("#edit_pack_code").val(data.list[0]["PACK_CODE"]);
  					$("#edit_keyword").val(data.list[0]["KEYWORD"]);
  					$("#edit_shortcode").val(data.list[0]["SHORT_CODE"]);
  							
  				} else {
  					$("#msg").removeClass("text-success");
					$("#msg").addClass("text-danger");
					$("#msg").html("Record Not Found.");

  				}			
		    });
	    });
  		
  		
  		 $("#update").on('click',function(){
     		$.ajax({
   				url : "EditSSPOffer",
   				data : {
 					"PACK_CODE" : $("#edit_pack_code").val(),
 					"Keyword" : $("#edit_keyword").val(),
 					"ShortCode" : $("#edit_shortcode").val(),
 				},
   				method : "POST"
   			}).done(function(data) {
   				if (null != data.Status && data.Status === "SUCCESS") {
   					$("#msg").removeClass("text-danger");
					$("#msg").addClass("text-success");
					$("#msg").html("Record Updated Successfully."); 
					$("#edit_pack_code").val('');
  					$("#edit_keyword").val('');
  					$("#edit_shortcode").val('');
  			
   				     }
   					else {
   					$("#msg").removeClass("text-success");
					$("#msg").addClass("text-danger");
					$("#msg").html("Record Updation Fail. Please try again .");	
					}
 		    });
      	 }); 
       
     
  		$("#delete").on('click',function(){	
  			var Package_Code=$("#PACK_CODE").val();
	 		if(Package_Code=="select"||Package_Code==null){
	 			$("#msg").addClass("text-danger");
	 			$("#msg").html("Please select an option.");
	 		  document.getElementById("PACK_CODE").style.borderColor = "red";
     			return false;
    		}else{
    			 $("#msg").html("");
   	 	    	  document.getElementById("PACK_CODE").style.borderColor = "";
    		}
  			
	 		 $("#editSSPPage").hide();
	    	 $("#showSSPPage").hide();
	    	 $("#newSSPPage").hide();
	    	
  			$.ajax({
  				url : "getSSPOffer?action=delete",
  				data :{
  					"PACK_CODE": $("#PACK_CODE").val()
  				},
  				method : "POST"
  			}).done(function(data) {
  				if (null != data.Status && data.Status === "SUCCESS") {
  					$("#PACK_CODE option:selected").remove();
  					$("#msg").removeClass("text-danger");
					$("#msg").addClass("text-success");
					$("#msg").html("Record Deteled Successfully."); 
   					}
   					else {
   					$("#msg").removeClass("text-success");
					$("#msg").addClass("text-danger");
					$("#msg").html("Record Deletion Fail. Please try again .");	
					}
		    });
 		
 	 		
	    });
  		
  		$("#show").on('click',function(){	
  			var Package_Code=$("#PACK_CODE").val();
	 		if(Package_Code=="select"||Package_Code==null){
	 			$("#msg").addClass("text-danger");
	 			$("#msg").html("Please select an option.");
	 		  document.getElementById("PACK_CODE").style.borderColor = "red";
     			return false;
    		}else{
    			 $("#msg").html("");
   	 	    	  document.getElementById("PACK_CODE").style.borderColor = "";
    		}
  			 $("#newSSPPage").hide();
  			 $("#editSSPPage").hide();
  			 $("#showSSPPage").show();
  			 $.ajax({
  				url : "getSSPOffer?action=show",
  				data :{
  					"PACK_CODE": $("#PACK_CODE").val()
  				},
  				method : "POST"
  			}).done(function(data) {
  				if (null != data.Status && data.Status === "SUCCESS") {
  					console.log(data);
  					$("#msg").removeClass("text-danger");
					$("#msg").addClass("text-success");
					$("#msg").html("Record Retrived Successfully."); 
   					$("#show_pack_code").val(data.list[0]["PACK_CODE"]);
  					$("#show_keyword").val(data.list[0]["KEYWORD"]);
  					$("#show_short_code").val(data.list[0]["SHORT_CODE"]);
  				} else {
  					$("#msg").removeClass("text-success");
					$("#msg").addClass("text-danger");
					$("#msg").html("Record Not Found.");	
			
  				}
		    });
 		
 	 		
	    });
  		$("li").each(function(){
  			$(this).removeClass("active");
  		});
		$("#showallOfferSSP").addClass("active");
    	  
});
</script>

<style type="text/css">     
    select {
        width:200px;
    }
</style>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>ALL SSP Offers</title>
</head>
<body>

	<div class="container"><c:import url='/jsp/nav.jsp'></c:import>
		<div class="row">
			<div class="col-md-12">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">SSP OFFER Information</h3>
						</div>
						<div class="panel-body">
						<div>
<!-- Select_Form  -->						
						<form class="" style="padding-top: 1em;" action="getSSPOffer" method="post" id="SSP_Offers" name="SSP_Offers">
						<div class="row">
						       <div>
							&nbsp&nbsp <font size="4"> Status: <span id="msg"></span></p> </font>
								</div>
								<div class="col-md-8 ">
								
						<select name="PACK_CODE" id="PACK_CODE">
									<option>select</option>
								<c:forEach items="${list}" var="entry">
									<option>${entry["PACK_CODE"]}</option>
								</c:forEach>
						</select> 
							<input class="btn btn-default" type="button" name="action" value="New" id="new"> 
							<input class="btn btn-default" type="button" name="action" value="edit" id="edit">
						    <input class="btn btn-default" type="button" name="action" value="delete" id="delete">
						    <input class="btn btn-default" type="button" name="action" value="show" id="show">
						    </div>
						   </div>
						 </form> 
						 </div>
<!-- New_Form -->						 
						 <div id="newSSPPage"  style="display:none" >
						 <form class="form-horizontal" action="NewSspPackage" name="NewSspPackage" method="post" style="padding-top: 2em;" data-toggle="validator" role="form" >
							<div class="row">
								<div class="col-md-8 ">
									<div class="form-group">
										<label for="PackageCode" class="col-sm-3 control-label">Package Code:</label>
										<div class="col-sm-8">
											<input id="PackageCode"  type="text" class="form-control" name="PackageCode" value='' placeholder="PackageCode" class="required">
										</div>
										
									</div>
									<div class="form-group">
										<label for="Keyword" class="col-sm-3 control-label">Keyword:</label>
										<div class="col-sm-8">
											<input type="text" id="Keyword" class="form-control" name="Keyword" value='' placeholder="Keyword">
										</div>
									</div>
									<div class="form-group">
										<label for="ShortCode" class="col-sm-3 control-label">Short Code:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" id="ShortCode" name="ShortCode" value='' placeholder="ShortCode">
										</div>
									</div>
								
								</div>
								<div class="col-md-4 "></div>
								<br> <br> <br>
								<div class="form-group">
									<div class="col-sm-8" align="center">
										<input type="button" class="btn btn-default" class="form-control" id="submit" value="submit"  />
										 &nbsp &nbsp &nbsp
										 <input type="reset" class="btn btn-default" class="form-control"  value="cancel" />
									</div>
								</div>
							</div>
						</form>
						 </div>
<!-- Edit_Form -->		 
						 <div id="editSSPPage" style="display:none">
						 <form class="form-horizontal" action="EditSSPOffer" method="post"  style="padding-top: 2em;">
							<div class="row">
								<div class="col-md-8 ">
									<div class="form-group">
										<label for="PACK_CODE" class="col-sm-3 control-label">PACK_CODE:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" id="edit_pack_code" name="PACK_CODE" placeholder="PACK_CODE" value='' disabled>
										</div>
									</div>
									<div class="form-group">
										<label for="Keyword" class="col-sm-3 control-label">Keyword:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" id="edit_keyword" name="Keyword" placeholder="Keyword" value=''>
										</div>
									</div>
									<div class="form-group">
										<label for="ShortCode" class="col-sm-3 control-label">Short Code:</label> 
										<div class="col-sm-8">
											<input type="text" class="form-control" name="ShortCode" id="edit_shortcode" placeholder="ShortCode" value=''>
										</div>
									</div>
									<br> 
								</div>
								<div class="col-md-4 "></div>
								<br> <br> <br>
								<div class="form-group">
									<div class="col-sm-8" align="center">
										<input type="button" class="btn btn-default" class="form-control"  value="update" id="update" />
										 &nbsp &nbsp &nbsp
									     <input type="reset" class="btn btn-default" class="form-control"  value="cancel" />
									</div>
								</div>
							</div>
						</form>
						 </div>
<!-- Show_Form -->				
						 <div  id="showSSPPage" style="display:none">
						 <form class="form-horizontal" action="EditSSPOffer" method="post"  style="padding-top: 2em;">
							<div class="row">
								<div class="col-md-8 ">
								<div class="form-group" align="center">
										<label for="PACK_CODE" class="col-sm-3 control-label"></label>
										<div class="col-sm-8" align="center"> 
											<c:out value="${msg}" escapeXml="false" />
										</div>
									</div>
										<div class="form-group">
										<label for="PACK_CODE" class="col-sm-3 control-label">PACK_CODE:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" id="show_pack_code" name="PACK_CODE" placeholder="PACK_CODE" value='' disabled>
										</div>
									</div>
									<div class="form-group">
										<label for="Keyword" class="col-sm-3 control-label">Keyword:</label>
										<div class="col-sm-8">
											<input type="text" class="form-control" id="show_keyword" name="Keyword" placeholder="Keyword" value='' disabled>
										</div>
									</div>
									<div class="form-group">
										<label for="ShortCode" class="col-sm-3 control-label">Short Code:</label>
										<div class="col-sm-8" >
											<input type="text" class="form-control" id="show_short_code" name="ShortCode" placeholder="ShortCode" value='' disabled>
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