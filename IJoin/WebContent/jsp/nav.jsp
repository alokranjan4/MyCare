<div class="row">
<nav class="navbar navbar-default">
  <div class="container-fluid">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="home">Indosat</a>
    </div>

    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
      <ul class="nav navbar-nav">
        <li class="active"><a href="order">Order Status</a></li>
        <li id="showallPackage1" class="dropdown">
          <a href="showallPackage1" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Pack Category <span class="caret"></span></a>
          <ul class="dropdown-menu">
			<li><a href="showallPackage1">View</a></li>
        <!--     <li><a href="newPackCat">New</a></li>
			<li role="separator" class="divider"></li>
            <li><a href="getPackage1?action=edit">Edit</a></li>
            <li><a href="getPackage1?action=delete">Delete</a></li>  -->
          </ul>
        </li>
		<li id="showallPackage2" class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Pack Details <span class="caret"></span></a>
          <ul class="dropdown-menu">
			<li><a href="showallPackage2">View</a></li>
            <!-- <li><a href="getPackage2?action=new">New</a></li>
			<li role="separator" class="divider"></li>
            <li><a href="getPackage2?action=edit">Edit</a></li>
            <li><a href="getPackage2?action=delete">Delete</a></li> -->
          </ul>
        </li>
		<li id="showallOfferSSP" class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">SSP Pack <span class="caret"></span></a>
          <ul class="dropdown-menu">
			<li><a href="showallOfferSSP">View</a></li>
             <li><a href="showallOfferSSP" id="link_new_ssp_Form">New</a></li>
			<!--<li role="separator" class="divider"></li>
            <li><a href="getSSPOffer?action=edit">Edit</a></li>
            <li><a href="getSSPOffer?action=delete">Delete</a></li> -->
          </ul>
        </li>
		<li id="showallOffer" class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Offer <span class="caret"></span></a>
          <ul class="dropdown-menu">
			<li><a href="showallOffer">View</a></li>
            <!-- <li><a href="getOffer?action=new">New</a></li>
			<li role="separator" class="divider"></li>
            <li><a href="getOffer?action=edit">Edit</a></li>
            <li><a href="getOffer?action=delete">Delete</a></li> -->
          </ul>
        </li>
		<li id="showallStore" class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Store <span class="caret"></span></a>
          <ul class="dropdown-menu">
			<li><a href="showallStore">View</a></li>
            <!-- <li><a href="getStore?action=new">New</a></li>
			<li role="separator" class="divider"></li>
            <li><a href="getStore?action=edit">Edit</a></li>
            <li><a href="getStore?action=delete">Delete</a></li> -->
          </ul>
        </li>
        <li class="dropdown">
          <a href="#" class="dropdown-toggle nav navbar-nav navbar-right" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><%=session.getAttribute("LoginID")%><span class="caret"></span></a>
          <ul class="dropdown-menu ">
			<li><a href="changePassword">Change Password</a></li>
			<li><a href="logout">LogOff</a></li>
			
          </ul>
        </li>
      </ul>
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>
</div>